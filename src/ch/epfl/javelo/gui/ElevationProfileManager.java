package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;

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
    private Pane pane;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
                                   ReadOnlyDoubleProperty position) {
        this.profile = profile;
        this.position = position;
    }

    public Pane pane() {
        return pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {

    }


}
