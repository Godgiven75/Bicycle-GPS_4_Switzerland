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
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

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
    private ObjectProperty<Point2D> mousePosition;
    private ObjectProperty<Point2D> gpOldPosition;
    private boolean hasClosestNode = false;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersP,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParametersP = mapViewParametersP;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
        this.gpOldPosition = new SimpleObjectProperty<>(new Point2D(0, 0));
        this.pane = drawPane();
        pane.setPickOnBounds(false);
        this.mousePosition = new SimpleObjectProperty<>(new Point2D(0, 0));
        addListeners();
        //addMouseEventsManager();

    }

    private void addMouseEventsManager() {
        MapViewParameters mvp = mapViewParametersP.get();

        for (Node group : pane.getChildren()) {
            int gpIndex = waypoints.indexOf(group);
            double finalX = group.getLayoutX();
            double finalY = group.getLayoutY();

            group.setOnMousePressed(m -> {
                mousePosition.set(new Point2D(m.getX(), m.getY()));
            });
            group.setOnMouseDragged(m -> {
                group.setLayoutX(m.getSceneX() - mousePosition.get().getX());
                group.setLayoutY(m.getSceneY() - mousePosition.get().getY());
            });

            group.setOnMouseReleased(m -> {
                PointWebMercator mousePWM = mvp.pointAt(
                        mousePosition.get().getX() - m.getSceneX(),
                        mousePosition.get().getY() - m.getSceneY());
                addWayPoint(mousePWM.x(), mousePWM.y());
                if (hasClosestNode) {
                    waypoints.set(gpIndex, waypoints.get(waypoints.size() - 1));
                    waypoints.remove(waypoints.size() - 1);
                    PointWebMercator tempP = PointWebMercator.ofPointCh(waypoints.get(gpIndex).p());
                    System.out.printf("Nouveau marqueur d'abscisse PWM %f \n", tempP.x());
                    System.out.printf("Nouveau marqueur d'abscisse %f \n", mvp.viewX(tempP));
                    group.setLayoutX(mvp.viewX(tempP));
                    group.setLayoutY(mvp.viewY(tempP));
                } else {
                    group.setLayoutX(finalX);
                    group.setLayoutY(finalY);
                }
            });

        }
    }

    private void addListeners() {
        mapViewParametersP.addListener(mvp -> drawPane());
        waypoints.addListener((Observable o) -> drawPane());
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
            if (i != 0 && i != (waypoints.size() - 1)) {
                group.getStyleClass().add("map.middle");
                exteriorMarker.setFill(Color.DARKTURQUOISE);
            } else {
                if (i == 0) {
                    group.getStyleClass().add("map.first");
                    exteriorMarker.setFill(Color.LIMEGREEN);
                }
                if (i == (waypoints.size() - 1)) {
                    group.getStyleClass().add("map.last");
                    exteriorMarker.setFill(Color.ORANGERED);
                }
            }
            PointCh pointCh = waypoint.p();
            PointWebMercator p = PointWebMercator.ofPointCh(pointCh);
            double finalX = mvp.viewX(p);
            double finalY = mvp.viewY(p);
            System.out.println("X du nouveau WayPoint: " + finalX);
            System.out.println("Y du nouveau WayPoint: " + finalY);
            group.setLayoutX(finalX);
            group.setLayoutY(finalY);
            gpOldPosition.set(new Point2D(finalX, finalY));

            group.setOnMousePressed(e -> {
                mousePosition.set(new Point2D(e.getX(), e.getY()));
            });

            group.setOnMouseDragged(m -> {
                group.setLayoutX(m.getSceneX() - mousePosition.get().getX());
                group.setLayoutY(m.getSceneY() - mousePosition.get().getY());
            });

            int finalI = i;
            group.setOnMouseReleased(m -> {
                PointWebMercator mousePWM = mvp.pointAt(
                        mousePosition.get().getX() - m.getSceneX(),
                        mousePosition.get().getY() - m.getSceneY());
                addWayPoint(mousePWM.x(), mousePWM.y());
                if (hasClosestNode) {
                    waypoints.set(finalI, waypoints.get(waypoints.size() - 1));
                    waypoints.remove(waypoints.size() - 1);
                    PointWebMercator tempP = PointWebMercator.ofPointCh(waypoints.get(finalI).p());
                    group.setLayoutX(mvp.viewX(tempP));
                    group.setLayoutY(mvp.viewY(tempP));
                    gpOldPosition.set(new Point2D(mvp.viewX(tempP), mvp.viewY(tempP)));
                } else {
                    group.setLayoutX(gpOldPosition.get().getX());
                    group.setLayoutY(gpOldPosition.get().getY());
                }
            });

            pane.getChildren().add(group);
        }
        System.out.printf("Il y a %d marqueurs \n", pane.getChildren().size());
        pane.setPickOnBounds(false);
        return pane;
    }

    private void createMarkers() {

    }

    private void positionMarkers() {


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
            hasClosestNode = false;
        }
        else {
            waypoints.add(new Waypoint(p, closestNodeId));
            hasClosestNode = true;
        }
    }
}