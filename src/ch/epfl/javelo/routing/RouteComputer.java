package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

public class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

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

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        int nodesNb = graph.nodeCount();
        float[] distance = new float[nodesNb];
        List<Edge> edges = new ArrayList<>();
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        CityBikeCF cityBikeCF = new CityBikeCF(graph);
        WeightedNode closestNode = new WeightedNode(0, 0);

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0f;
        en_exploration.add(new WeightedNode(startNodeId, 0));
        float dist = 0f;

        while(!en_exploration.isEmpty()) {
            closestNode = en_exploration.remove();

            // Si le noeud a déjà été exploré
            if (closestNode.distance() == Float.NEGATIVE_INFINITY) continue;

            if (closestNode.nodeId() == endNodeId) {
                return new SingleRoute(edges);
            }

            for (int i = 0; i < graph.nodeOutDegree(closestNode.nodeId()); i++) {
                int i_thEdgeId = graph.nodeOutEdgeId(closestNode.nodeId(), i);
                int edgeEndNodeId = graph.edgeTargetNodeId(i_thEdgeId);
                // Facteur de coût -> allongement artificiel de la longueur d'une arête
                double coeff = cityBikeCF.costFactor(closestNode.nodeId(), i_thEdgeId);
                dist = distance[closestNode.nodeId()] + (float) (coeff * graph.edgeLength(i_thEdgeId));

                if (dist < distance[edgeEndNodeId]) {
                    distance[edgeEndNodeId] = dist;
                    en_exploration.add(new WeightedNode(edgeEndNodeId, dist));
                    distance[closestNode.nodeId()] = Float.NEGATIVE_INFINITY;
                    edges.add(Edge.of(graph, i_thEdgeId, closestNode.nodeId(), edgeEndNodeId));
                }
            }

        }
        // Si aucun chemin n'a été trouvé
        return null;
    }

}
