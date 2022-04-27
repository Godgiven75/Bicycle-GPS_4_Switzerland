package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static java.lang.Math.multiplyExact;
import static java.lang.Math.scalb;

/**
 * Gère l'affichage et l'interaction avec le fond de carte
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class BaseMapManager {
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersP) throws IOException {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersP = mapViewParametersP;
        this.canvas = new Canvas();
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        this.pane = drawPane();
    }

    /**
     * Retourne le panneau JavaFX affichant le fond de carte
     * @return le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
       return pane;
    }
    private Pane drawPane() throws IOException {
        MapViewParameters mvp = mapViewParametersP.get();
        double x = mvp.xImage();
        double y = mvp.yImage();
        int zoomLevel = mvp.zoomLevel();
        int xIndex = (int) scalb(x, -zoomLevel);
        int yIndex = (int) scalb(y, - zoomLevel);
        TileManager.TileId tileId = new TileManager.TileId(zoomLevel, xIndex, yIndex);
        Image image = tileManager.imageForTileAt(tileId);
        canvas.getGraphicsContext2D().drawImage(image, x, y);
        new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        return pane;
    }
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        try {
            drawPane();
        } catch (IOException ignored) {}
    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
