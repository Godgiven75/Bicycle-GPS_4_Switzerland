package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
    public static final int MAX_STEP_LENGTH = 5;
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

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();

        routeBean.routeProperty().addListener((p, o, n) -> {
            if (n != null) {
                ElevationProfile profile = ElevationProfileComputer
                        .elevationProfile(n, MAX_STEP_LENGTH);

                ObjectProperty<ElevationProfile> profileProperty =
                        new SimpleObjectProperty<>(profile);

                DoubleProperty highlightedPositionOnProfileProperty =
                        new SimpleDoubleProperty();

                ElevationProfileManager elevationProfileManager =
                        new ElevationProfileManager(profileProperty,
                                highlightedPositionOnProfileProperty);

                highlightedPositionOnProfileProperty.bind(
                        elevationProfileManager.mousePositionOnProfileProperty());

                DoubleProperty mapP = annotatedMapManager.mousePositionOnRouteProperty();
                DoubleProperty profP = (DoubleProperty) elevationProfileManager.mousePositionOnProfileProperty();
                routeBean.highlightedPositionProperty().bind(Bindings.when(
                                mapP.greaterThan(0d))
                        .then(mapP)
                        .otherwise(profP));

                if (mapAndProfilePane.getItems().size() > 1)
                    mapAndProfilePane.getItems().set(1, elevationProfileManager.pane());
                else
                    mapAndProfilePane.getItems().add(1, elevationProfileManager.pane());

                menu.setOnAction(e -> {
                    GpxGenerator.createGpx(routeBean.route(), profile);
                    try {
                        GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), profile);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                });
            }
            else {
                if (mapAndProfilePane.getItems().size() > 1)
                    mapAndProfilePane.getItems().remove(1);
            }
        });
    }
}