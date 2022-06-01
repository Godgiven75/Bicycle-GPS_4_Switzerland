package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


import java.util.function.Consumer;

/**
 * Gère l'affichage de la carte "annotée", c.-à-d. le fond de la carte au-dessus
 * duquel sont superposés l'itinéraire et les points de passage.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class AnnotatedMapManager {
    private final RouteBean routeBean;
    private final StackPane pane;
    private final SimpleDoubleProperty mousePositionOnRouteP;
    private final ObjectProperty<Point2D> mousePositionP;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private static final double MOUSE_ON_ROUTE_DISTANCE = 15d;
    private static final int INITIAL_ZOOM_LEVEL = 12;
    private static final int INITIAL_X = 543200;
    private static final int INITIAL_Y = 370650;
    private static final MapViewParameters INIT_MVP =
            new MapViewParameters(INITIAL_ZOOM_LEVEL, INITIAL_X, INITIAL_Y);

    /**
     * Construit un gestionnaire de carte "annotée".
     *
     * @param graph le graphe du réseau routier
     * @param tileManager le gestionnaire de tuile
     * @param routeBean le "bean" JavaFX correspondant à la route
     * @param errorConsumer le consommateur d'erreur
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager,
                               RouteBean routeBean, Consumer<String> errorConsumer) {
        this.routeBean = routeBean;

        mapViewParametersP = new SimpleObjectProperty<>(INIT_MVP);
        WaypointsManager waypointsManager =
                new WaypointsManager(graph, mapViewParametersP,
                        routeBean.waypoints(), errorConsumer);

        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);

        RouteManager routeManager =
                new RouteManager(routeBean, mapViewParametersP);

        pane = new StackPane();
        pane.getStyleClass().add("map.css");

        mousePositionOnRouteP = new SimpleDoubleProperty();
        mousePositionP = new SimpleObjectProperty<>(new Point2D(0, 0));
        // Création de la hiérarchie JavaFX
        pane.getChildren().add(baseMapManager.pane());
        pane.getChildren().add(routeManager.pane());
        pane.getChildren().add(waypointsManager.pane());

        addMouseEventsManager();
        createBindings();
    }

    /**
     * Retourne le panneau principal du gestionnaire de carte "annotée".
     *
     * @return le panneau principal du gestionnaire de carte "annotée"
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Retourne une propriété JavaFX contenant la position le long de l'itinéraire
     * correspondant à la position de la souris.
     *
     * @return une propriété JavaFX contenant la position le long de l'itinéraire
     * correspondant à la position de la souris
     */
    public SimpleDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteP;
    }

    // Ajoute le gestionnaire d'événements de souris sur le panneau.
    private void addMouseEventsManager() {
        pane.setOnMouseMoved(e ->
                mousePositionP.set(new Point2D(e.getX(), e.getY())));
        pane.setOnMouseExited(e -> mousePositionP.set(null));
    }

    // Création des liens entre la propriété contenant la position de la souris
    // sur le profil et celles contenant les paramètres de la carte, la position
    // de la souris et la route.
    private void createBindings() {
        mousePositionOnRouteP.bind(Bindings.createDoubleBinding(() -> {
            Route route = routeBean.route();
            if (route != null && mousePositionP.get() != null) {
                Point2D mousePosition = mousePositionP.get();
                MapViewParameters mvp = mapViewParametersP.get();
                PointWebMercator p = mvp.pointAt(mousePosition.getX(), mousePosition.getY());
                PointCh pToCh = p.toPointCh();
                if (pToCh != null) {
                    RoutePoint closestPoint = route.pointClosestTo(pToCh);
                    double mouseX = mvp.viewX(p);
                    double mouseY = mvp.viewY(p);
                    PointWebMercator onRoute = PointWebMercator.ofPointCh(closestPoint.point());
                    double routeX = mvp.viewX(onRoute);
                    double routeY = mvp.viewY(onRoute);
                    if (Math2.norm(routeX - mouseX, routeY - mouseY) <=
                            MOUSE_ON_ROUTE_DISTANCE) {
                        return closestPoint.position();
                    }
                }
            }
            return Double.NaN;
        }, mousePositionP, mapViewParametersP, routeBean.routeProperty()));
    }
}