package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

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
    private final Pane pane;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersP,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParametersP = mapViewParametersP;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
        this.pane = drawPane();
    }

    public Pane pane() {
        return pane;
    }

    private Pane drawPane() {
        Pane pane = new Pane();
        MapViewParameters mvp = mapViewParametersP.get();

        for (int i = 0; i < waypoints.size(); i++) {
            SVGPath exteriorMarker = new SVGPath();
            SVGPath interiorMarker = new SVGPath();
            exteriorMarker.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            interiorMarker.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
            interiorMarker.setFill(Color.WHITE);
            Waypoint waypoint = waypoints.get(i);
            Group group = new Group();
            group.getChildren().addAll(exteriorMarker, interiorMarker);
            group.getStyleClass().add("map.pin");
            if (i != 0 && i != waypoints.size()) {
                group.getStyleClass().add("map.middle");
                exteriorMarker.setFill(Color.DARKTURQUOISE);
            } else {
                if (i == 0) {
                    group.getStyleClass().add("map.first");
                    exteriorMarker.setFill(Color.LIMEGREEN);
                }
                if (i == waypoints.size()) {
                    group.getStyleClass().add("map.last");
                    exteriorMarker.setFill(Color.ORANGERED);
                }
            }
            PointCh pointCh = waypoint.p();
            System.out.println(pointCh);
            PointWebMercator p = PointWebMercator.ofPointCh(pointCh);
            double finalX = p.x();
            double finalY = p.y();
            System.out.println(finalX);
            System.out.println(finalY);
            group.setLayoutX(finalX - group.getLayoutBounds().getMinX());
            group.setLayoutY(finalY - group.getLayoutBounds().getMinY());
            pane.getChildren().add(group);
        }
        System.out.printf("Il y a %d marqueurs", pane.getChildren().size());
        return pane;
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
        waypoints.add(new Waypoint(p, closestNodeId));
    }
}