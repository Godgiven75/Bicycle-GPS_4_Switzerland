package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Short.toUnsignedInt;

/**
 * Représente le tableau contenant les 16384 secteurs de Javelo.
 *
 * @param buffer le ByteBuffer contenant la valeur des attributs de la totalité
 * des secteurs
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public record GraphSectors(ByteBuffer buffer) {
    private static final double SWISS_WIDTH = 349_000;
    private static final double SWISS_HEIGHT = 221_000;
    private static final double SWISS_E_MIN = 2_485_000;
    private static final double SWISS_N_MIN = 1_075_000;
    private static final int SUBDIVISIONS_PER_SIDE = 128;
    private static final int SUBDIVISIONS_PER_SIDE_INDEX = SUBDIVISIONS_PER_SIDE - 1;
    private static final double SECTOR_WIDTH = SWISS_WIDTH / SUBDIVISIONS_PER_SIDE;
    private static final double SECTOR_HEIGHT = SWISS_HEIGHT / SUBDIVISIONS_PER_SIDE;
    private static final int OFFSET_SECTOR = Integer.BYTES + Short.BYTES;

    /**
     * Représente un secteur par l'identité du premier noeud du secteur et
     * l'identité du noeud situé juste après le dernier noeud du secteur.
     *
     * @param startNodeId l'identité du neoud de départ
     * @param endNodeId l'identité du noeud d'arrivée
     */
    public record Sector(int startNodeId, int endNodeId) {}

    /**
     * Retourne la liste de tous les secteurs ayant une intersection avec le carré
     * centré au point donné et de côté égal au double de la distance donnée.
     *
     * @param center le centre du carré
     * @param distance la moitié d'un côté du carré, en mètres
     *
     * @return la liste de tous les secteurs ayant une intersection avec le carré
     * centré au point donné et de côté égal au double de la distance donnée
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        List<Sector> inArea = new ArrayList<>();

        double leftSide = center.e() - distance;
        double lowerSide = center.n() - distance;
        double distanceToWestLimit = leftSide - SWISS_E_MIN;
        double distanceToSouthLimit = lowerSide - SWISS_N_MIN;
        final double EDGE_LENGTH = 2 * distance;

        int xMin = Math2.clamp(0,
                (int) (distanceToWestLimit / SECTOR_WIDTH),
                SUBDIVISIONS_PER_SIDE_INDEX);
        int xMax = Math2.clamp(0,
                (int) ( (distanceToWestLimit + EDGE_LENGTH) / SECTOR_WIDTH),
                SUBDIVISIONS_PER_SIDE_INDEX);
        int yMin = Math2.clamp(0,
                (int) (distanceToSouthLimit / SECTOR_HEIGHT),
                SUBDIVISIONS_PER_SIDE_INDEX);
        int yMax = Math2.clamp(0,
                (int) ( (distanceToSouthLimit + EDGE_LENGTH) / SECTOR_HEIGHT),
                SUBDIVISIONS_PER_SIDE_INDEX);

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                int startNodeIndex = OFFSET_SECTOR * (x + y * SUBDIVISIONS_PER_SIDE);
                int endNodeIndex = startNodeIndex + Integer.BYTES;
                int startNodeId = buffer.getInt(startNodeIndex);
                int endNodeId = startNodeId + toUnsignedInt(buffer.getShort(endNodeIndex));

                inArea.add(new Sector(startNodeId, endNodeId));
            }
        }

        return inArea;
    }
}
