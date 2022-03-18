package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un itinéraire simple, càd reliant un point de départ à un point d'arrivée, sans point de passage
 * intermédiaire
 */
public final class SingleRoute implements Route {
    private final List<Edge> edges;

    /**
     * Retourne l'itinéraire simple composé des arêtes données, ou lève IllegalArgumentException si la liste d'arêtes
     * est vide
     * @param edges
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
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

    @Override
    public List<Edge> edges() {
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> l = new ArrayList<>();
        l.add(edges.get(0).fromPoint()); // Ajout du 1er point de la 1ère arête, puis du point d'arrivée de chaque arête
        for (Edge e : edges) {
            l.add(e.toPoint());
        }
        return l;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }


}
