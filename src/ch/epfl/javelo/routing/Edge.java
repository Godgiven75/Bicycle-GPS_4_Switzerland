package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Enregistrement représentant les arêtes d'un itinéraire
 * @param fromNodeId l'identité du nœud de départ
 * @param toNodeId l'identité du nœud de destination
 * @param fromPoint le point de départ de l'arête
 * @param toPoint le point d'arrivée de l'arête
 * @param length la longueur de l'arête, en mètres
 * @param profile le profil en long de l'arête

 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     * Retourne une instance de Edge dont les attributs fromNodeId et toNodeId  sont ceux donnés, les deux autres étant ceux de l'arête d'identité edgeId dans le graphe Graph
     * @param graph graphe
     * @param edgeId identité de l'arête dont on veut récupérer les attributs
     * @param fromNodeId identité du nœud de départ
     * @param toNodeId identité du nœud de destination
     * @return une instance de Edge dont les attributs fromNodeId et toNodeId  sont ceux donnés, les deux autres étant ceux de l'arête d'identité edgeId dans le graphe Graph
     */
    public Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    public double positionClosestTo(PointCh point) {

        return Math2.projectionLength()

    }

    public PointCh pointAt(double position) {

    }

    public double elevationAt(double position) {

    }
}
