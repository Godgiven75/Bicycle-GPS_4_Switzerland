package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

import static ch.epfl.javelo.routing.Edge.of;

public class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;


    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Retourne l'itinéraire de coût total minimal allant du noeud d'identité
     * startNodeId au noeud d'identité endNodeId dans le graphe passé au
     * constructeur, ou null si aucun itinéraire n'existe. Si le noeud de départ
     * et d'arrivée sont identiques, lève IllegalArgumentException
     *
     * @param startNodeId noeud de départ
     * @param endNodeId noeud d'arrivée
     * @return l'itinéraire de coût total minimal allant du noeud d'identité
     * startNodeId au noeud d'identité endNodeId dans le graphe passé au
     * constructeur, ou null si aucun itinéraire n'existe. Si le noeud de départ
     * et d'arrivée sont identiques, lève IllegalArgumentException
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }
        
        float[] distance = new float[graph.nodeCount()];
        int[] predecessorsEdgeAndNode = new int[distance.length];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        PointCh endPoint = graph.nodePoint(endNodeId);
        distance[startNodeId] =  (float) endPoint.distanceTo(
                graph.nodePoint(startNodeId));

        Queue<WeightedNode> discoveredNodes = new PriorityQueue<>();
        discoveredNodes.add(new WeightedNode(startNodeId,
                distance[startNodeId]));


        while (!discoveredNodes.isEmpty()) {
            WeightedNode node = discoveredNodes.remove();
            int nodeId = node.nodeId();
            if (distance[nodeId] == Float.NEGATIVE_INFINITY)
                continue;

            if (nodeId == endNodeId)
                return new SingleRoute(shortestItinerary(startNodeId, endNodeId,
                        predecessorsEdgeAndNode));

            for (int i = 0; i < graph.nodeOutDegree(nodeId); i++) {
                int edgeId = graph.nodeOutEdgeId(nodeId, i);
                int edgeTargetNodeId = graph.edgeTargetNodeId(edgeId);

                double costFactor = costFunction.costFactor(nodeId, edgeId);
                double distanceToEndPoint = endPoint.distanceTo(
                        graph.nodePoint(edgeTargetNodeId));
                double distanceToTargetNodeId = distance[nodeId]
                        + graph.edgeLength(edgeId) * costFactor;

                if (distanceToTargetNodeId < distance[edgeTargetNodeId]) {
                    predecessorsEdgeAndNode[edgeTargetNodeId] =
                            (nodeId << 4) | i;
                    distance[edgeTargetNodeId] = (float) distanceToTargetNodeId;
                    discoveredNodes.add(new WeightedNode(edgeTargetNodeId,
                            (float) (distanceToTargetNodeId
                                    + distanceToEndPoint)));
                }
            }
            distance[nodeId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }

    private List<Edge> shortestItinerary(int startNodeId, int endNodeId, int[] predecessors) {
        Deque<Edge> itinerary =  new ArrayDeque<>();
        int toNodeId = endNodeId;
        while (toNodeId != startNodeId) {
            int edgeIdAndNodeId = predecessors[toNodeId];
            int fromNodeId = Bits.extractUnsigned(edgeIdAndNodeId, 4, 28);
            int edgeId = graph.nodeOutEdgeId(
                    fromNodeId,
                    Bits.extractUnsigned(edgeIdAndNodeId, 0, 4)
            );
            itinerary.offerFirst(of(graph, edgeId, fromNodeId, toNodeId));
            toNodeId = fromNodeId;
        }
        return new ArrayList<>(itinerary);
    }
}