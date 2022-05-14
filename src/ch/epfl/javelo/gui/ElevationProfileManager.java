package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * Publique et finale, gère l'affichage et l'interaction avec le profil en long
 * d'un itinéraire.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class ElevationProfileManager {
    private ReadOnlyObjectProperty<ElevationProfile> profile;
    private ReadOnlyDoubleProperty position;
    private ObjectProperty<Rectangle2D> rectangle2DP;
    private ObjectProperty<Transform> screenToWorldP;
    private ObjectProperty<Transform> worldToScreen;
    private BorderPane pane;
    private DoubleProperty mousePositionOnProfileProperty;
    private Pane centerPane;
    private VBox bottomPane;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
                                   ReadOnlyDoubleProperty position) {
        this.profile = profile;
        this.position = position;
        this.rectangle2DP = new SimpleObjectProperty<>();
        this.screenToWorldP = new SimpleObjectProperty<>();
        this.worldToScreen = new SimpleObjectProperty<>();
        this.pane = new BorderPane();
        this.centerPane = new Pane();
        this.bottomPane = new VBox();
        this.mousePositionOnProfileProperty = new SimpleDoubleProperty();

    }

    private void addListeners() {
        rectangle2DP.addListener(e -> {
            // la position mise en évidence
            Line line = new Line();
            centerPane.getChildren().add(line);
            line
        });
    }

    public Pane pane() {
        return pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return
    }

    private void createPane() {

        // contient les 2 conteneurs
        BorderPane rootPane = new BorderPane();
        rootPane.getStylesheets().add("elevation_profile");

        Pane centerPane = this.centerPane;
        rootPane.setCenter(centerPane);
        centerPane.setPadding(new Insets(10, 10, 20, 40));

        // le chemin représentant la grille
        Path path = new Path();
        centerPane.getChildren().add(path);
        path.getStyleClass().add("grid");

        // les étiquettes de la grille
        Group group = new Group();
        centerPane.getChildren().add(group);

        // le graphe du profil
        Polygon polygon = new Polygon();
        // ne pas oublier les 2 points additionnels du semi-rectangle
        double[] profilePoints = new double[];

        centerPane.getChildren().add(polygon);
        polygon.getStyleClass().add("profile");

        // la position mise en évidence
        Line line = new Line();
        centerPane.getChildren().add(line);

        Pane bottomPane = this.bottomPane;
        rootPane.setBottom(bottomPane);
        bottomPane.getStyleClass().add("profile_data");
        // texte contenant les statistiques du profil
        Text textVBox = new Text();
        bottomPane.getChildren().add(textVBox);

        this.pane = rootPane;
    }

    // Passe du système de coordonnées du panneau JavaFX contenant la grille au
    // système de coordonnées du "monde réel".
    private void screenToWorld(Transform t) {
        Affine transform = new Affine();
        transform.prependTranslation(-40, 653);
        transform.prependScale(1.0/t.getTx(), 1.0/t.getTy());

    }
}
