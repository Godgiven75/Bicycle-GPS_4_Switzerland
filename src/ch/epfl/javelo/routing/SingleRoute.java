package ch.epfl.javelo.routing;

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
        double totalLength = 0.0;
        for (Edge e : edges) {
            totalLength += e.length();
        }
        return totalLength;
    }

    /**
     * Retourne la totalité des arêtes de l'itinéraire
     * @return la totalité des arêtes de l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        return edges;
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
        l.add(edges.get(edges.size()-1).toPoint());
        return l;
    }

    private int binarySearchIndex(double position) {
        int binarySearchResult = Arrays.binarySearch(nodePositions, position);
        if (binarySearchResult >= 0 ) {
            if (binarySearchResult == nodePositions.length - 1)
                return binarySearchResult - 1;
            return binarySearchResult;
        }
        return - binarySearchResult - 2;
    }

    // @param position is the position on the itinerary, returns the position on the edge
    private double positionOnEdge(double position) {
        int binarySearchIndex = binarySearchIndex(position);
        double anteriorLength = 0;
        for (int i = 0; i < binarySearchIndex; i++) {
            anteriorLength += edges.get(i).length();
        }
        return position - anteriorLength;
    }

    /**
     * Retourne le point se trouvant à la position donnée le long de l'itinéraire
     * @param position
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */
    @Override
    public PointCh pointAt(double position) {
        Edge e = edges.get(binarySearchIndex(position));
        return e.pointAt(positionOnEdge(position));
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
        Edge e = edges.get(binarySearchIndex(position));
        return e.elevationAt(positionOnEdge(position));
    }

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @param position
     * @return l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        Edge e = edges.get(binarySearchIndex(position));
        int fromNodeId = e.fromNodeId();
        int toNodeId = e.toNodeId();
        double mean = (nodePositions[fromNodeId] + nodePositions[toNodeId]) / 2.0;
        return position <= mean ? fromNodeId :  toNodeId;
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
            double positionClosestToPoint = e.positionClosestTo(point);
            closestPoint = closestPoint.min(e.fromPoint(), positionClosestToPoint, point.distanceTo(e.fromPoint()));
        }
        Edge latestEdge = edges.get(edges.size() - 1);
        closestPoint = closestPoint.min(latestEdge.pointAt(latestEdge.positionClosestTo(point)),
                latestEdge.positionClosestTo(point), point.distanceTo(latestEdge.fromPoint()));
        return closestPoint;
    }


}
