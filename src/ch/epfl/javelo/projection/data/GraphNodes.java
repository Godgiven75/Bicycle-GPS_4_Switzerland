package ch.epfl.javelo.projection.data;

import java.nio.IntBuffer;

/**
 * Représente le tableau de tous les noeuds du graphe Javelo
 */
public record GraphNodes(IntBuffer buffer) {
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * Retourne le nombre total de noeuds
     * @return un entier correspond au nombre total de noeuds dans la mémoire tampon
     */
    public int count() {
        return buffer.capacity()/3;
    }

    /**
     * Retourne la coordonnée E du noeud d'identité donnée
     * @param nodeId
     * @return la coordonnée E du noeud d'identité donnée
     */
    public double nodeE(int nodeId) {
        return buffer.get(3 * nodeId + OFFSET_E);
    }

    /**
     * Retourne la coordonnée N du noeud d'identité donnée
     * @param nodeId
     * @return la coordonnée N du noeud d'identité donnée
     */
    public double nodeN(int nodeId) {
        return buffer.get(3 * nodeId + OFFSET_N);
    }

    /**
     * Retourne le nombre d'arêtes sortant du noeud d'identité donné
     * @param nodeId
     * @return le nombre d'arêtes sortant du noeud d'identité donné
     */
    public int outDegree(int nodeId) {
        return buffer.get(3 * nodeId + OFFSET_OUT_EDGES);
    }

    /**
     * Retourne l'identité de la edgeIndex-ième arête sortant du noeud d'identité nodeId
     * @param nodeId
     * @param edgeIndex
     * @return l'identité de la edgeIndex-ième arête sortant du noeud d'identité nodeId
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return buffer.get( (3 * nodeId + OFFSET_OUT_EDGES)
    }


}
