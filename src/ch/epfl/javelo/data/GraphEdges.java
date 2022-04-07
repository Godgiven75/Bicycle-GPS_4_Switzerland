package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Représente le tableau de toutes les arêtes du graphe JaVelo.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int OFFSET_TARGET_NODE_ID = 0;
    private static final int OFFSET_LENGTH = OFFSET_TARGET_NODE_ID + Integer.BYTES;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH + Short.BYTES;
    private static final int OFFSET_ATTRIBUTES_INDEX = OFFSET_ELEVATION_GAIN + Short.BYTES;
    private final static int BYTES_FOR_EDGES = OFFSET_ATTRIBUTES_INDEX + Short.BYTES;

    /**
     * Retourne vrai si et seulement si l'arête d'identité donnée va dans le sens
     * inverse de la voie OSM dont elle provient.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return vrai si et seulement si l'arête d'identité donnée va dans le sens
     * inverse de la voie OSM dont elle provient
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.get(edgeId * BYTES_FOR_EDGES + OFFSET_TARGET_NODE_ID) < 0;
    }

    /**
     * Retourne l'identité du noeud destination de l'arête d'identité donnée.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return l'identité du noeud destination de l'arête d'identité donnée
     */
    public int targetNodeId(int edgeId) {
        int idWithEdgeDirection = edgesBuffer.getInt(edgeId * BYTES_FOR_EDGES + OFFSET_TARGET_NODE_ID);
        return isInverted(edgeId) ? ~idWithEdgeDirection : idWithEdgeDirection;
    }

    /**
     * Retourne la longueur, en mètres, de l'arête d'identité donnée
     *
     * @param edgeId l'identité de l'arête
     *
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        int length = Short.toUnsignedInt(edgesBuffer.getShort(BYTES_FOR_EDGES * edgeId + OFFSET_LENGTH));
        return Q28_4.asDouble(length);
    }

    /**
     * Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return le dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {
        int elevationGain = Short.toUnsignedInt(edgesBuffer.getShort(BYTES_FOR_EDGES * edgeId + OFFSET_ELEVATION_GAIN));
        return Q28_4.asDouble(elevationGain);
    }

    /**
     * Retourne vrai si et seulement si l'arête d'identité donnée possède un profil.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return vrai si et seulement si l'arête d'identité donnée possède un profil
     */
    public boolean hasProfile(int edgeId) {
        int startOfRange = 30;
        int rangeLength = 2;

        return Bits.extractSigned(profileIds.get(edgeId), startOfRange, rangeLength ) != profileTypes.NO_PROFILE.ordinal();
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède
     */
    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId))
            return new float[]{};

        int profileTypeValue = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
        int firstProfileId = Bits.extractUnsigned(profileIds.get(edgeId), 0, 29);

        profileTypes profileType = allProfileTypes.get(profileTypeValue);


        int lengthToQ28_4 = Short.toUnsignedInt(edgesBuffer.getShort(BYTES_FOR_EDGES * edgeId + OFFSET_LENGTH));
        int twoToQ28_4 = Q28_4.ofInt(2);
        int numberOfSamples = 1 + Math2.ceilDiv(lengthToQ28_4, twoToQ28_4);


        float[] samples = new float[numberOfSamples];

        int sampleLength = 0;
        switch (profileType) {
            case UNCOMPRESSED -> sampleLength = Short.SIZE;
            case COMPRESSED_Q44 -> sampleLength = Short.SIZE / 2;
            case COMPRESSED_Q04 -> sampleLength = Short.SIZE / 4;
        }


        int samplesPerShort = Short.SIZE / sampleLength; // nombre d'échantillons contenu dans un des short du buffer, dépend du format de compression

        boolean inverted = isInverted(edgeId);

        int k = inverted ? numberOfSamples - 1 : 0;

        for (int i = 0; i < elevations().capacity() - 1; ++i) {
            int start =  Short.SIZE  - sampleLength;

            int elevationsIndex =  firstProfileId + i ;
            int s = Short.toUnsignedInt(elevations.get(elevationsIndex));

            if (i == 0)  {
                samples[k] = Q28_4.asFloat(s);
                k += inverted ? -1 : 1;
                continue;
            }

            for (int j = 0; j < samplesPerShort; ++j) {
                if (k < 0 || k >= samples.length) return samples;

                if (profileType == profileTypes.UNCOMPRESSED){
                    samples[k] = Q28_4.asFloat(s);
                } else {
                    int sample =  Bits.extractSigned(s, start, sampleLength);
                    float difference = Q28_4.asFloat(sample);
                    int indexOfPreviousSample = inverted ?  k + 1 : k - 1;
                    samples[k] = difference + samples[indexOfPreviousSample];
                    start -=  sampleLength;
                }
                k += inverted ? -1 : 1;
            }
        }
     return samples;
    }

    public static float[] reverse(float[] array) { //attention, ne pas laisser en public
        float[] newArray = new float[array.length];

        for (int i = 0; i < array.length; i++) {
            newArray[array.length - 1 - i] = array[i];
        }

        return newArray;
    }

    private enum profileTypes {
        NO_PROFILE,
        UNCOMPRESSED,
        COMPRESSED_Q44,
        COMPRESSED_Q04
    }
    private final static List<profileTypes> allProfileTypes = List.of(profileTypes.values());

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * BYTES_FOR_EDGES + OFFSET_ATTRIBUTES_INDEX));
    }
}


