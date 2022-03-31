package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

import static ch.epfl.javelo.routing.Edge.of;

public class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;


    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = new CityBikeCF(graph);
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
        record WeightedNode(int nodeId, float distance)
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
        WeightedNode closestNode;

        Arrays.fill(distanceTo, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessors, 0);
        distanceTo[startNodeId] = 0f;
        exploring.add(new WeightedNode(startNodeId, 0));


        while (!exploring.isEmpty()) {

            closestNode = exploring.remove();
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
        return null;
    }
    private Deque<Edge> shortestItinerary( int startNodeId, int endNodeId, int[] predecessors) {
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
}
