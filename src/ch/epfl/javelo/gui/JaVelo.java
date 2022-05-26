package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Classe principale de l'application.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class JaVelo extends Application {
    private BorderPane mainPane;
    private SplitPane mapAndProfilePane;
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
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        //Consumer<String> errorConsumer = new ErrorConsumer();

        RouteBean routeBean =
                new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        routeBean.setHighlightedPositionP(1000);

        RouteManager routeManager =
                new RouteManager(routeBean, mapViewParametersP);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.waypoints(),
                        errorConsumer);

        mapAndProfilePane.setOrientation(Orientation.VERTICAL);
        //mapAndProfilePane.getChildren
        //StackPane stackPane = new StackPane()
        mainPane.setCenter(mapAndProfilePane);
        //mainPane.setTop();

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPane));
    }


}
