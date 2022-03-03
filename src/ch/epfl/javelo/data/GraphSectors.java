package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Représente le tableau contenant les 16384 secteurs de Javelo
 */
public record GraphSectors(ByteBuffer buffer) {

    /**
     * Retourne la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et de côté
     * égal au double de la distance donnée
     * @param center
     * @param distance
     * @return la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et de côté
     * égal au double de la distance donnée
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        return null;
    }

    public record Sector(int startNodeId, int endNodeId) {

    }
}
