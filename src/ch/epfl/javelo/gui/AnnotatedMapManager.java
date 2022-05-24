package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Gère l'affichage de la carte "annotée", càd le fond de la carte au-dessus
 * duquel sont superposés l'itinéraire et les points de passage.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class AnnotatedMapManager {
    private final Graph graph;
    private final TileManager tileManager;
    private final RouteBean routeBean;
    private final Consumer<String> errorConsumer;
    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;


    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.errorConsumer = errorConsumer;

        MapViewParameters initMVP = new MapViewParameters(12, 543200, 370650 );
        ObjectProperty<MapViewParameters> mapViewParametersP = new SimpleObjectProperty<>(initMVP);
        waypointsManager = new WaypointsManager(graph, mapViewParametersP, routeBean.waypoints(), errorConsumer);

        baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);

        routeManager = new RouteManager(routeBean, mapViewParametersP);
    }



}
