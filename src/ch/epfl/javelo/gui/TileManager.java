package ch.epfl.javelo.gui;

import java.awt.*;
import java.nio.file.Path;

/**
 * Représente un gestionnaire de tuiles OSM.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class TileManager {
    private final Path path;
    private final String tileServer;

    private record TileId(int zoomLevel, int xTileIndex, int yTileIndex) {

        /**
         * Retourne l'image associée à l'identité d'une tuile
         *
         * @param zoomLevel le niveau de zoom
         * @param xTileIndex l'index x de la tuile
         * @param yTileIndex l'index y de la tuile
         *
         * @return l'image associée à l'identité de tuile donnée
         */
        public static boolean isValid(int zoomLevel, int xTileIndex, int yTileIndex) {

        }
    }


    public TileManager(Path path, String tileServer) {
        this.path = path;
        this.tileServer = tileServer;
    }

    public Image imageForTileAt(TileId tileId) {

    }

}
