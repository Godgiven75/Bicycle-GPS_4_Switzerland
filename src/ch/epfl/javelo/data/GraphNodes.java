package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;

import java.nio.IntBuffer;

import static ch.epfl.javelo.Bits.extractUnsigned;

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
        return buffer.capacity()/NODE_INTS;
    }

    /**
     * Retourne la coordonnée E du noeud d'identité donnée
     * @param nodeId
     * @return la coordonnée E du noeud d'identité donnée
     */
    public double nodeE(int nodeId) {
        return buffer.get(NODE_INTS * nodeId + OFFSET_E);
    }

    /**
     * Retourne la coordonnée N du noeud d'identité donnée
     * @param nodeId
     * @return la coordonnée N du noeud d'identité donnée
     */
    public double nodeN(int nodeId) {
        return buffer.get(NODE_INTS * nodeId + OFFSET_N);
    }

    /**
     * Retourne le nombre d'arêtes sortant du noeud d'identité donné
     * @param nodeId
     * @return le nombre d'arêtes sortant du noeud d'identité donné
     */
    public int outDegree(int nodeId) {
        return buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES) >>> 28;
    }

    /**
     * Retourne l'identité de la edgeIndex-ième arête sortant du noeud d'identité nodeId
     * @param nodeId
     * @param edgeIndex
     * @return l'identité de la edgeIndex-ième arête sortant du noeud d'identité nodeId
     */
    public int edgeId(int nodeId, int edgeIndex) {
        //assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        System.out.println(Integer.toBinaryString(
                buffer.get(Bits.extractUnsigned(NODE_INTS * nodeId + OFFSET_OUT_EDGES, 0, 28))));
        return (buffer.get(Bits.extractUnsigned(NODE_INTS * nodeId + OFFSET_OUT_EDGES, 0, 28)) << 4)
                + edgeIndex - 1;
    }

}
