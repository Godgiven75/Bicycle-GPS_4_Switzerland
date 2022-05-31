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


    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        String tileServerHost = "tile.openstreetmap.org";
        Path cacheBasePath = Path.of("osm-cache");

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        ErrorManager errorManager = new ErrorManager();

        Consumer<String> errorConsumer = errorManager::displayError;

        RouteBean routeBean =
                new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));

        AnnotatedMapManager annotatedMapManager =
                new AnnotatedMapManager(graph,
                        tileManager,
                        routeBean,
                        errorConsumer);
        DoubleProperty highlightedPositionOnProfile = new SimpleDoubleProperty();
        ElevationProfileManager elevationProfileManager
                = new ElevationProfileManager(
                routeBean.elevationProfileProperty(), highlightedPositionOnProfile);
        ReadOnlyDoubleProperty profP = elevationProfileManager.mousePositionOnProfileProperty();
        highlightedPositionOnProfile.bind(profP);
        ReadOnlyDoubleProperty mapP = annotatedMapManager.mousePositionOnRouteProperty();
        routeBean.highlightedPositionProperty().bind(
                Bindings.when(
                                mapP.greaterThanOrEqualTo(0))
                        .then(mapP)
                        .otherwise(profP));

        mainPane.getStylesheets().add("map.css");
        mapAndProfilePane.getItems().add(0, annotatedMapManager.pane());
        mapAndProfilePane.setOrientation(Orientation.VERTICAL);
        StackPane mapAndProfileAndErrorPane =
                new StackPane(mapAndProfilePane, errorManager.pane());
        mainPane.setCenter(mapAndProfileAndErrorPane);
        Menu menu = new Menu("Fichier");
        MenuItem menuItem = new MenuItem("Exporter GPX");
        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());
        menu.getItems().add(menuItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        mainPane.setTop(menuBar);
        menu.setOnAction(e -> {
            try {
                GpxGenerator.createGpx(routeBean.route(), routeBean.elevationProfileProperty().get());
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), routeBean.elevationProfileProperty().get());
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();


        routeBean.elevationProfileProperty().addListener((p, o, n) -> {
            if (n != null) {
                if (mapAndProfilePane.getItems().size() > 1 )
                    mapAndProfilePane.getItems().set(1, elevationProfileManager.pane());
                else
                    mapAndProfilePane.getItems().add(1, elevationProfileManager.pane());
            } else {
                if (mapAndProfilePane.getItems().size() > 1)
                    mapAndProfilePane.getItems().remove(1);
            }
        });


    }
}