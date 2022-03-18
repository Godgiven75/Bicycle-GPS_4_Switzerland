package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;

import java.util.Collections;
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
        this.edges = Collections.unmodifiableList(edges);
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

    }


}
