package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Consumer;

/**
 * Gère l'affichage et l'interaction avec les points de passage.
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
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
    }

    public Pane pane() {
        return null;
    }

    public void addWayPoint() {

    }
}
