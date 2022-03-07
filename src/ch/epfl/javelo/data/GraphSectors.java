package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
     * égal au double de la distance donnée
     * @param center
     * @param distance
     * @return la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et de côté
     * égal au double de la distance donnée
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        List<Sector> inArea = new ArrayList<Sector>();

        double leftSide = center.e() - distance;
        double lowerSide = center.n() - distance;
        double distanceToWestLimit = leftSide - SWISS_E_MIN;
        double distanceToSouthLimit = lowerSide - SWISS_N_MIN;

        int xMin = (int) (distanceToWestLimit/ SECTOR_WIDTH);
        int yMin = (int) ( (distanceToWestLimit + 2 * distance) / SECTOR_WIDTH);
        int xMax = (int) ( (distanceToSouthLimit + 2 * distance) / SECTOR_LENGTH);
        int yMax = (int) (distanceToSouthLimit/ SECTOR_LENGTH);

        // clamp à rajouter pour les x et y min/max

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                inArea.add(new Sector(buffer.getInt(OFFSET_SECTOR * (x + y * 128)),
                        buffer.getShort(OFFSET_SECTOR * (x + y * 128) + Integer.BYTES) ));
            }
        }
        return inArea;
    }

    public record Sector(int startNodeId, int endNodeId) {}
}
