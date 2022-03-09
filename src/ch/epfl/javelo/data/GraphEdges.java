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

    private final static int OFFSET_EDGES_BUFFER = Integer. BYTES + Short.BYTES + Short.BYTES + Short.BYTES;

    /**
     * Retourne vrai si et seulement si l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     * @param edgeId
     * @return vrai si et seulement si l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.get(edgeId * OFFSET_EDGES_BUFFER) < 0;
    }

    /**
     * Retourne l'identité du noeud destination de l'arête d'identité donnée
     * @param edgeId
     * @return l'identité du noeud destination de l'arête d'identité donnée
     */
    public int targetNodeId(int edgeId) {
        int idWithEdgeDirection = edgesBuffer.getInt(edgeId * OFFSET_EDGES_BUFFER);
        return  Bits.extractUnsigned(idWithEdgeDirection, 0,31);

    }

    /**
     * Retourne la longueur, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        int shift = 4;
        int length = Q28_4.ofInt(edgesBuffer.getShort(OFFSET_EDGES_BUFFER * edgeId + 4));
        return Q28_4.asDouble(length);
    }

    /**
     * Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return le dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {
        return edgesBuffer.getShort(OFFSET_EDGES_BUFFER * edgeId + 6);
    }

    /**
     * Retourne vrai si et seulement si l'arête d'identité donnée possède un profil
     * @param edgeId
     * @return vrai si et seulement si l'arête d'identité donnée possède un profil
     */
    public boolean hasProfile(int edgeId) {
        int startOfRange = 30;
        int rangeLength = 2;

        return Bits.extractUnsigned(edgeId, startOfRange, rangeLength ) != profileTypes.NO_PROFILE.ordinal();
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède
     * pas de profil
     * @param edgeId
     * @return le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède
     */
    public float[] profileSamples(int edgeId) {
        if(!hasProfile(edgeId)) return new float[]{};

        int profileTypeValue = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);

        profileTypes profileType;

        switch (profileTypeValue) {
            case 1 -> profileType = profileTypes.UNCOMPRESSED;
            case 2 -> profileType = profileTypes.COMPRESSED_Q44;
            case 3 -> profileType = profileTypes.COMPRESSED_Q04;
            default -> profileType = profileTypes.NO_PROFILE;
        }
        int shift = 1;
        int lengthToQ28_4 = Q28_4.ofInt(edgesBuffer.getShort(OFFSET_EDGES_BUFFER * edgeId + shift));
        int twoToQ28_4 = Q28_4.ofInt(2);

        int numberOfSamples = 1 + Math2.ceilDiv(lengthToQ28_4, twoToQ28_4);
        int firstSampleToQ28_4 = Q28_4.ofInt(Bits.extractUnsigned(elevations.get(edgeId), 16, 16));
        float firstSample = Q28_4.asFloat(firstSampleToQ28_4);

        float[] samples = new float[numberOfSamples];

        samples[0] = firstSample;

        int length = profileType == profileTypes.UNCOMPRESSED
                ? 16
                : profileType == profileTypes.COMPRESSED_Q44
                ? 8
                : 4;

        int samplesPerShort = 16 / length;

        int i = 1;
        while(i < numberOfSamples) {
            for (int j = 0; j < samplesPerShort; ++j) {

                int start = j * length;
                int sampleToQ28_4 = Q28_4.ofInt(Bits.extractSigned(elevations.get(edgeId + i + j), start, length));
                float difference = Q28_4.asFloat(sampleToQ28_4);

                samples[i + j] = firstSample + difference;
                ++i;
            }
        }

        return samples;
    }

    private enum profileTypes {
        NO_PROFILE,
        UNCOMPRESSED,
        COMPRESSED_Q44,
        COMPRESSED_Q04
    }

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     * @param edgeId
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        int shift = 8;
        return edgesBuffer.getShort(edgeId * OFFSET_EDGES_BUFFER + 8);
    }
}


