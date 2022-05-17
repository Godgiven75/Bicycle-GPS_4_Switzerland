package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * Publique et finale, gère l'affichage et l'interaction avec le profil en long
 * d'un itinéraire.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */

public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> profile;
    private final ReadOnlyDoubleProperty position;
    private final ObjectProperty<Rectangle2D> rectangle2DP;
    private final ObjectProperty<Transform> screenToWorldP;
    private final ObjectProperty<Transform> worldToScreenP;
    private final BorderPane mainPane;
    private final DoubleProperty mousePositionOnProfileProperty; // contient la position du curseur sur le profil
    private final Pane centerPane;
    private final VBox bottomPane;
    private final Insets insets = new Insets(10, 10, 20, 40);


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
                                   ReadOnlyDoubleProperty position) {
        this.profile = profile;
        this.position = position;
        this.screenToWorldP = new SimpleObjectProperty<>();
        this.worldToScreenP = new SimpleObjectProperty<>();
        this.mainPane = new BorderPane();
        this.centerPane = new Pane();
        this.bottomPane = new VBox();
        this.rectangle2DP = new SimpleObjectProperty<>(
                new Rectangle2D(
                        centerPane.getHeight() - insets.getBottom(),
                        centerPane.getHeight() - insets.getBottom(),
                        centerPane.getWidth() - (insets.getRight() + insets.getLeft()),
                        centerPane.getHeight() - (insets.getBottom() + insets.getTop())));
        this.mousePositionOnProfileProperty = new SimpleDoubleProperty();
    }

    private void addListeners() {
        rectangle2DP.addListener(e -> {
            // la position mise en évidence
            Line line = new Line();
            centerPane.getChildren().add(line);
        });
    }

    public Pane pane() {
        return mainPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

    private int computeVerticalStep() {
        final int minVerticalDistance = 50;
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        double height = rectangle2DP.get().getHeight();
        double maxElevation = profile.get().maxElevation();
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
        double length = profile.get().length();
        for (int step : POS_STEPS) {
            int nbIntervals = (int) (length / step);
            if (width / nbIntervals >= minHorizontalDistance)
                return step;
        }
        return POS_STEPS[POS_STEPS.length - 1];
    }

    private void createPane() {
        // contient les 2 conteneurs
        mainPane.getStylesheets().add("elevation_profile");

        Pane centerPane = this.centerPane;
        mainPane.setCenter(centerPane);

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
        Polygon polygon = new Polygon();
        // ne pas oublier les 2 points additionnels du semi-rectangle
        int verticalStep = computeVerticalStep();
        int horizontalStep = computeHorizontalStep();
        double[] verticalPoints = new double[(int) (profile.get().maxElevation() / verticalStep)];
        double[] horizontalPoints = new double[(int) (profile.get().length() / horizontalStep)];
        int j = 0;
        for (int i = 0; i < verticalPoints.length; i++)
            verticalPoints[i] = profile.get().elevationAt(j += verticalStep);
        j = 0;
        for (int i = 0; i < horizontalPoints.length; i++)
            horizontalPoints[i] = profile.get().elevationAt(j += horizontalStep);
        for (int i = 0; i < horizontalPoints.length; i++) {
            if (i % 2 == 0)
                polygon.getPoints().add(horizontalPoints[i]);
            else polygon.getPoints().add(verticalPoints[i]);
        }

        centerPane.getChildren().add(polygon);
        polygon.getStyleClass().add("profile");

        // la position mise en évidence
        Line line = new Line();
        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> position.get()));
        line.startYProperty().bind(Bindings.select(rectangle2DP, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle2DP, "maxY"));
        line.visibleProperty().bind(position.greaterThanOrEqualTo(0));
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
    private void screenToWorld(Point2D pointToTransform) {
        Rectangle2D rect = rectangle2DP.get();
        Affine affine = new Affine();
        ElevationProfile elevationProfile = profile.get();
        // translate le vecteur tel que son origine se positionne sur le coin
        // haut-gauche du rectangle
        affine.prependTranslation(- (pointToTransform.getX() + insets.getLeft()),
                    pointToTransform.getY() + insets.getBottom());
        // passe de l'échelle de l'écran au monde réel (dont les axes des ordonnées
        // sont opposés)
        affine.prependScale(
                (1.0 / rect.getWidth()) * elevationProfile.length(),
                 - (1.0 / rect.getHeight())
                        * (elevationProfile.maxElevation()
                            - elevationProfile.minElevation()));
        // translate à nouveau de sorte que l'origine du vecteur se trouve au coin
        // bas-gauche du rectangle
        affine.prependTranslation(pointToTransform.getX() + insets.getLeft(),
                    - (pointToTransform.getY() + insets.getBottom()));
        screenToWorldP.set(affine);
    }

    private void worldToScreen() throws NonInvertibleTransformException {
        worldToScreenP.set(screenToWorldP.get().createInverse());
    }
}
