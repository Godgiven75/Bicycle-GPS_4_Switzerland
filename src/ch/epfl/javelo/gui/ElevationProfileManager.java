package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

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
    private final DoubleProperty mousePositionOnProfileP; // contient la position du curseur sur le profil
    private final Pane centerPane;
    private final VBox bottomPane;
    private final Insets insets = new Insets(10, 10, 20, 40);
    private Polygon polygon;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileP,
                                   ReadOnlyDoubleProperty positionP) {
        this.elevationProfileP = elevationProfileP;
        this.positionP = positionP;
        this.mainPane = new BorderPane();
        this.centerPane = new Pane();
        this.bottomPane = new VBox();
        this.rectangle2DP = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        this.mousePositionOnProfileP = new SimpleDoubleProperty();
        this.screenToWorldP = new SimpleObjectProperty<>();
        this.worldToScreenP = new SimpleObjectProperty<>();
        mainPane.getStylesheets().add("elevation_profile.css");
        bottomPane.setId("profile_data");
        mainPane.setCenter(centerPane);
        mainPane.setBottom(bottomPane);
        this.polygon = new Polygon();
        centerPane.getChildren().add(polygon);
        addBindings();
        addListeners();
    }

    public Pane pane() {
        return mainPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileP;
    }

    private void addListeners() {
        rectangle2DP.addListener((p, o, n) -> {
            screenToWorldP.set(screenToWorld());
            try {
                worldToScreenP.set(worldToScreen());
            } catch (NonInvertibleTransformException error) {
                throw new Error(error);
            }
            displayProfile();
            createPane();
        });
        elevationProfileP.addListener((p, o, n) -> {
            screenToWorldP.set(screenToWorld());
            try {
                worldToScreenP.set(worldToScreen());
            } catch (NonInvertibleTransformException error) {
                throw new Error(error);
            }
            displayProfile();
            createPane();
        });
    }
    private void addBindings() {
        // Lie la propriété contenant le rectangle aux propriétés contenant la
        // largeur et la longueur du panneau central
        rectangle2DP.bind(Bindings.createObjectBinding(() -> {
            double width = Math2.clamp(0,
                    mainPane.getWidth() - insets.getRight() - insets.getLeft(),
                    mainPane.getWidth());
            System.out.println(mainPane.getWidth() + " lol");
            double height = Math2.clamp(0,
                    mainPane.getHeight() - insets.getBottom() - insets.getTop(),
                    mainPane.getHeight());
            System.out.println(mainPane.getHeight());
            System.out.println(mainPane.getHeight() - insets.getBottom() - insets.getTop());
            return new Rectangle2D(insets.getLeft(), insets.getTop(), width, height);
        }, centerPane.heightProperty(), centerPane.widthProperty()));
    }

    private void addMouseEventsManager() {

    }

    private int computeVerticalStep() {
        final int minVerticalDistance = 25;
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        double height = rectangle2DP.get().getHeight();
        double maxElevation = elevationProfileP.get().maxElevation();
        for (int step : ELE_STEPS) {
            double temp = (step * height) / maxElevation;
            if (temp >= minVerticalDistance) {
                System.out.println("Vertical : " + step);
                return step;
            }
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
            double temp = (step * width) / length;
            if (temp >= minHorizontalDistance) {
                System.out.println("Horizontal : " + step);
                return step;
            }
        }
        return POS_STEPS[POS_STEPS.length - 1];
    }

    private void displayProfile() {
        List<Double> points = new ArrayList<>();
        Rectangle2D r = rectangle2DP.get();
        ElevationProfile p = elevationProfileP.get();
        double minX = r.getMinX();
        double maxX = r.getMaxX();
        Transform worldToScreen = worldToScreenP.get();
        Transform screenToWorld = screenToWorldP.get();
        points.add(minX);
        points.add(r.getMaxY());
        for (double x = minX; x <= maxX; x += 1) {
            double position = screenToWorld.transform(x, 0).getX();
            points.add(x);
            points.add(worldToScreen.transform(0, p.elevationAt(position)).getY());
        }
        points.add(r.getMaxX());
        points.add(r.getMaxY());
        polygon.getPoints().setAll(points);
        polygon.setId("profile");
    }

    private void createPane() {
        // contient les 2 conteneurs
        // le chemin représentant la grille
        Path path = new Path();
        path.setId("grid");
        Rectangle2D r = rectangle2DP.get();
        double minX = r.getMinX();
        double minY = r.getMinY();
        double maxX = r.getMaxX();
        double maxY = r.getMaxY();
        Transform worldToScreen = worldToScreenP.get();
        double horizontalStep = worldToScreen.transform(computeHorizontalStep(), 0).getX();
        double verticalStep = worldToScreen.transform(0, computeVerticalStep()).getY();
        for (double x = minX; x <= maxX; x += horizontalStep) {
            path.getElements().add(new MoveTo(x, maxY));
            path.getElements().add(new LineTo(x, minY));
        }
        for (double y = minY; y <= maxY; y+= verticalStep) {
            path.getElements().add(new MoveTo(minY, y));
            path.getElements().add(new LineTo(maxX, y));
        }
        centerPane.getChildren().add(path);
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
        double sy = (elevationProfile.minElevation() - elevationProfile.maxElevation())
                / (rect.getMaxY() - rect.getMinY());
        transform.prependScale(sx, sy);
        transform.prependTranslation(0, elevationProfileP.get().maxElevation());
        return transform;
    }

    private Transform worldToScreen() throws NonInvertibleTransformException {
         return screenToWorld().createInverse();
    }
}
