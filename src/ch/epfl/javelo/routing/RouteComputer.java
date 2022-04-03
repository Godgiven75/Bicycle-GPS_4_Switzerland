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
     * @param startNodeId
     * @param endNodeId
     * @return l'itinéraire de coût total minimal allant du noeud d'identité
     * startNodeId au noeud d'identité endNodeId dans le graphe passé au
     * constructeur, ou null si aucun itinéraire n'existe. Si le noeud de départ
     * et d'arrivée sont identiques, lève IllegalArgumentException
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

         /*record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        int nbNodes = graph.nodeCount();
        double[] distanceTo = new double[nbNodes];
        int[] predecessors = new int[nbNodes];
        PriorityQueue<WeightedNode> exploring = new PriorityQueue<>();

        Arrays.fill(distanceTo, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessors, 0);
        distanceTo[startNodeId] = 0f;
        exploring.add(new WeightedNode(startNodeId, 0));

        while (!exploring.isEmpty()) {

            WeightedNode closestNode = exploring.remove();
            int closestNodeId = closestNode.nodeId();

            // Si le noeud a déjà été exploré, on l'ignore
            if (distanceTo[closestNodeId] == Float.NEGATIVE_INFINITY)
                continue;

            if (closestNodeId == endNodeId)
                return new SingleRoute(new ArrayList<>(
                        shortestItinerary(startNodeId, endNodeId, predecessors)
                ));

            //double distanceToNodeOut;
            // Pour chaque arête sortant du closestNode
            for (int i = 0; i < graph.nodeOutDegree(closestNodeId); i++) {
                // Identité de l'arête sortante du plus proche noeud
                int i_thEdgeId = graph.nodeOutEdgeId(closestNodeId, i);
                // Noeud d'arrivée de l'arête
                int edgeEndNodeId = graph.edgeTargetNodeId(i_thEdgeId);
                // Le facteur de coût correspond à un "allongement artificiel"
                // de la longueur d'une arête
                double costFactor = costFunction.costFactor(closestNodeId, i_thEdgeId);
                // Calcul de la "distance artificielle"
                double distanceToNodeOut = distanceTo[closestNodeId]
                        + (float) (costFactor * graph.edgeLength(i_thEdgeId));

                double knownDistanceToThisNode = distanceTo[edgeEndNodeId];
                if (distanceToNodeOut < knownDistanceToThisNode) {
                    predecessors[edgeEndNodeId] = (closestNodeId << 4) | i;
                    distanceTo[edgeEndNodeId] = distanceToNodeOut;
                    exploring.add(
                            new WeightedNode(edgeEndNodeId, (float) distanceToNodeOut)
                    );
                }
            }
            // Marquer le noeud exploré pour ne plus y revenir
            distanceTo[closestNodeId] = Float.NEGATIVE_INFINITY;
        }
        // Si aucun chemin n'a été trouvé
        return null;*/
        return bestRouteBetweenAStar(startNodeId, endNodeId);
    }
    private Deque<Edge> shortestItinerary(int startNodeId, int endNodeId, int[] predecessors) {
        Deque<Edge> itinerary = new LinkedList<>();
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
        return itinerary;
    }

    private Route bestRouteBetweenAStar(int startNodeId, int endNodeId) {
        //Duplication (temporaire) de code
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
        distance[startNodeId] =  (float) endPoint.distanceTo(graph.nodePoint(startNodeId));

        Queue<WeightedNode> discoveredNodes = new PriorityQueue<>();
        discoveredNodes.add(new WeightedNode(startNodeId, distance[startNodeId]));


        while (!discoveredNodes.isEmpty()) {
            WeightedNode node = discoveredNodes.remove();
            int nodeId = node.nodeId();
            if (distance[nodeId] == Float.NEGATIVE_INFINITY)
                continue;

            if (nodeId == endNodeId)
                return new SingleRoute(new ArrayList<>(shortestItinerary
                                (startNodeId, endNodeId, predecessorsEdgeAndNode)
                ));

            for (int i = 0; i < graph.nodeOutDegree(nodeId); i++) {
                int edgeId = graph.nodeOutEdgeId(nodeId, i);
                int edgeTargetNodeId = graph.edgeTargetNodeId(edgeId);

                double costFactor = costFunction.costFactor(nodeId, edgeId);
                double distanceToEndPoint = endPoint.distanceTo(graph.nodePoint(edgeTargetNodeId));
                double distanceToTargetNodeId = Math.fma(graph.edgeLength(edgeId), costFactor, distance[nodeId]);

                if (distanceToTargetNodeId < distance[edgeTargetNodeId]) {
                    predecessorsEdgeAndNode[edgeTargetNodeId] = (node.nodeId() << 4) | i;
                    distance[edgeTargetNodeId] = (float) distanceToTargetNodeId;
                    discoveredNodes.add(new WeightedNode(edgeTargetNodeId, (float) (distanceToTargetNodeId +  distanceToEndPoint)));
                }
            }
            distance[node.nodeId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }
}
