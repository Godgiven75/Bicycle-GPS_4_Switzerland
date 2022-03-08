package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Représente le tableau de toutes les arêtes du graphe JaVelo
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    //private final static int OFFSET_BYTE_BUFFER = 4;

    /**
     * Retourne vrai si et seulement si l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     * @param edgeId
     * @return vrai si et seulement si l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt(edgeId * Integer.BYTES) < 0;
    }

    /**
     * Retourne l'identité du noeud destination de l'arête d'identité donnée
     * @param edgeId
     * @return l'identité du noeud destination de l'arête d'identité donnée
     */
    public int targetNodeId(int edgeId) {
        int i = edgesBuffer.getInt(edgeId * Integer.BYTES);
        return (int) Short.toUnsignedInt((short) i);
    }

    /**
     * Retourne la longueur, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        short length = edgesBuffer.getShort(Integer.BYTES * edgeId + Byte.BYTES);
        return Q28_4.asDouble(length);
    }

    /**
     * Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return le dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(elevations.get(edgeId));
    }

    /**
     * Retourne vrai si et seulement si l'arête d'identité donnée possède un profil
     * @param edgeId
     * @return vrai si et seulement si l'arête d'identité donnée possède un profil
     */
    public boolean hasProfile(int edgeId) {
        int startOfRange = 30;
        int rangeLength = 2;

        return Bits.extractUnsigned(edgeId, startOfRange, rangeLength ) != 0;
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède
     * pas de profil
     * @param edgeId
     * @return le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède
     */
    public float[] profileSamples(int edgeId) {

        if(!hasProfile(edgeId)) return new float[]{};
        //Doit-on changer le traitement en fonction des types de données
        int lengthToQ28_4 = Q28_4.ofInt(edgesBuffer.getShort(Integer.BYTES * edgeId + Byte.BYTES));
        int twoToQ28_4 = Q28_4.ofInt(2);
        int numberOfSamples = 1 + Math2.ceilDiv( lengthToQ28_4, twoToQ28_4);

        float[] samples = new float[numberOfSamples];

        float firstSample = Q28_4.asFloat(elevations.get(edgeId));
        samples[0] = firstSample;

        for(int i = 1; i < numberOfSamples; ++i) {
            samples[i] = Q28_4.asFloat(elevations.get(edgeId + i));
        }

        return samples;
    }

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     * @param edgeId
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        return 0;
    }
}


