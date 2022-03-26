package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.javelo.routing.RoutePoint.NONE;

/**
 * Représente un itinéraire simple, càd reliant un point de départ à un point d'arrivée, sans point de passage
 * intermédiaire
 */
public final class SingleRoute implements Route {
    private final List<Edge> edges;
    private final double[] nodePositions;


    /**
     * Retourne l'itinéraire simple composé des arêtes données, ou lève IllegalArgumentException si la liste d'arêtes
     * est vide
     * @param edges
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        this.nodePositions = nodePositions();
    }

    private double[] nodePositions() {
        int nbEdges = edges.size();
        double[] nodePositions = new double[nbEdges + 1];
        int nodeId = 0;
        for(Edge e : edges) {
            nodePositions[++nodeId] = e.length() + nodePositions[nodeId - 1];
        }
        return nodePositions;
    }

    /**
     * Retourne l'index du segment de l'itinéraire contenant la position donnée, qui vaut toujours 0 dans le cas d'un
     * itinéraire simple
     * @param position
     * @return l'index du segment de l'itinéraire contenant la position donnée, qui vaut toujours 0 dans le cas d'un
     * itinéraire simple
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * Retourne la longueur de l'itinéraire, en mètres
     * @return la longueur de l'itinéraire, en mètres
     */
    @Override
    public double length() {
        return nodePositions[nodePositions.length - 1];
    }

    /**
     * Retourne la totalité des arêtes de l'itinéraire
     * @return la totalité des arêtes de l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        return List.copyOf(edges);
    }

    /**
     * Retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire
     * @return la totalité des points situés aux extrémintés des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> l = new ArrayList<>();
        for (Edge e : edges) {
            l.add(e.fromPoint());
        }
        l.add(edges.get(edges.size() - 1).toPoint());
        return l;
    }

    /**
     * Retourne le point se trouvant à la position donnée le long de l'itinéraire
     * @param position
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */
    @Override
    public PointCh pointAt(double position) {
        int binarySearchResult = Arrays.binarySearch(nodePositions, Math2.clamp(0.0, position, length()));
        if(binarySearchResult == 0)
            return edges.get(binarySearchResult).fromPoint();
        if(binarySearchResult > 0)
            return edges.get(binarySearchResult - 1).toPoint();
        int actualIndex = - binarySearchResult - 2;
        return edges.get(actualIndex).pointAt(position - nodePositions[actualIndex]);

    }

    /**
     * Retourne l'altitude à la position donnée le long de l'itinéraire, qui peut valoir NaN si l'arête contenant
     * cette position n'a pas de profil
     * @param position
     * @return l'altitude à la position donnée le long de l'itinéraire, qui peut valoir NaN si l'arête contenant
     * cette position n'a pas de profil
     */
    @Override
    public double elevationAt(double position) {
        int binarySearchResult = Arrays.binarySearch(nodePositions, Math2.clamp(0.0, position, length()));
        System.out.println(binarySearchResult);
        int binarySearchIndex = binarySearchResult;
        if(binarySearchResult == nodePositions.length - 1)
            binarySearchIndex = binarySearchResult  - 1;
        if(binarySearchResult < 0)
            binarySearchIndex = -binarySearchResult - 2;
        return edges.get(binarySearchIndex).elevationAt(position - nodePositions[binarySearchIndex]);
    }

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @param position
     * @return l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        int binarySearchResult = Arrays.binarySearch(nodePositions, Math2.clamp(0.0, position, length()));
        if(binarySearchResult >= 0) {
            if (binarySearchResult == nodePositions.length - 1)
                return edges.get(binarySearchResult - 1).toNodeId();
            return edges.get(binarySearchResult).fromNodeId();
        }
        int actualIndex = -binarySearchResult - 2;
        Edge e = edges.get(actualIndex);
        int fromNodeId = e.fromNodeId();
        int toNodeId = e.toNodeId();
        double mean = (nodePositions[actualIndex] + nodePositions[actualIndex + 1]) / 2.0;
        return position <= mean ? fromNodeId : toNodeId;
    }

    /**
     * Retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
     * @param point
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = NONE;
        for (Edge e : edges) {
            double closestPositionOnEdge = Math2.clamp(0.0, e.positionClosestTo(point), e.length());
            int nodeIndex = edges.indexOf(e);
            double closestPositionOnItinerary = nodePositions[nodeIndex] + closestPositionOnEdge;
            PointCh closestPointOnEdge = e.pointAt(closestPositionOnEdge);
            closestPoint = closestPoint.min(closestPointOnEdge, closestPositionOnItinerary, point.distanceTo(closestPointOnEdge));
        }
        return closestPoint;
    }

}
