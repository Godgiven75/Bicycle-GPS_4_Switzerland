package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class RouteComputer {
    Graph graph;
    CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Retourne l'itinéraire de coût total minimal allant du noeud d'identité startNodeId au noeud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe. Si le noeud de départ et d'arrivée
     * sont identiques, lève IllegalArgumentException
     * @param startNodeId
     * @param endNodeId
     * @return l'itinéraire de coût total minimal allant du noeud d'identité startNodeId au noeud d'identité endNodeId
     * dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe. Si le noeud de départ et d'arrivée
     * sont identiques, lève IllegalArgumentException
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        int nodesNb = graph.nodeCount();
        float[] distance = new float[nodesNb];
        Set<Integer> explored = new HashSet<>();
        PriorityQueue<Integer> en_exploration = new PriorityQueue<>();

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0f;
        en_exploration.add(startNodeId);

        while(!en_exploration.isEmpty()) {

        }


        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

    }

}
