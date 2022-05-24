package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
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
    private final Graph graph;
    private final TileManager tileManager;
    private final RouteBean routeBean;
    private final Consumer<String> errorConsumer;
    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;
    private final StackPane pane;
    private final SimpleDoubleProperty mousePositionOnRouteP;
    private final ObjectProperty<Point2D> mousePositionP;



    /**
     * Construit un gestionnaire de carte "annotée".
     * @param graph le graphe du réseau routier
     * @param tileManager le gestionnaire de tuile
     * @param routeBean le "bean" JavaFX correspondant à la route
     * @param errorConsumer le consommateur d'erreur
     */
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

        pane = new StackPane();
        pane.getStyleClass().add("map.css");

        mousePositionOnRouteP = new SimpleDoubleProperty();
        mousePositionP = new SimpleObjectProperty<>();
        // Création de la hiérarchie JavaFX
        pane.getChildren().add(baseMapManager.pane());
        pane.getChildren().add(routeManager.pane());
        pane.getChildren().add(waypointsManager.pane());

    }

    /**
     * Retourne le panneau principal du gestionnaire de carte "annotée".
     * @return le panneau principal du gestionnaire de carte "annotée"
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Retourne une propriété JavaFX contenant la position le long de l'itinéraire
     * correspondant à la position de la souris.
     * @return une propriété JavaFX contenant la position le long de l'itinéraire
     * correspondant à la position de la souris
     */
    public SimpleDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteP;
    }

    private void addMouseEventsManager() {
        pane.setOnMouseEntered(e -> {
            mousePositionP.set(new Point2D(e.getX(), e.getY()));
        });

        pane.setOnMouseExited(e -> {
            mousePositionOnRouteP.set(Double.NaN);

        });
    }

    private void addBindings() {

    }


}
