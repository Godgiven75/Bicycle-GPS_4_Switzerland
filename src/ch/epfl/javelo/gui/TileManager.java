package ch.epfl.javelo.gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Représente un gestionnaire de tuiles OSM.
 *
 * @author Tanguy Dieudonné (326618)
 * @author Nathanaël Girod (329987)
 */
public final class TileManager {
    private final Path path;
    private final String tileServer;
    private static final int MAX_ENTRIES = 100;
    private Map<TileId,Image> inMemoryCache =
            new LinkedHashMap<>(MAX_ENTRIES, .1f, true);

    protected boolean removeEldestEntry(Map m) {
        return m.size() > MAX_ENTRIES;
    }

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
            return false;
        }
    }


    public TileManager(Path path, String tileServer) {
        this.path = File.createDirectories();
        this.tileServer = tileServer;
    }

    /**
     * Retourne une image à partir de l'identité de la tuile.
     *
     * @param tileId l'identité de la tuile
     * @return son image
     * @throws IOException si l'URL ne correspond pas à une tuile connue
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        StringJoiner tileSpecificDir = new StringJoiner("/", "", ".png");
        tileSpecificDir
                .add(String.valueOf(tileId.zoomLevel()))
                .add(String.valueOf(tileId.xTileIndex()))
                .add(String.valueOf(tileId.yTileIndex()));
        StringBuilder tileDir = new StringBuilder("https://tile.openstreetmap.org/");
        tileDir.append(tileSpecificDir);
        // Cache mémoire
        if (inMemoryCache.containsKey(tileId)) {
            return inMemoryCache.get(tileId);
        }
        // Cache disque
        else if ()
        //serveur openstreetmap
        URL u = new URL(tileDir);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try (InputStream i = c.getInputStream()
    }

}
