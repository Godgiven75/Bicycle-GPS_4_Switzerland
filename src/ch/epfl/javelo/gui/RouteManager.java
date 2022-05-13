package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Classe publique finale gérant l'affichage de l'itinéraire et (une partie de)
 * l'interaction avec lui.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class RouteManager {
    private final Pane pane;
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParametersP;
    private final Consumer<String> errorConsumer;
    private final Polyline routePolyline;
    private final Circle highlightedPositionC;

    public RouteManager(RouteBean routeBean,
                        ReadOnlyObjectProperty<MapViewParameters> mvp,
                        Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapViewParametersP = mvp;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();
        pane.setPickOnBounds(false);

        routePolyline = new Polyline();
        routePolyline.setId("route");
        highlightedPositionC = new Circle();
        highlightedPositionC.setId("highlight");
        highlightedPositionC.setRadius(5f);
        pane.getChildren().add(routePolyline);
        pane.getChildren().add(highlightedPositionC);
        addMouseEventsManager();
        addListeners();
    }

    /**
     * Retourne le panneau JavaFX contenant la ligne représentant l'itinéraire
     * et le disque de mise en évidence.
     *
     * @return le panneau JavaFX contenant la ligne représentant l'itinéraire
     * et le disque de mise en évidence
     */
    public Pane pane() {
        return pane;
    }

    private void addMouseEventsManager() {
        highlightedPositionC.setOnMousePressed((e) -> {
            Point2D localToParent = highlightedPositionC.localToParent(e.getX(), e.getY());
            PointCh p = mapViewParametersP.get()
                    .pointAt(localToParent.getX(), localToParent.getY())
                    .toPointCh();
            RoutePoint closestPoint = routeBean.route().pointClosestTo(p);
            int closestNode = routeBean.route().nodeClosestTo(closestPoint.position());
            Waypoint w = new Waypoint(p, closestNode);
            if (routeBean.waypoints().contains(w)) {
                errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
                return;
            }
            routeBean.waypoints().add(w);
        });
    }

    private void addListeners() {
        routeBean.routeProperty().addListener((p) -> {
            // Devrait-on mettre le booléen dans RouteBean ?
            if (!hasItinerary()) {
                errorConsumer.accept("Il n'y a pas de route permettant de " +
                        "relier ces points de passages");
                routePolyline.setVisible(false); //?
                highlightedPositionC.setVisible(false);
                return;
            }

            Route route = routeBean.route();
            // L'emballage des doubles est-il un problème ?
            List<Double> points = new ArrayList<>();
            route.points().forEach(pointCh -> {
                MapViewParameters mvp = mapViewParametersP.get();
                PointWebMercator pwm = PointWebMercator.ofPointCh(pointCh);
                points.add(mvp.viewX(pwm));
                points.add(mvp.viewY(pwm));
            });
            routePolyline.setLayoutX(0);
            routePolyline.setLayoutY(0);
            routePolyline.getPoints().setAll(points);
            routePolyline.setVisible(true);
            highlightPosition();
            highlightedPositionC.setVisible(true);
        });
        mapViewParametersP.addListener((p, o, n) -> {
            int oldZoomLevel = o.zoomLevel();
            int newZoomLevel = n.zoomLevel();

            if(newZoomLevel == oldZoomLevel) {
                Point2D oldTopLeft = o.topLeft();
                Point2D newTopLeft = n.topLeft();
                Point2D offset = newTopLeft.subtract(oldTopLeft);
                routePolyline.setLayoutX(routePolyline.getLayoutX() - offset.getX());
                routePolyline.setLayoutY(routePolyline.getLayoutY() - offset.getY());
            } else {
                if (hasItinerary()) {
                    List<Double> pointsAtNewZoomLevel = new ArrayList<>();
                    routeBean.route().points().forEach(pointCh -> {
                        PointWebMercator pwm = PointWebMercator.ofPointCh(pointCh);
                        pointsAtNewZoomLevel.add(n.viewX(pwm));
                        pointsAtNewZoomLevel.add(n.viewY(pwm));
                    });
                    if (!o.topLeft().equals(n.topLeft())) {
                        routePolyline.setLayoutX(0);
                        routePolyline.setLayoutY(0);
                    }
                    routePolyline.getPoints().setAll(pointsAtNewZoomLevel);
                }
            }
        });
    }

    private void highlightPosition() {
        MapViewParameters mvp = mapViewParametersP.get();
        double highlightedPosition = routeBean.highlightedPositionProperty().get();
        PointWebMercator highlightedPoint =
                PointWebMercator.ofPointCh(routeBean.route().pointAt(highlightedPosition));

        highlightedPositionC.setCenterX(mvp.viewX(highlightedPoint));
        highlightedPositionC.setCenterY(mvp.viewY(highlightedPoint));

    }

    private boolean hasItinerary() {
        return routeBean.route() != null;
    }
}