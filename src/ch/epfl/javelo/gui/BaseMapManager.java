package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;


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
    private static final int PIXELS_IN_TILE = 256;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersP) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersP = mapViewParametersP;
        this.canvas = new Canvas(500, 500);
        this.canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        //this.canvas
        redrawOnNextPulse();
        this.pane = new Pane();
        this.pane.getChildren()
                .add(canvas);
        this.canvas.widthProperty()
                .bind(pane.widthProperty());
        this.canvas.heightProperty()
                .bind(pane.heightProperty());
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
            canvas = new Canvas(500, 500);
            MapViewParameters mvp = mapViewParametersP.get();
            int zoomLevel = mvp.zoomLevel();
            double xImage = mvp.xImage();
            double yImage = mvp.yImage();

            // Index de la première tuile que l'on va dessiner (celle qui contient
            // le coin en haut à gauche donné dans les MapViewParameters)
            int firstXIndex = Math.floorDiv( (int) xImage, PIXELS_IN_TILE);
            int firstYIndex = Math.floorDiv( (int) yImage, PIXELS_IN_TILE);

            //Nombre de tuiles sur l'axe horizontal
            int tilesInWidth = (int) canvas.getWidth() / PIXELS_IN_TILE + 1;
            //Nombre de tuiles sur l'axe vertical
            int tilesInHeight = (int) canvas.getHeight() / PIXELS_IN_TILE + 1;

            PointWebMercator topLeft = mvp.pointAt(mvp.xImage(), mvp.yImage());
            double topLeftX = mvp.viewX(topLeft);
            double topLeftY = mvp.viewY(topLeft);

            // Coordonées du pixel correspondant au coin en haut à gauche de la
            // première tuile à dessiner sur le canevas
            double firstX = firstXIndex * PIXELS_IN_TILE - topLeftX;
            double firstY = firstYIndex * PIXELS_IN_TILE - topLeftY;

            int xIndex = firstXIndex;
            double x = firstX;
            for (int i = 0; i <= tilesInWidth; i += 1) {
                int yIndex = firstYIndex;
                double y = firstY;
                for (int j = 0; j <= tilesInHeight; j += 1) {
                    TileManager.TileId tileId =
                            new TileManager.TileId(zoomLevel, xIndex, yIndex++);
                    Image image = tileManager.imageForTileAt(tileId);
                    canvas.getGraphicsContext2D()
                            .drawImage(image,x, y);
                    y += PIXELS_IN_TILE;
                }
                xIndex++;
                x += PIXELS_IN_TILE;
            }

            pane.getChildren()
                    .add(canvas);
        } catch (IOException ignored) {}

    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
