package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Classe principale de l'application.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class JaVelo extends Application {
    public static final int MIN_WIDTH = 800;
    public static final int MIN_HEIGHT = 600;
    private final BorderPane mainPane = new BorderPane();
    private final SplitPane mapAndProfilePane = new SplitPane();

    /**
     * Méthode principale de l'application
     * @param args les arguments passés à l'application
     */
    public static void main(String[] args) {launch(args);}

    /**
     * Méthode de démarrage de l'application javelo
     * @param primaryStage la scène principale de l'application
     * @throws Exception une exception, de type IOException ou UncheckedIOException
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        String tileServerHost = "tile.openstreetmap.org";
        Path cacheBasePath = Path.of("osm-cache");

        // Création du gestionnaire de tuiles
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        // Création du gestionnaire d'erreurs
        ErrorManager errorManager = new ErrorManager();

        Consumer<String> errorConsumer = errorManager::displayError;

        // Création du bean JavaFX de l'itinéraire
        RouteBean routeBean =
                new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));

        // Création du gestionnaire de la carte annotée
        AnnotatedMapManager annotatedMapManager =
                new AnnotatedMapManager(graph,
                        tileManager,
                        routeBean,
                        errorConsumer);
        // Propriété contenant la position mise en évidence le long du profil
        DoubleProperty highlightedPositionOnProfile = new SimpleDoubleProperty();

        //Création du gestionnaire du profil en long de l'itinéraire
        ElevationProfileManager elevationProfileManager
                = new ElevationProfileManager(routeBean.elevationProfileProperty(),
                highlightedPositionOnProfile);

        ReadOnlyDoubleProperty profP =
                elevationProfileManager.mousePositionOnProfileProperty();

        // Création d'un lien entre la  ropriété contenant la position mise en
        // évidence le long du profil et la propriété contenant la position mise
        // en évidence le long de l'itinéraire
        highlightedPositionOnProfile.bind(routeBean.highlightedPositionProperty());

        ReadOnlyDoubleProperty mapP = annotatedMapManager.mousePositionOnRouteProperty();

        // Création d'un lien JavaFX entre la propriété contenant la position de
        // l'itinéraire mise en évidence et les propriétés contenant la position
        routeBean.highlightedPositionProperty().bind(
                Bindings.when(
                            mapP.greaterThanOrEqualTo(0d))
                        .then(mapP)
                        .otherwise(profP));

        mainPane.getStylesheets().add("map.css");

        mapAndProfilePane.getItems().add(0, annotatedMapManager.pane());
        mapAndProfilePane.setOrientation(Orientation.VERTICAL);
        mainPane.setCenter(new StackPane(mapAndProfilePane, errorManager.pane()));

        // Création du menu d'exportation du fichier GPX correspondant à l'itinéraire
        Menu menu = new Menu("Fichier");
        MenuItem menuItem = new MenuItem("Exporter GPX");
        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());
        menu.getItems().add(menuItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        mainPane.setTop(menuBar);
        // Création du gestionnaire d'évènements du menu
        menu.setOnAction(e -> {
            try {
                GpxGenerator.createGpx(routeBean.route(), routeBean.elevationProfileProperty().get());
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), routeBean.elevationProfileProperty().get());
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });

        // Création d'un auditeur JavaFX sur la propriété de routeBean contenant
        // le profil se chargeant de l'affichage du panneau contenant le profil
        routeBean.elevationProfileProperty().addListener((p, oldP, newP) -> {
            if (newP != null) {
                if (mapAndProfilePane.getItems().size() > 1 )
                    mapAndProfilePane.getItems().set(1, elevationProfileManager.pane());
                else
                    mapAndProfilePane.getItems().add(1, elevationProfileManager.pane());
            } else {
                if (mapAndProfilePane.getItems().size() > 1)
                    mapAndProfilePane.getItems().remove(1);
            }
        });

        // Paramétrage de la scène
        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}