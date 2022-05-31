package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

/**
 * Gère l'affichage des messages d'erreur.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class ErrorManager {
    public static final int APPEARANCE_DURATION = 200;
    public static final int PAUSE_DURATION = 2000;
    public static final int DISAPPEARANCE_DURATION = 500;
    public static final int TOTALLY_TRANSPARENT = 0;
    public static final double ALMOST_OPAQUE = 0.8;
    private final VBox pane;
    private final Text errorMessage = new Text();
    private final SequentialTransition sequentialTransition;

    /**
     * Construit le panneau contenant le message d'erreur ainsi que l'animation
     * d'affichage du message à l'écran.
     */
    public ErrorManager() {
        this.pane = new VBox(errorMessage);
        pane.getStylesheets().add("error.css");
        pane.setMouseTransparent(true);
        this.sequentialTransition = new SequentialTransition(pane);

        FadeTransition raiseOpacity =
                new FadeTransition(Duration.millis(APPEARANCE_DURATION), pane);
        raiseOpacity.setFromValue(TOTALLY_TRANSPARENT);
        raiseOpacity.setToValue(ALMOST_OPAQUE);

        PauseTransition pause = new PauseTransition(Duration.millis(PAUSE_DURATION));
        FadeTransition lowerOpacity =
                new FadeTransition(Duration.millis(DISAPPEARANCE_DURATION), pane);
        lowerOpacity.setFromValue(ALMOST_OPAQUE);
        lowerOpacity.setToValue(TOTALLY_TRANSPARENT);

        sequentialTransition.getChildren()
                .setAll(List.of(raiseOpacity, pause, lowerOpacity));
    }

    /**
     * Retourne le panneau sur lequel apparaissent les messages d'erreur.
     *
     * @return le panneau de type Pane sur lequel apparaissent les messages
     * d'erreur
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Fait apparaître temporairement un (court) message d'erreur, accompagné d'un
     * son indiquant l'erreur.
     *
     * @param s le (court) message d'erreur
     */
    public void displayError(String s) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorMessage.setText(s);
        sequentialTransition.stop();
        sequentialTransition.play();
    }
}
