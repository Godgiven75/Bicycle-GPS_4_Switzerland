package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
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
    private final ReadOnlyDoubleProperty highlightedPositionP;
    private final ObjectProperty<Rectangle2D> rectangle2DP;
    private final ObjectProperty<Transform> screenToWorldP;
    private final ObjectProperty<Transform> worldToScreenP;
    private final DoubleProperty mousePositionOnProfileP; // contient la position du curseur sur le profil
    private final BorderPane mainPane;
    private final Pane centerPane;
    private final Insets insets = new Insets(10, 10, 20, 40);
    private final Polygon polygon;
    private final Path path;
    private final Group group;
    private final Text textVbox;
    private final Line line;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileP,
                                   ReadOnlyDoubleProperty highlightedPositionP) {
        this.elevationProfileP = elevationProfileP;
        this.highlightedPositionP = highlightedPositionP;
        this.mainPane = new BorderPane();
        this.centerPane = new Pane();
        VBox bottomPane = new VBox();
        this.rectangle2DP = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        this.mousePositionOnProfileP = new SimpleDoubleProperty(highlightedPositionP.get());
        this.screenToWorldP = new SimpleObjectProperty<>();
        this.worldToScreenP = new SimpleObjectProperty<>();
        mainPane.getStylesheets().add("elevation_profile.css");
        bottomPane.setId("profile_data");
        mainPane.setBottom(bottomPane);
        mainPane.setCenter(centerPane);
        this.polygon = new Polygon();
        polygon.setId("profile");
        this.path = new Path();
        path.setId("grid");
        this.group = new Group();
        this.textVbox = new Text();
        this.line = new Line();
        centerPane.getChildren().add(line);
        centerPane.getChildren().add(path);
        centerPane.getChildren().add(polygon);
        centerPane.getChildren().add(group);
        bottomPane.getChildren().add(textVbox);
        addBindings();
        addListeners();
        addMouseEventsManager();
    }

    /**
     *
     * @return
     */
    public Pane pane() {
        return mainPane;
    }

    /**
     *
     * @return
     */
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
                    centerPane.getWidth() - insets.getRight() - insets.getLeft(),
                    centerPane.getWidth());
            double height = Math2.clamp(0,
                    centerPane.getHeight() - insets.getBottom() - insets.getTop(),
                    centerPane.getHeight());
            return new Rectangle2D(insets.getLeft(), insets.getTop(), width, height);
        }, centerPane.heightProperty(), centerPane.widthProperty()));
    }

    private void addMouseEventsManager() {
        // Assigne dynamiquement la propriété contenant la position le long du
        // profil correspondant à la position de la souris
        centerPane.setOnMouseMoved(e -> {
            Transform screenToWorld = screenToWorldP.get();
            Rectangle2D rec = rectangle2DP.get();
            if (!(rec.contains(e.getX(), e.getY()))) {
                mousePositionOnProfileP.set(Double.NaN);
                return;
            }
            double position = screenToWorld.transform(e.getX(), e.getY()).getX();
            mousePositionOnProfileP.set(Math.round(position));

            line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
                Transform worldToScreen = worldToScreenP.get();
                // Pas sûr
                if (worldToScreen == null)
                    return Double.NaN;
                return worldToScreen.transform(highlightedPositionP.get(), 0).getX();
            }));
            line.startYProperty().bind(Bindings.select(rectangle2DP, "minY"));
            line.endYProperty().bind(Bindings.select(rectangle2DP, "maxY"));
            line.visibleProperty().bind(highlightedPositionP.greaterThanOrEqualTo(0));;
            System.out.println(highlightedPositionP.get());

        });
        centerPane.setOnMouseExited(e -> mousePositionOnProfileP.set(Double.NaN));
    }

    private int computeVerticalStep() {
        final int minVerticalDistance = 25;
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        double height = rectangle2DP.get().getHeight();
        double maxElevation = elevationProfileP.get().maxElevation() - elevationProfileP.get().minElevation();
        for (int step : ELE_STEPS) {
            double temp = (step * height) / maxElevation;
            if (temp >= minVerticalDistance) {
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
    }

    private void createPane() {
        // Chemin représentant la grille :
        List<PathElement> lines = new ArrayList<>();
        List<Text> texts = new ArrayList<>();
        Rectangle2D r = rectangle2DP.get();
        double minX = r.getMinX();
        double minY = r.getMinY();
        double maxX = r.getMaxX();
        double maxY = r.getMaxY();
        Transform worldToScreen = worldToScreenP.get();
        Point2D p = new Point2D(computeHorizontalStep(), -computeVerticalStep());
        double horizontalStep = worldToScreen.deltaTransform(p).getX();
        double verticalStep = worldToScreen.deltaTransform(p).getY();
        // Lignes verticales
        int horizontalKilometers = 0;
        for (double x = minX; x <= maxX; x += horizontalStep) {
            Text txt = new Text(x, maxY, String.valueOf(horizontalKilometers++));
            txt.getStyleClass().addAll("grid_label", "horizontal");
            txt.textOriginProperty().set(VPos.TOP);
            txt.setFont(Font.font("Avenir", 10));
            txt.setX(txt.getX() - txt.prefWidth(0) / 2);
            texts.add(txt);
            lines.add(new MoveTo(x, maxY));
            lines.add(new LineTo(x, minY));
        }
        double minElevation = elevationProfileP.get().minElevation();
        int verticalKey = Math2.ceilDiv((int) minElevation, computeVerticalStep())
                * computeVerticalStep();
        double minVertical = maxY - worldToScreen.transform(0, verticalKey).getY();
        int nbOfIterations = (int) ((maxY - minVertical) / verticalStep);
        // Lignes et légende horizontales
        for (double y = minVertical; y <= maxY; y+= verticalStep) {
            Text txt = new Text(minX, y, String.valueOf(verticalKey + nbOfIterations * computeVerticalStep()));
            txt.getStyleClass().addAll("grid_label", "vertical");
            txt.textOriginProperty().set(VPos.CENTER);
            txt.setFont(Font.font("Avenir", 10));
            txt.setX(txt.getX() - (txt.prefWidth(0) + 2));
            texts.add(txt);
            lines.add(new MoveTo(minX, y));
            lines.add(new LineTo(maxX, y));
            verticalKey -= computeVerticalStep();
        }
        // Màj des lignes de la grille à chaque redimensionnement
        path.getElements().setAll(lines);
        // Màj des légendes d'abscisses et ordonnées
        group.getChildren().setAll(texts);

        // Les étiquettes de la grille :
        group.getStyleClass().add("grid_label.horizontal");
        // le graphe du profil
        // la position mise en évidence

        // texte contenant les statistiques du profil
        ElevationProfile profile = elevationProfileP.get();
        textVbox.setText(String.format("Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                profile.length() / 1000.0,
                profile.totalAscent(),
                profile.totalDescent(),
                profile.minElevation(),
                profile.maxElevation()));
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
