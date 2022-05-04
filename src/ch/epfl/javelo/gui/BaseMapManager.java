package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    private ObjectProperty<Point2D> mousePositionP;
    private Pane pane;
    private Canvas canvas;
    private boolean redrawNeeded;
    private int currentZoomLevel;
    private static final int PIXELS_IN_TILE = 256;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersP) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapViewParametersP = mapViewParametersP;
        this.currentZoomLevel = mapViewParametersP.get().zoomLevel();
        this.canvas = new Canvas(600, 300);
        this.pane = new Pane(canvas);
        this.mousePositionP = new SimpleObjectProperty<>(new Point2D(0, 0));
        pane.setPickOnBounds(false);
        addBindings();
        addMouseEventsManager();

        System.out.println(pane);

        //addMouseEventsManager();

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        canvas.widthProperty().addListener((p) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p) -> redrawOnNextPulse());


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

            MapViewParameters mvp = mapViewParametersP.get();

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

            // Coordonnées du pixel correspondant au coin en haut à gauche de la
            // première tuile à dessiner sur le canevas
            double firstX =  topLeftX + firstXIndex * PIXELS_IN_TILE - mousePositionP.get().getX();
            System.out.println(firstX);
            double firstY = topLeftY + firstYIndex * PIXELS_IN_TILE - mousePositionP.get().getY();
            int xIndex = firstXIndex;
            double x = firstX;
            for (int i = 0; i <= tilesInWidth; i += 1) {
                int yIndex = firstYIndex;
                double y = firstY;
                for (int j = 0; j <= tilesInHeight; j += 1) {
                    TileManager.TileId tileId =
                            new TileManager.TileId(currentZoomLevel, xIndex, yIndex++);
                    Image image = tileManager.imageForTileAt(tileId);
                    canvas.getGraphicsContext2D()
                            .drawImage(image, x, y);
                    y += PIXELS_IN_TILE;
                }
                xIndex++;
                x += PIXELS_IN_TILE;
            }
        } catch (IOException ignored) {}  // Ne devrait jamais se produire

    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
    private void addBindings() {
        canvas.widthProperty()
                .bind(pane.widthProperty());
        canvas.heightProperty()
                .bind(pane.heightProperty());
    }
    private void addMouseEventsManager() {

        pane.setOnMouseDragged(event -> {
            Point2D previousPosition = mousePositionP.get();
            Point2D currentPosition = new Point2D(event.getX(), event.getY());
            mousePositionP.set(mousePositionP.get().add(event.getX(), event.getY()));
            Point2D offset = currentPosition.subtract(previousPosition);
            redrawOnNextPulse();
        });

        //pane.setOnMousePressed();
        //pane.setOnMouseReleased();


        pane.setOnScroll(e -> {
            mapViewParametersP.set(new MapViewParameters(currentZoomLevel + (int)e.getDeltaY(),
                    mapViewParametersP.get().xImage(), mapViewParametersP.get().yImage()));
            redrawOnNextPulse();
        });

    }

}