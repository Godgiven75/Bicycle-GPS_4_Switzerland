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
    private final VBox pane = new VBox();
    private final  Text errorMessage = new Text();
    private final SequentialTransition sequentialTransition;

    public ErrorManager() {
        pane.getStylesheets().add("error.css");
        pane.getChildren().add(errorMessage);
        errorMessage.setMouseTransparent(true);
        this.sequentialTransition = new SequentialTransition(errorMessage);
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
        sequentialTransition.stop();
        errorMessage.setText(s);
        java.awt.Toolkit.getDefaultToolkit().beep();
        FadeTransition raiseOpacity =
                new FadeTransition(Duration.millis(3000), errorMessage);
        raiseOpacity.setFromValue(0);
        raiseOpacity.setToValue(0.8);
        raiseOpacity.setDuration(new Duration(200));
        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        FadeTransition lowerOpacity =
                new FadeTransition(Duration.millis(500), errorMessage);
        lowerOpacity.setFromValue(0.8);
        lowerOpacity.setToValue(0);
        lowerOpacity.setDuration(new Duration(500));
        sequentialTransition.getChildren()
                .setAll(List.of(raiseOpacity, pause, lowerOpacity));
        sequentialTransition.play();
    }
}
