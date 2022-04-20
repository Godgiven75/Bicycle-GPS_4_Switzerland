package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement représentant un point de passage.
 *
 * @param p la position du point de passage dans le système de coordonnées Suisse
 * @param closestNodeId l'identité du noeud JaVelo le plus proche de ce point de
 * passage
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public record Waypoint(PointCh p, int closestNodeId) {}
