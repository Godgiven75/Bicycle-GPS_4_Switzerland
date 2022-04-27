package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 * Représente le tableau de toutes les arêtes du graphe JaVelo.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 *
 * @param edgesBuffer mémoire tampon contenant la valeur des attributs généraux
 * des arêtes
 *
 * @param profileIds mémoire tampon contenant la valeur des attributs concernant
 * le profil des arêtes
 *
 * @param elevations mémoire tampon contenant la totalité des échantillons des
 * profils, compressés ou non
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
        int idWithEdgeDirection = edgesBuffer.getInt(
                edgeId * BYTES_FOR_EDGES + OFFSET_TARGET_NODE_ID);
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
        int length = Short.toUnsignedInt(
                edgesBuffer.getShort(BYTES_FOR_EDGES * edgeId + OFFSET_LENGTH));
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
        int elevationGain = Short.toUnsignedInt(
                edgesBuffer.getShort(BYTES_FOR_EDGES * edgeId + OFFSET_ELEVATION_GAIN));
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
        return Bits.extractSigned(profileIds.get(edgeId), startOfRange, rangeLength)
                != ProfileTypes.NO_PROFILE.ordinal();
    }
    // Type énuméré représentant les différents types de profil
    private enum ProfileTypes {
        NO_PROFILE,
        UNCOMPRESSED,
        COMPRESSED_Q44,
        COMPRESSED_Q04
    }
    // Liste contenant les différents types de profil
    private final static List<ProfileTypes> allProfileTypes = List.of(ProfileTypes.values());


    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil
     */
    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId))
            return new float[]{};

        float[] samples = new float[numberOfSamplesInProfile(edgeId)];
        ProfileTypes profileType = profileType(edgeId);
        float firstUncompressedSample = Q28_4.asFloat(Short.toUnsignedInt(
                elevations.get(firstProfileId(edgeId))));
        samples[0] = firstUncompressedSample;

        switch (profileType) {
            case UNCOMPRESSED -> extractSamplesUncompressed(edgeId, samples);
            case COMPRESSED_Q44 -> extractSamplesCompressed(edgeId, Short.SIZE / 2,
                    samples);
            case COMPRESSED_Q04 -> extractSamplesCompressed(edgeId, Short.SIZE / 4,
                    samples);
        }

        if (isInverted(edgeId))
            reverse(samples);
        return samples;
    }

    private void extractSamplesUncompressed(int edgeId, float[] samples) {
        int sampleIndex = 1;
        for (int i = 1; i < elevations.capacity() - 1; i++) {
            int elevationsIndex = firstProfileId(edgeId) + i ;
            int sample = Short.toUnsignedInt(elevations.get(elevationsIndex));
            if (sampleIndex >= samples.length)
                break;
            samples[sampleIndex] = Q28_4.asFloat(sample);
            sampleIndex++;
        }
    }
    private void extractSamplesCompressed(int edgeId, int sampleRangeLength,float[] samples) {
        int samplesPerShort = Short.SIZE / sampleRangeLength;
        int samplesIndex = 1;
        boolean isSamplesFull = false;

        for (int i = 1; i < elevations().capacity() - 1; ++i) {
            if(isSamplesFull)
                break;
            int start = Short.SIZE  - sampleRangeLength;
            int elevationsIndex = firstProfileId(edgeId) + i ;
            int s = Short.toUnsignedInt(elevations.get(elevationsIndex));

            for (int j = 0; j < samplesPerShort; ++j) {
                if (samplesIndex > samples.length - 1) {
                    isSamplesFull = true;
                    break;
                }

                int sample = Bits.extractSigned(s, start, sampleRangeLength);
                float difference = Q28_4.asFloat(sample);
                int indexOfPreviousSample = samplesIndex - 1;
                samples[samplesIndex] = difference + samples[indexOfPreviousSample];
                start -= sampleRangeLength;
                samplesIndex++;
            }

        }

    }

    //Inverse les éléments du tableau passé en argument
    private void reverse(float[] arr) {
        float temp;
        for (int i = 0; i < arr.length / 2; i++) {
            temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
    }

    // Retourne le type de profil d' l'arête d'identité edgeId
    private ProfileTypes profileType(int edgeId) {
        int profileTypeValueStartOfRange = 30;
        int profileTypeValueRangeLength = 2;
        int profileTypeValue = Bits.extractUnsigned(profileIds.get(edgeId),
                profileTypeValueStartOfRange,
                profileTypeValueRangeLength);
        return allProfileTypes.get(profileTypeValue);
    }

    // Retourne l'identité du premier profil de l'arête d'identité edgeId
    private int firstProfileId(int edgeId) {
        int firstProfileIdStartOfRange = 0;
        int firstProfileIdRangeLength = 29;
        return Bits.extractUnsigned(profileIds.get(edgeId),
                firstProfileIdStartOfRange,
                firstProfileIdRangeLength);
    }

    // Retourne le nombre d'échantillons du profil de l'arête d'identité edgeId
    private int numberOfSamplesInProfile(int edgeId) {
        int lengthToQ28_4 = Short.toUnsignedInt(
                edgesBuffer.getShort(BYTES_FOR_EDGES * edgeId + OFFSET_LENGTH));
        int twoToQ28_4 = Q28_4.ofInt(2);
        return 1 + Math2.ceilDiv(lengthToQ28_4, twoToQ28_4);
    }

    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     *
     * @param edgeId l'identité de l'arête
     *
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(
                edgesBuffer.getShort(edgeId * BYTES_FOR_EDGES + OFFSET_ATTRIBUTES_INDEX));
    }
}