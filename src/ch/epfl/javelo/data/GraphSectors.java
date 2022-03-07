package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente le tableau contenant les 16384 secteurs de Javelo
 */
public record GraphSectors(ByteBuffer buffer) {
    private static final double sectorWidth = 349_000/128.0;
    private static final double sectorLength = 221_000/128.0;
    private static final double swissEmin = 2_485_000;
    private static final double swissNmin = 1_075_000;

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
        double distanceToWestLimit = leftSide - swissEmin;
        double distanceToSouthLimit = lowerSide - swissNmin;

        int xMin = (int) (distanceToWestLimit/sectorWidth);
        int yMin = (int) ((distanceToWestLimit + 2 * distance) / sectorWidth);
        int xMax = (int) ((distanceToSouthLimit + 2 * distance) / sectorLength);
        int yMax = (int) (distanceToSouthLimit/sectorLength);

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                inArea.add(new Sector(buffer.getInt(6 * (x + y * 128)),
                        buffer.getShort(6 * (x + y * 128) + 4) ));
            }
        }
        return inArea;
    }

    public record Sector(int startNodeId, int endNodeId) {}
}
