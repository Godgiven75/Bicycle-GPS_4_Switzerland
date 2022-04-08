package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Enregistrement représentant les arêtes d'un itinéraire
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 *
 * @param fromNodeId l'identité du nœud de départ
 * @param toNodeId l'identité du nœud de destination
 * @param fromPoint le point de départ de l'arête
 * @param toPoint le point d'arrivée de l'arête
 * @param length la longueur de l'arête, en mètres
 * @param profile le profil en long de l'arête
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint,
                   double length, DoubleUnaryOperator profile) {

    /**
     * Retourne une instance de Edge dont les attributs fromNodeId et toNodeId
     * sont ceux donnés, les deux autres étant ceux de l'arête d'identité edgeId
     * dans le graphe.
     *
     * @param graph le graphe
     * @param edgeId l'identité de l'arête dont on veut récupérer les attributs
     * @param fromNodeId l'identité du nœud de départ
     * @param toNodeId l'identité du nœud de destination
     *
     * @return une instance de Edge dont les attributs fromNodeId et toNodeId
     * sont ceux donnés, les deux autres étant ceux de l'arête d'identité edgeId
     * dans le graphe
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId,
                graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Retourne le point se trouvant à la position donnée sur l'arête, exprimée en mètres.
     *
     * @param position la position le long de l'arête, en mètres
     *
     * @return le point se trouvant à la position donnée sur l'arête, exprimée en mètres
     */
    public PointCh pointAt(double position) {
        double e = Math2.interpolate(fromPoint.e(), toPoint.e(), position / length);
        double n = Math2.interpolate(fromPoint.n(), toPoint.n(), position / length);
        return new PointCh(e, n);
    }

    /**
     * Retourne la position le long de l'arête, en mètres, qui se trouve la plus
     * proche du point donné.
     *
     * @param point le point PointCh
     *
     * @return la position le long de l'arête, en mètres, qui se trouve la plus
     * proche du point donné
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(),
                toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * Retourne l'altitude, en mètres, à la position donnée sur l'arête.
     *
     * @param position la position sur l'arête, en mètres
     *
     * @return l'altitude, en mètres, à la position donnée sur l'arête
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
