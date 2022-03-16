package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Short.toUnsignedInt;

/**
 * Représente le tableau contenant les 16384 secteurs de Javelo
 */
public record GraphSectors(ByteBuffer buffer) {
    private static final double SECTOR_WIDTH = 349_000/128.0;
    private static final double SECTOR_LENGTH = 221_000/128.0;
    private static final double SWISS_E_MIN = 2_485_000;
    private static final double SWISS_N_MIN = 1_075_000;
    private static final int OFFSET_SECTOR = Integer.BYTES + Short.BYTES;

    /**
     * Retourne la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et de côté
     * égal au double de la distance donnée (en mètres)
     * @param center
     * @param distance
     * @return la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et de côté
     * égal au double de la distance donnée
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        List<Sector> inArea = new ArrayList<>();

        double leftSide = center.e() - distance;
        double lowerSide = center.n() - distance;
        double distanceToWestLimit = leftSide - SWISS_E_MIN;
        double distanceToSouthLimit = lowerSide - SWISS_N_MIN;

        int xMin = Math2.clamp(0, (int) (distanceToWestLimit/ SECTOR_WIDTH), 128);
        int xMax = Math2.clamp(0, (int) ( (distanceToWestLimit + 2 * distance) / SECTOR_WIDTH), 128);
        int yMin = Math2.clamp(0, (int) (distanceToSouthLimit/ SECTOR_LENGTH), 128);
        int yMax = Math2.clamp(0, (int) ( (distanceToSouthLimit + 2 * distance) / SECTOR_LENGTH), 128);

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                int startNodeId = buffer.getInt(OFFSET_SECTOR * (x + y * 128));
                int endNodeId = startNodeId
                                + toUnsignedInt(buffer.getShort(OFFSET_SECTOR * (x + y * 128) + Integer.BYTES));

                inArea.add(new Sector(startNodeId, endNodeId));
            }
        }

        return inArea;
    }

    /**
     * Représente un secteur par l'identité du premier noeud du secteur et l'identité du noeud situé juste après le
     * dernier noeud du secteur
     */
    public record Sector(int startNodeId, int endNodeId) {}

}
