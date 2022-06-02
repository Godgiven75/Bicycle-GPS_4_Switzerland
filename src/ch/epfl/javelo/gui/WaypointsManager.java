package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
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
    private final static double SEARCH_DISTANCE = 500d;

    /**
     * Construit le réseau routier, les paramètres de la carte, une liste
     * observable de tous les points de passage et un objet permettant de signaler
     * les erreurs.
     *
     * @param graph le réseau routier
     *
     * @param mapViewParametersP la propriété contenant les paramètres de la carte
     *
     * @param waypoints la liste observable des points de passage
     *
     * @param errorConsumer l'objet permettant de signaler les erreurs
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersP,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParametersP = mapViewParametersP;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
        pane = new Pane();
        pane.setPickOnBounds(false);
        createMarkers();
        positionMarkers();
        addListeners();
        addMouseEventsHandler();
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
        if (p != null) {
            int closestNodeId = graph.nodeClosestTo(p, 500);
            if (closestNodeId != -1) {
                waypoints.add(new Waypoint(p, closestNodeId));
                return;
            }
        }
        errorConsumer.accept("Aucune route à proximité !");
    }

    /**
     * Retourne le panneau contenant les points de passage.
     *
     * @return le panneau contenant les points de passage
     */
    public Pane pane() {
        return pane;
    }

    // Ajoute les gestionnaires d'événement détectant le déplacement de la souris
    // lorsque le bouton gauche est maintenu pressé sur un marqueur, et le clic
    // sur un marqueur de point de passage qui permet de le supprimer.
    private void addMouseEventsHandler() {
        // Propriété à usage interne contenant la dernière position de la souris
        ObjectProperty<Point2D> mousePositionP =
                new SimpleObjectProperty<>(new Point2D(0, 0));
        // Propriété à usage interne contenant la position de la souris au
        // moment du dernier clic sur un point de passage
        ObjectProperty<Point2D> previousMarkerPositionP =
                new SimpleObjectProperty<>(new Point2D(0, 0));

        MapViewParameters mvp = mapViewParametersP.get();

        for (int i = 0; i < pane.getChildren().size(); i += 1) {
            Node group = pane.getChildren().get(i);
            int finalI = i;
            group.setOnMousePressed(m -> {
                Point2D mousePosition = new Point2D(m.getX(), m.getY());
                mousePositionP.set(mousePosition);
                PointWebMercator pwm = PointWebMercator.ofPointCh(waypoints.get(finalI).p());
                previousMarkerPositionP.set(new Point2D(mvp.viewX(pwm), mvp.viewY(pwm)));
            });

            group.setOnMouseDragged(m -> {
                group.setLayoutX(group.getLayoutX() + m.getX() - mousePositionP.get().getX());
                group.setLayoutY(group.getLayoutY() + m.getY() - mousePositionP.get().getY());
            });

            group.setOnMouseReleased(m -> {
                if (!m.isStillSincePress()) {
                    PointWebMercator mousePWM = mvp.pointAt(
                            group.getLayoutX() + m.getX() - mousePositionP.get().getX(),
                            group.getLayoutY() + m.getY() - mousePositionP.get().getY());
                    PointCh mousePointCh = mousePWM.toPointCh();
                    if (mousePointCh != null) {
                        int nodeClosestTo = graph.nodeClosestTo(mousePointCh, SEARCH_DISTANCE);
                        // nodeClosestTo est -1 si aucun noeud n'a été trouvé
                        if (nodeClosestTo != -1) {
                            Waypoint newWayPoint = new Waypoint(mousePointCh, nodeClosestTo);
                            waypoints.set(finalI, newWayPoint);
                            PointWebMercator tempP =
                                    PointWebMercator.ofPointCh(graph.nodePoint(nodeClosestTo));
                            previousMarkerPositionP
                                    .set(new Point2D(mvp.viewX(tempP), mvp.viewY(tempP)));
                            return;
                            }
                        }
                        errorConsumer.accept("Aucune route à proximité !");
                        group.setLayoutX(previousMarkerPositionP.get().getX());
                        group.setLayoutY(previousMarkerPositionP.get().getY());
                } else {
                    pane.getChildren().remove(group);
                    waypoints.remove(finalI);
                }
            });
        }
    }

    // Création des marqueurs
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
    }

    // Positionnement des marqueurs
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

    // Ajoute les auditeurs sur les paramètres de la carte et la liste des points
    // de passage.
    private void addListeners() {
        mapViewParametersP.addListener(mvp -> {
            createMarkers();
            addMouseEventsHandler();
            positionMarkers();
        });
        waypoints.addListener((ListChangeListener<Waypoint>) c -> {
            createMarkers();
            addMouseEventsHandler();
            positionMarkers();
        });
    }
}