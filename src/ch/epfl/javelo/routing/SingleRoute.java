package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;

import java.util.Collections;
import java.util.List;

/**
 * Représente un itinéraire simple, càd reliant un point de départ à un point d'arrivée, sans point de passage
 * intermédiaire
 */
public final class SingleRoute {
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

    


}
