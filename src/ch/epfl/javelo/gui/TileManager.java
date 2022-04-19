package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.projection.WebMercator;
import javafx.scene.image.Image;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private Map<TileId,Image> cacheMemory =
            new LinkedHashMap<>(MAX_ENTRIES, .75f, true);

    protected boolean removeEldestEntry(Map m) {
        return m.size() > MAX_ENTRIES;
    }

    private record TileId(int zoomLevel, int xTileIndex, int yTileIndex) {

        /**
         * Retourne true si les paramètres passés en argument correspondent à une
         * tuile valide, et false sinon
         *
         * @param zoomLevel le niveau de zoom
         * @param xTileIndex l'index x de la tuile
         * @param yTileIndex l'index y de la tuile
         *
         * @return true si les paramètres passés en argument correspondent à une
         * tuile valide, et false sinon
         */
        public static boolean isValid(int zoomLevel, int xTileIndex, int yTileIndex) {
            //devrait-on utiliser les méthodes xAtZoomLevel() et yAtZoomLevel de
            // WebMercator (je ne pense pas puisque xTileIndex et yTileIndex
            // sont des entiers, donc utiliser Math.scalb semple superflu
            double xWebMercator = xTileIndex << zoomLevel;
            double yWebMercator = yTileIndex << zoomLevel;
            // Cela dit, les lignes suivantes sont "dupliquées" car elles figurent
            // aussi dans  WebMercator
            double lon = WebMercator.lon(xWebMercator);
            double lat = WebMercator.lat(yWebMercator);
            double e = Ch1903.e(lon, lat);
            double n = Ch1903.n(lon, lat);
            return SwissBounds.containsEN(e, n);
        }
    }


    public TileManager(Path path, String tileServer) throws IOException {

        this.path = Files.createDirectories(path);
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
        int zoomLevel = tileId.zoomLevel();
        int xTileIndex = tileId.xTileIndex();
        int yTIleIndex = tileId.yTileIndex();
        Preconditions.checkArgument(TileId.isValid(zoomLevel, xTileIndex, yTIleIndex));

        if (cacheMemory.containsKey(tileId)) {
            return cacheMemory.get(tileId);
        }

        if (Files.exists(imagePath(path, tileId))) {
            try (InputStream fis = new FileInputStream(imagePath(path, tileId).toString())) {
                Image image = new Image(fis);
                cacheMemory.put(tileId, image);
                return image;
            }
        }
        Files.createDirectories(imagePath(path, tileId));
        OutputStream fos = new FileOutputStream(imagePath(path, tileId).toString());
        URL u = new URL(imagePath(Path.of(tileServer), tileId) + ".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try (InputStream i = c.getInputStream()) {
            return new Image(i);
        }
    }
    private Path imagePath(Path basePath, TileId tileId) {
        return basePath
                .resolve(Path.of(String.valueOf(tileId.zoomLevel())))
                .resolve(Path.of(String.valueOf(tileId.xTileIndex())))
                .resolve(Path.of(String.valueOf(tileId.xTileIndex())));
    }

}
