package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * Représente un itinéraire
 */
public interface Route {

    /**
     * Retourne l'index du segment à la position donnée (en mètres)
     * @param position
     * @return l'index du segment à la position donnée (en mètres)
     */
    int indexOfSegmentAt(double position);

    /**
     * Retourne la longueur de l'itinéraire (en mètres)
     * @return la longueur de l'itinéraire (en mètres)
     */
    double length();

    /**
     * Retourne la totalité des arêtes de l'itinéraire
     * @return la totalité des arêtes de l'itinéraire
     */
    List<Edge> edges();

    /**
     * Retourne le point se trouvant à la position donnée le long de l'itinéraire
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */
    List<PointCh> points();

    /**
     * Retourne l'altitude à la position donnée le long de l'itinéraire
     * @param position
     * @return l'altitude à la position donnée le long de l'itinéraire
     */
    PointCh pointAt(double position);

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @return l'identité du noeud appartenant à l'itinéaire et se trouvant le plus proche de la position donnée
     */
    double elevationAt(double position);

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @return l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    int nodeClosestTo(double position);

    /**
     * Retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    RoutePoint pointClosestTo(PointCh point);
}
