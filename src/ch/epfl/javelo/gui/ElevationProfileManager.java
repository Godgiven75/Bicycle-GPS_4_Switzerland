package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Publique et finale, gère l'affichage et l'interaction avec le profil en long
 * d'un itinéraire.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */

public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileP;
    private final ReadOnlyDoubleProperty positionP;
    private final ObjectProperty<Rectangle2D> rectangle2DP;
    private final ObjectProperty<Transform> screenToWorldP;
    private final ObjectProperty<Transform> worldToScreenP;
    private final BorderPane mainPane;
    private final DoubleProperty mousePositionOnProfileProperty; // contient la position du curseur sur le profil
    private final Pane centerPane;
    private final VBox bottomPane;
    private final Insets insets = new Insets(10, 10, 20, 40);


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileP,
                                   ReadOnlyDoubleProperty positionP) {
        this.elevationProfileP = elevationProfileP;
        this.positionP = positionP;
        this.mainPane = new BorderPane();
        mainPane.getStylesheets().add("elevation_profile.css");
        this.centerPane = new Pane();
        centerPane.setStyle("-fx-background-color: green");
        mainPane.setCenter(centerPane);
        this.bottomPane = new VBox();
        mainPane.setBottom(bottomPane);
        bottomPane.setStyle("-fx-background-color: red");
        this.rectangle2DP = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        this.mousePositionOnProfileProperty = new SimpleDoubleProperty();
        this.screenToWorldP = new SimpleObjectProperty<>();
        this.worldToScreenP = new SimpleObjectProperty<>();
        screenToWorldP.set(screenToWorld());
        try {
            worldToScreenP.set(worldToScreen());
        } catch (NonInvertibleTransformException error) {
            throw new Error(error);
        }
        addBindings();
        addListeners();
        createPane();
        displayProfile();
    }

    public Pane pane() {
        return mainPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

    private void addListeners() {
        rectangle2DP.addListener(e -> {
            screenToWorldP.set(screenToWorld());
            try {
                worldToScreenP.set(worldToScreen());
            } catch (NonInvertibleTransformException error) {
                throw new Error(error);
            }
        });
        elevationProfileP.addListener(e -> {
            screenToWorldP.set(screenToWorld());
            try {
                worldToScreenP.set(worldToScreen());
            } catch (NonInvertibleTransformException error){
                throw new Error(error);
            }
        });
    }
    private void addBindings() {
        // Lie la propriété contenant le rectangle aux propriétés contenant la
        // largeur et la longueur du panneau central

        rectangle2DP.bind(Bindings.createObjectBinding(() -> {
            double width = Math2.clamp(0,
                    centerPane.getWidth() - insets.getRight(),
                    centerPane.getWidth());
            double height = Math2.clamp(0,
                    centerPane.getHeight() - insets.getBottom(),
                    centerPane.getWidth());
            return new Rectangle2D(insets.getLeft(), insets.getTop(), width, height);
        }, centerPane.heightProperty(), centerPane.widthProperty()));

    }
    private void addMouseEventsManager() {


    }

    private int computeVerticalStep() {
        final int minVerticalDistance = 50;
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        double height = rectangle2DP.get().getHeight();
        double maxElevation = elevationProfileP.get().maxElevation();
        for (int step : ELE_STEPS) {
            int nbIntervals = (int) (maxElevation / step);
            if (height / nbIntervals >= minVerticalDistance)
                return step;
        }
        return ELE_STEPS[ELE_STEPS.length - 1];

    }
    private int computeHorizontalStep() {
        final int minHorizontalDistance = 50;
        int[] POS_STEPS =
                { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
        double width = rectangle2DP.get().getWidth();
        double length = elevationProfileP.get().length();
        for (int step : POS_STEPS) {
            int nbIntervals = (int) (length / step);
            if (width / nbIntervals >= minHorizontalDistance)
                return step;
        }
        return POS_STEPS[POS_STEPS.length - 1];
    }

    private void displayProfile() {
        Polygon polygon = new Polygon();
        centerPane.getChildren().add(polygon);
        List<Double> points = new ArrayList<>();
        Rectangle2D r = rectangle2DP.get();
        ElevationProfile p = elevationProfileP.get();
        double minX = r.getMinX();
        double maxX = r.getMaxX();
        Transform worldToScreen = worldToScreenP.get();
        Transform screenToWorld = screenToWorldP.get();
        for (double x = minX; x <= maxX; x+= 1 ) {
            double position = screenToWorld.transform(x, 0).getX();
            points.add(x);
            points.add(worldToScreen.transform(0, p.elevationAt(position)).getX());
        }
        polygon.getPoints().setAll(points);
        polygon.getStyleClass().add("elevationProfileP");
    }
    private void createPane() {
        // contient les 2 conteneurs
        // le chemin représentant la grille
        Path path = new Path();
        centerPane.getChildren().add(path);
        path.getStyleClass().add("grid");
        // à mettre dans une boucle
        path.getElements().add(new MoveTo());
        path.getElements().add(new LineTo());
        // les étiquettes de la grille
        Group group = new Group();
        centerPane.getChildren().add(group);
        // le graphe du profil
        // la position mise en évidence
        Line line = new Line();
        line.layoutXProperty().bind(Bindings.createDoubleBinding(positionP::get));
        line.startYProperty().bind(Bindings.select(rectangle2DP, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle2DP, "maxY"));
        line.visibleProperty().bind(positionP.greaterThanOrEqualTo(0));
        centerPane.getChildren().add(line);
        Pane bottomPane = this.bottomPane;
        mainPane.setBottom(bottomPane);
        bottomPane.getStyleClass().add("profile_data");
        // texte contenant les statistiques du profil
        Text textVBox = new Text();
        bottomPane.getChildren().add(textVBox);
    }

    // Passe du système de coordonnées du panneau JavaFX contenant la grille au
    // système de coordonnées du "monde réel".
    private Transform screenToWorld() {
        Rectangle2D rect = rectangle2DP.get();
        Affine transform = new Affine();
        ElevationProfile elevationProfile = elevationProfileP.get();
        transform.prependTranslation(-rect.getMinX(), -rect.getMinY());
        double sx = elevationProfile.length() / (rect.getMaxX() - rect.getMinX());
        double sy = elevationProfile.maxElevation() / (rect.getMaxY() - rect.getMinY());
        transform.prependScale(sx, sy);
        transform.prependTranslation(0, elevationProfileP.get().maxElevation());
        return transform;
    }

    private Transform worldToScreen() throws NonInvertibleTransformException {
        return screenToWorld().createInverse();
    }
}