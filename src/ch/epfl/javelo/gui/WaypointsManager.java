package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

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

    /**
     * Prend les coordonnées x et y d'un point et ajoute un nouveau point de
     * passage au noeud du graphe qui en est le plus proche (dans un rayon de
     * 500m).
     *
     * @param x la coordonnée x du point
     * @param y la coordonnée y du point
     */
    public void addWayPoint(double x, double y) {
        PointCh p = (new PointWebMercator(x, y)).toPointCh();
        int closestNodeId = graph.nodeClosestTo(p, 500);
        if (closestNodeId == -1)
            errorConsumer.accept("Aucune route à proximité !");
    }
}
