package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

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
    private RouteBean routeBean;
    private ReadOnlyObjectProperty<MapViewParameters> mapViewParametersP;
    private Consumer<String> errorConsumer;

    public RouteManager(RouteBean routeBean,
                        ReadOnlyObjectProperty<MapViewParameters> mvp,
                        Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapViewParametersP = mvp;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();
        pane.setPickOnBounds(false);
    }

    /**
     * Retourne le panneau JavaFX contenant la ligne représentant l'itinéraire
     * et le disque de mise en évidence.
     *
     * @return le panneau JavaFX contenant la ligne représentant l'itinéraire
     * et le disque de mise en évidence
     */
    public Pane pane() {
        MapViewParameters mvp = mapViewParametersP.get();
        List<PointCh> routeBeanItineraryPoints = routeBean.route().points();
        List<Node> paneChildren = pane.getChildren();
        int zoomLevel = mvp.zoomLevel();

        double[] polylinePoints = new double[2 * routeBeanItineraryPoints.size()];
        for (int i = 0; i < routeBeanItineraryPoints.size(); i++) {
            PointCh pch = routeBean.route().points().get(i);
            PointWebMercator pwm = PointWebMercator.ofPointCh(pch);
            double x = pwm.xAtZoomLevel(zoomLevel);
            double y = pwm.yAtZoomLevel(zoomLevel);
            polylinePoints[i] = x;
            polylinePoints[i + 1] = y;
        }
        Polyline itineraryGUI = new Polyline(polylinePoints);
        itineraryGUI.setId("route");
        paneChildren.add(itineraryGUI);

        double highlightedPosition = routeBean.highlightedPosition();
        PointWebMercator highlightedPoint =
                PointWebMercator.ofPointCh(routeBean.route().pointAt(highlightedPosition));
        Circle highlightedPositionGUI = new Circle();
        highlightedPositionGUI.setCenterX(highlightedPoint.xAtZoomLevel(zoomLevel));
        highlightedPositionGUI.setCenterY(highlightedPoint.yAtZoomLevel(zoomLevel));
        highlightedPositionGUI.setRadius(5f);
        highlightedPositionGUI.setId("highlighted");
        paneChildren.add(highlightedPositionGUI);

        return pane;
    }

}