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
     * Retourne l'itinéraire de coût total minimal allant du noeud d'identité
     * startNodeId au noeud d'identité endNodeId dans le graphe passé au
     * constructeur, ou null si aucun itinéraire n'existe. Si le noeud de départ
     * et d'arrivée sont identiques, lève IllegalArgumentException
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

        int nodesNb = graph.nodeCount();
        float[] distanceTo = new float[nodesNb];
        int[] predecessor = new int[nodesNb];
        List<Edge> edges = Arrays.asList(new Edge[nodesNb]);
        Deque<Edge> pathEdges = new LinkedList<>();
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();
        CityBikeCF cityBikeCF = new CityBikeCF(graph);
        WeightedNode closestNode = new WeightedNode(0, 0);

        Arrays.fill(distanceTo, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0);
        distanceTo[startNodeId] = 0f;
        en_exploration.add(new WeightedNode(startNodeId, 0));
        float distanceToNodeOut = 0f;

        while(!en_exploration.isEmpty()) {

            closestNode = en_exploration.remove();
            int closestNodeId = closestNode.nodeId();

            // Si le noeud a déjà été exploré, on l'ignore
            if (closestNode.distance() == Float.NEGATIVE_INFINITY) continue;

            if (closestNodeId == endNodeId) {
                int j = endNodeId;
                // Construire la liste d'edges dans l'ordre de l'itinéraire
                while (j != startNodeId) {
                    Edge e = edges.get(j);
                    pathEdges.offerFirst(e);
                    j = edges.get(j).fromNodeId();
                }
                /*
                for (int i = predecessor.length - 1; i >= 0; i++) {
                    for (Edge e : edges) {
                        if (e.toNodeId() == predecessor[j]) {
                            pathEdges.offerFirst(e);
                            j = predecessor[j];
                            break;
                        }
                    }
                }

                 */
                return new SingleRoute(new ArrayList<>(pathEdges));
            }

            // Pour chaque arête sortant du closestNode
            for (int i = 0; i < graph.nodeOutDegree(closestNodeId); i++) {
                // Identité de l'arête sortante du plus proche noeud
                int i_thEdgeId = graph.nodeOutEdgeId(closestNodeId, i);
                // Noeud d'arrivée de l'arête
                int edgeEndNodeId = graph.edgeTargetNodeId(i_thEdgeId);
                // Facteur de coût -> allongement artificiel de la longueur d'une arête
                double coeff = cityBikeCF.costFactor(closestNodeId, i_thEdgeId);
                // Calcul distance
                distanceToNodeOut = distanceTo[closestNodeId]
                        + (float) (coeff * graph.edgeLength(i_thEdgeId));
                // Affectation de l'identité du noeud précédent
                predecessor[edgeEndNodeId] = closestNodeId;

                float knownDistanceToThisNode = distanceTo[edgeEndNodeId];
                if (distanceToNodeOut < knownDistanceToThisNode) {
                    distanceTo[edgeEndNodeId] = distanceToNodeOut;
                    en_exploration.add(
                            new WeightedNode(edgeEndNodeId, distanceToNodeOut)
                    );
                    edges.set(
                            edgeEndNodeId,
                            Edge.of(graph, i_thEdgeId, closestNodeId, edgeEndNodeId)
                    );
                }
            }
            // Marquer le noeud exploré pour ne plus y revenir
            distanceTo[closestNodeId] = Float.NEGATIVE_INFINITY;

        }
        // Si aucun chemin n'a été trouvé
        return null;
    }

}
