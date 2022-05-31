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
    private final BorderPane mainPane = new BorderPane();
    private final Pane centerPane = new Pane();
    private final Polygon polygon = new Polygon();
    private final Path path = new Path();
    private final Group group = new Group();
    private final Text textVbox = new Text();
    private final Line line = new Line();

    /**
     * Construit un nouveau gestionnaire de profil.
     * @param elevationProfileP Propriété (lecture seule) contenant le profil
     * à afficher (contient null s'il n'y a pas de profil à afficher)
     * @param highlightedPositionP Propriété (lecture seule) contenant la position
     * à mettre en évidence (contient NaN si aucune position n'est à mettre en
     * évidence)
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileP,
                                   ReadOnlyDoubleProperty highlightedPositionP) {
        this.elevationProfileP = elevationProfileP;
        this.highlightedPositionP = highlightedPositionP;
        this.rectangle2DP = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        this.mousePositionOnProfileP = new SimpleDoubleProperty();
        this.screenToWorldP = new SimpleObjectProperty<>(new Affine());
        this.worldToScreenP = new SimpleObjectProperty<>(new Affine());

        addBindings();
        createSceneGraph();
        addListeners();
        addMouseEventsManager();
    }

    /**
     * Retourne le panneau contenant le dessin du profil.
     *
     * @return le panneau contenant le dessin du profil
     */
    public Pane pane() {
        return mainPane;
    }

    /**
     * Retourne une propriété en lecture seule contenant la position du pointeur
     * de la souris le long du profil, ou NaN si le pointeur de la souris ne
     * se trouve pas au-dessus du profil.
     *
     * @return une propriété en lecture seule contenant la position du pointeur
     * de la souris le long du profil, ou NaN si le pointeur de la souris ne
     * se trouve pas au-dessus du profil
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileP;
    }

    private void createSceneGraph() {
        mainPane.getStylesheets().add("elevation_profile.css");
        VBox bottomPane = new VBox();
        bottomPane.setId("profile_data");
        mainPane.setBottom(bottomPane);
        mainPane.setCenter(centerPane);
        polygon.setId("profile");
        path.setId("grid");
        centerPane.getChildren().add(line);
        centerPane.getChildren().add(path);
        centerPane.getChildren().add(polygon);
        centerPane.getChildren().add(group);
        bottomPane.getChildren().add(textVbox);
    }
    private void addListeners() {
        rectangle2DP.addListener((p, o, n) -> {
            try {
                screenToWorldP.set(screenToWorld());
                worldToScreenP.set(worldToScreen());
                displayProfile();
                createPane();
                updateProfileStatistics();
            } catch (NonInvertibleTransformException error) {
                throw new Error(error);
            }

        });

        elevationProfileP.addListener((p, o, n) -> {
            try {
                if (elevationProfileP.get() != null) {
                    if (rectangle2DP.get().getHeight() == 0 || rectangle2DP.get().getWidth() == 0)
                        return;
                    screenToWorldP.set(screenToWorld());
                    worldToScreenP.set(worldToScreen());
                    updateProfileStatistics();
                    displayProfile();
                    createPane();
                }
            } catch(NonInvertibleTransformException error){
                throw new Error(error);
            }
        });
    }
    private void addBindings() {
        // Lie la propriété contenant le rectangle aux propriétés contenant la
        // largeur et la longueur du panneau central
        rectangle2DP.bind(Bindings.createObjectBinding(() -> {
            Insets insets = new Insets(10, 10, 20, 40);
            double width = Math2.clamp(0,
                    centerPane.getWidth() - insets.getRight() - insets.getLeft(),
                    centerPane.getWidth());
            double height = Math2.clamp(0,
                    centerPane.getHeight() - insets.getBottom() - insets.getTop(),
                    centerPane.getHeight());
            return new Rectangle2D(insets.getLeft(), insets.getTop(), width, height);
        }, centerPane.heightProperty(), centerPane.widthProperty()));

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            Transform worldToScreen = worldToScreenP.get();
            return worldToScreen.transform(highlightedPositionP.get(), 0).getX();
        }, worldToScreenP, highlightedPositionP));
        line.startYProperty().bind(Bindings.select(rectangle2DP, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle2DP, "maxY"));
        line.visibleProperty().bind(highlightedPositionP.greaterThanOrEqualTo(0));
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

        // Lignes et légende verticales
        int horizontalKilometers = 0;
        for (double x = minX; x <= maxX; x += horizontalStep) {
            Text txt = new Text(x, maxY, String.valueOf(horizontalKilometers));
            txt.getStyleClass().addAll("grid_label", "horizontal");
            txt.textOriginProperty().set(VPos.TOP);
            txt.setFont(Font.font("Avenir", 10));
            txt.setX(txt.getX() - txt.prefWidth(0) / 2);
            texts.add(txt);
            lines.add(new MoveTo(x, maxY));
            lines.add(new LineTo(x, minY));
            horizontalKilometers += computeHorizontalStep() / 1000;
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

    }
    // Affiche les statistiques du profil
    private void updateProfileStatistics() {
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

    // Inverse la transformation précédente en faisant passer du système de
    // coordonnées du "monde réel" au système de coordonnées du panneau JavaFX
    // contenant la grille.
    private Transform worldToScreen() throws NonInvertibleTransformException {
        return screenToWorld().createInverse();
    }
}