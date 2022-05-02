package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.geometry.Point2D;




/**
 * Représente les paramètres du fond de carte présenté dans l'interface graphique.
 *
 * @param zoomLevel le niveau de zoom
 * @param xImage la coordonnée x du coin haut-gauche de la portion de carte affichée
 * dans le système Web Mercator, au niveau de zoom donné
 * @param yImage la coordonnée y du coin haut-gauche de la portion de carté affichée
 * dans le système Web Mercator, au niveau de zoom donné
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public record MapViewParameters(int zoomLevel, double xImage, double yImage) {

    /**
     * Retourne un Point2D correspondant au coin en haut à gauche de l'image
     * @return un point2D correspondant au coin en haut à gauche de l'image
     */
    public Point2D topLeft() {
        return new Point2D(xImage, yImage);
    }

    /**
     * Retourne une nouvelle instance de MapViewParameters, avec les nouvelles
     * coordonnées du coin en haut à gauche de l'image, passées en arguments
     * @param newXImage la nouvelle coordonnée x du coin en haut à gauche
     * @param newYImage la nouvelle coordonnée y du coin en haut à gauche
     * @return une nouvelle instance de MapViewParameters, avec les nouvelles
     * coordonnées du coin en haut à gauche de l'image
     */
    public MapViewParameters withMinXY(double newXImage, double newYImage) {
        return new MapViewParameters(zoomLevel, newXImage, newYImage);
    }

    /**
     * Prend les coordonnées d'un point exprimées par rapport au point haut-gauche
     * de la portion de carte affichée à l'écran et retourne ce point sous la forme
     * d'une instance de PointWebMercator.
     *
     * @param x la coordonnée x du point par rapport au point en haut à gauche
     *          de l'image
     * @param y la coordonnée y du point par rapport au point en haut à gauche
     *          de l'image
     *
     * @return ce point sous la forme d'une instance de PointWebMercator
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel,x - xImage , yImage - y);
    }

    /**
     * Retourne la position x correspondante au point p pris en argument, exprimée
     * par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     *
     * @param p le point PointWebMercator
     *
     * @return la position x correspondante au point p pris en argument, exprimée
     * par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     */
    public double viewX(PointWebMercator p) {
        return p.xAtZoomLevel(zoomLevel) - xImage;
    }

    /**
     * Retourne la position x correspondante au point p pris en argument, exprimée
     * par rapport au coin haut-gauche de la portion de carte affichée à l'écran.
     *
     * @param p le point PointWebMercator
     *
     * @return la position x correspondante au point p pris en argument, exprimée
     * par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     */
    public double viewY(PointWebMercator p) {
        return p.yAtZoomLevel(zoomLevel) - yImage;
    }
}
