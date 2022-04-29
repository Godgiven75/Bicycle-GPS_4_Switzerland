package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
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
    private Pane pane;
    private Canvas canvas;
    private boolean redrawNeeded;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersP) throws IOException {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersP = mapViewParametersP;
        this.canvas = new Canvas(300, 600);
        redrawOnNextPulse();
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        pane = new Pane(this.canvas);
        canvas.widthProperty().bind(pane.widthProperty());
    }

    /**
     * Retourne le panneau JavaFX affichant le fond de carte
     * @return le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
       return pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        try {
            Canvas newC = new Canvas(300, 600);
            MapViewParameters mvp = mapViewParametersP.get();
            int zoomLevel = mvp.zoomLevel();
            double xImage = mvp.xImage();
            double yImage = mvp.yImage();
            final int TILE_SIZE = 256;
            for (int x = 0; x <= newC.getWidth(); x += TILE_SIZE) {
                for (int y = 0; y <= newC.getHeight(); y += TILE_SIZE) {
                    int xId = (int) scalb(xImage, -zoomLevel) + x;
                    int yId = (int) scalb(yImage, -zoomLevel) + y;
                    TileManager.TileId tileId = new TileManager.TileId(zoomLevel, xId, yId);
                    Image image = tileManager.imageForTileAt(tileId);
                    newC.getGraphicsContext2D()
                            .drawImage(image, x, y);
                }
                canvas = newC;
                pane = new Pane(canvas);
            }
        } catch (Exception ignored) {
            System.out.println(ignored);
        }

    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
