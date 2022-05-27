package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
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
    public static final int INITIAL_ZOOM_LEVEL = 12;
    public static final int INITIAL_X_IMAGE = 543200;
    public static final int INITIAL_Y_IMAGE = 370650;
    public static final int MAX_STEP_LENGTH = 5;
    private BorderPane mainPane;
    private SplitPane mapAndProfilePane = new SplitPane();
    public JaVelo() {
        this.mainPane = new BorderPane();
    }

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        CityBikeCF costFunction = new CityBikeCF(graph);
        String tileServerHost = "tile.openstreetmap.org";
        Path cacheBasePath = Path.of("osm-cache");

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        // Carte affichée au démarrage
        MapViewParameters mapViewParameters =
                new MapViewParameters(INITIAL_ZOOM_LEVEL, INITIAL_X_IMAGE, INITIAL_Y_IMAGE);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        Consumer<String> errorConsumer = new ErrorConsumer();

        RouteBean routeBean =
                new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));

        ElevationProfile profile = ElevationProfileComputer
                .elevationProfile(routeBean.route(), MAX_STEP_LENGTH);

        ObjectProperty<ElevationProfile> profileProperty =
                new SimpleObjectProperty<>(profile);

        DoubleProperty highlightProperty = new SimpleDoubleProperty();

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(profileProperty,
                        highlightProperty);
        highlightProperty.bind(
                elevationProfileManager.mousePositionOnProfileProperty());

        RouteManager routeManager =
                new RouteManager(routeBean, mapViewParametersP);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.waypoints(),
                        errorConsumer);

        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        AnnotatedMapManager annotatedMapManager =
                new AnnotatedMapManager(graph,
                        tileManager,
                        routeBean,
                        errorConsumer);

        mapAndProfilePane.setOrientation(Orientation.VERTICAL);
        // Lorsqu'un itinéraire existe
        if (routeBean.route() != null) {
            mapAndProfilePane
                    .getChildrenUnmodifiable()
                    .setAll(annotatedMapManager.pane(), elevationProfileManager.pane());
            SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
        }
        else
            mapAndProfilePane
                    .getChildrenUnmodifiable()
                    .setAll(annotatedMapManager.pane());

        mainPane.setCenter(mapAndProfilePane);
        MenuItem menuItem = new MenuItem("Exporter GPX");
        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());
        Menu menu = new Menu("Fichier");
        ElevationProfile elevationProfile =
                ElevationProfileComputer.elevationProfile(routeBean.route(), 5f);
        menu.setOnAction(e -> {
            GpxGenerator.createGpx(routeBean.route(), elevationProfile);
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), elevationProfile);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
        menu.getItems().add(menuItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        mainPane.setTop(menuBar);

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(new Scene(mainPane));
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }

}
