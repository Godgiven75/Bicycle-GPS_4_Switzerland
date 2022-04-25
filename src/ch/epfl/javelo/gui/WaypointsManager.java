package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class WaypointsManager {
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersP,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParametersP = mapViewParametersP;
        this.waypoints = (ObservableList<Waypoint>) List.copyOf(waypoints);
        this.errorConsumer = errorConsumer;
    }
}
