package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
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
    private final ObjectProperty<Point2D> mousePositionP;
    private final ObjectProperty<Point2D> previousMarkerPositionP;
    private final static double SEARCH_DISTANCE = 500d;


    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersP,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParametersP = mapViewParametersP;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
        this.previousMarkerPositionP = new SimpleObjectProperty<>(new Point2D(0, 0));
        this.mousePositionP = new SimpleObjectProperty<>(new Point2D(0, 0));
        this.pane = new Pane();
        pane.setPickOnBounds(false);
        createMarkers();
        positionMarkers();
        addListeners();
        addMouseEventsManager();
    }

    private void addMouseEventsManager() {
        MapViewParameters mvp = mapViewParametersP.get();

        for (Node group : pane.getChildren()) {
            group.setOnMousePressed(m -> {
                int markerIndex = pane.getChildren().indexOf(group);
                Point2D mousePosition = new Point2D(m.getX(), m.getY());
                mousePositionP.set(mousePosition);
                PointWebMercator pwm = PointWebMercator.ofPointCh(waypoints.get(markerIndex).p());
                previousMarkerPositionP.set(new Point2D(mvp.viewX(pwm), mvp.viewY(pwm)));
                if (m.isStillSincePress()) {
                    pane.getChildren().remove(group);
                    waypoints.remove(markerIndex);
                }
            });

            group.setOnMouseDragged(m -> {
                group.setLayoutX(group.getLayoutX() + m.getX() - mousePositionP.get().getX());
                group.setLayoutY(group.getLayoutY() + m.getY() - mousePositionP.get().getY());
            });

            group.setOnMouseReleased(m -> {
                if(!m.isStillSincePress()) {
                    int markerIndex = pane.getChildren().indexOf(group);
                    PointWebMercator mousePWM = mvp.pointAt(
                            m.getX() + group.getLayoutX() - mousePositionP.get().getX(),
                            m.getY() + group.getLayoutY() - mousePositionP.get().getY());
                    System.out.println(mousePWM);
                    PointCh mousePointCh = mousePWM.toPointCh();
                    int nodeClosestTo = graph.nodeClosestTo(mousePointCh, SEARCH_DISTANCE);
                    // nodeClosestTo est -1 si aucun noeud n'a été trouvé
                    if (nodeClosestTo != -1) {
                        Waypoint newWayPoint = new Waypoint(mousePointCh, nodeClosestTo);
                        waypoints.set(markerIndex, newWayPoint);
                        PointWebMercator tempP = PointWebMercator.ofPointCh(graph.nodePoint(nodeClosestTo));
                        previousMarkerPositionP.set(new Point2D(mvp.viewX(tempP), mvp.viewY(tempP)));
                    } else {
                        group.setLayoutX(previousMarkerPositionP.get().getX());
                        group.setLayoutY(previousMarkerPositionP.get().getY());
                    }
                }
            });
        }
    }

    private void addListeners() {
        mapViewParametersP.addListener(mvp -> {
            createMarkers();
            addMouseEventsManager();
            positionMarkers();
        });
        waypoints.addListener((Observable o) -> {
            createMarkers();
            addMouseEventsManager();
            positionMarkers();
        });
    }

    public Pane pane() {
        return pane;
    }

    private void createMarkers() {
        List<Node> markers = new ArrayList<>();
        for (int i = 0; i < waypoints.size(); i++) {
            SVGPath exteriorMarker = new SVGPath();
            SVGPath interiorMarker = new SVGPath();
            exteriorMarker.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            interiorMarker.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
            exteriorMarker.getStyleClass().add("pin_outside");
            interiorMarker.getStyleClass().add("pin_inside");
            Group group = new Group();
            group.getChildren().addAll( exteriorMarker, interiorMarker);
            group.getStyleClass().add("pin");

            if (i == 0)
                group.getStyleClass().add("first");
            else if (i == waypoints.size() - 1)
                group.getStyleClass().add("last");
            else
                group.getStyleClass().add("middle");

            markers.add(group);
        }
        pane.getChildren().setAll(markers);
        //addMouseEventsManager();
    }

    private void positionMarkers() {
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Waypoint w = waypoints.get(i);
            MapViewParameters mvp = mapViewParametersP.get();
            PointWebMercator p = PointWebMercator.ofPointCh(w.p());
            double x = mvp.viewX(p);
            double y = mvp.viewY(p);
            Node marker = pane.getChildren().get(i);
            marker.setLayoutX(x);
            marker.setLayoutY(y);
        }
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
        if (closestNodeId == -1) {
            errorConsumer.accept("Aucune route à proximité !");
        }
        else {
            waypoints.add(new Waypoint(p, closestNodeId));
        }
    }

}
