package ch.epfl.javelo.gui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

/**
 * Gère l'affichage et l'interaction avec le fond de carte
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class BaseMapManager {
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersP) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersP = mapViewParametersP;
    }

    /**
     * Retourne le panneau JavaFX affichant le fond de carte
     * @return le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
        Pane pane = new Pane();
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(pane.widthProperty());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.



    }
}
