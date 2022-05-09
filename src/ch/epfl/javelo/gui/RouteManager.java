package ch.epfl.javelo.gui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

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
        Polyline itinerary = new Polyline();
        itinerary.setId("route");
        pane.getChildren().add(itinerary);


        Circle highlightedPosition = new Circle();
        highlightedPosition.setRadius(5f);
        highlightedPosition.setId("highlighted");
        pane.getChildren().add(highlightedPosition);

        return pane;
    }

}
