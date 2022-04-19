package ch.epfl.javelo.gui;

/**
 * Représente les paramètres du fond de carte présenté dans l'interface graphique.
 *
 * @param zoomLevel le niveau de zoom
 * @param xImage la coordonnée x du coin haut-gauche de la portion de carte affichée
 * dans le système WGS 84, au niveau de zoom donné
 * @param yImage la coordonnée y du coin haut-gauche de la portion de carté affichée
 * dans le système WGS 84, au niveau de zoom donné
 */
public record MapViewParameters(int zoomLevel, int xImage, int yImage) {


    public int topLeft() {

    }
}
