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
        return isInverted(edgeId) ? ~idWithEdgeDirection : idWithEdgeDirection;
    }

    /**
     * Retourne la longueur, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        //int shift = 4;
        int length = Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_EDGES_BUFFER * edgeId + 4));//);
        return Q28_4.asDouble(length);
    }

    /**
     * Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return le dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {

        int elevationGain = Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_EDGES_BUFFER * edgeId + 6));
        return Q28_4.asDouble(elevationGain);
    }

    /**
     * Retourne vrai si et seulement si l'arête d'identité donnée possède un profil
     * @param edgeId
     * @return vrai si et seulement si l'arête d'identité donnée possède un profil
     */
    public boolean hasProfile(int edgeId) {
        int startOfRange = 30;
        int rangeLength = 2;

        return Bits.extractSigned(profileIds.get(edgeId), startOfRange, rangeLength ) != profileTypes.NO_PROFILE.ordinal();
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède
     * pas de profil
     * @param edgeId
     * @return le tableau des échantillons du profil de l'arête d'identité donnée, qui est vide si l'arête ne possède
     */
    public float[] profileSamples(int edgeId) {
        if(!hasProfile(edgeId)) return new float[]{};

        int profileTypeValue = profileIds.get(edgeId) >>>   Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
        int firstProfileId = Bits.extractUnsigned(profileIds.get(edgeId), 0, 29);

        profileTypes profileType = profileTypeValue == 1
                ? profileTypes.UNCOMPRESSED
                : profileTypeValue == 2
                ? profileTypes.COMPRESSED_Q44
                : profileTypes.COMPRESSED_Q04;
        System.out.println(profileType);

        int shift = 4;
        int lengthToQ28_4 = (edgesBuffer.getShort(OFFSET_EDGES_BUFFER * edgeId + shift));
        int twoToQ28_4 = Q28_4.ofInt(2);
        int numberOfSamples = 1 + Math2.ceilDiv(lengthToQ28_4, twoToQ28_4);


        float[] samples = new float[numberOfSamples];

        int length = profileType == profileTypes.UNCOMPRESSED
                ? Short.SIZE
                : profileType == profileTypes.COMPRESSED_Q44
                ? Byte.SIZE
                : Byte.SIZE / 2;

        System.out.println(length);
        int samplesPerShort = Short.SIZE / length; // nombre d'échantillons contenu dans un des short du buffer, dépend du format de compression

        boolean inverted = isInverted(edgeId);

        int k = inverted ? numberOfSamples - 1 : 0;

        for (int i = 0; i < elevations().capacity() - 1; ++i) {
            int start =  Short.SIZE  - length;

            int elevationsIndex =  firstProfileId + i ;
            int s = Short.toUnsignedInt(elevations.get(elevationsIndex));

            if (i == 0)  {
                samples[k] = Q28_4.asFloat(s);
                k += inverted ? -1 : 1;
                continue;
            }

            for (int j = 0; j < samplesPerShort; ++j) {
                if (k < 0 || k >= samples.length)
                {
                    for (float f : samples) System.out.println(f);
                    return samples;
                }

                if (profileType == profileTypes.UNCOMPRESSED){
                    samples[k] = Q28_4.asFloat(s);
                } else {
                    int sample =  Bits.extractSigned(s, start, length);
                    float difference = Q28_4.asFloat(sample);
                    int indexOfPreviousSample = inverted ?  k + 1 : k - 1;
                    samples[k] = difference + samples[indexOfPreviousSample];
                    start -=  length;
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

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     * @param edgeId
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        int shift = 8;
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * OFFSET_EDGES_BUFFER + shift));
    }
}


