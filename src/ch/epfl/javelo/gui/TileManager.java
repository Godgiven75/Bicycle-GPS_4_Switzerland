package ch.epfl.javelo.gui;


import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.projection.WebMercator;

import javafx.scene.image.Image;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
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
    //On gardera au maximum 100 tuiles en mémoire
    private static final int MAX_ENTRIES = 100;
    private final Map<TileId, Image> cacheMemory =
            new LinkedHashMap<>(MAX_ENTRIES, .75f, true);

    private record TileId(int zoomLevel, int xTileIndex, int yTileIndex) {

        /**
         * Retourne vrai si les paramètres passés en argument correspondent à une
         * tuile valide, et faux sinon
         *
         * @param zoomLevel le niveau de zoom
         * @param xTileIndex l'index x de la tuile
         * @param yTileIndex l'index y de la tuile
         *
         * @return vrai si les paramètres passés en argument correspondent à une
         * tuile valide, et faux sinon
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

    /**
     * Crée un nouveau gestionnaire de tuiles, avec un cache disque
     * au chemin spécifié, et qui charge les tuiles depuis le serveur de tuile
     * passé en argument
     * @param path chemin du cache disque
     * @param tileServer nom serveur de tuile
     * @throws IOException si le chemin pour le cache-disque est invalide
     */
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

        if (cacheMemory.containsKey(tileId)) {
            return cacheMemory.get(tileId);
        }
        //Permet de supprimer l'élément auquel on a accédé le moins récemment
        //de la mémoire cache si la mémoire cache est pleine, afin de pouvoir
        //ajouter une nouvelle image
        if (cacheMemory.size() >= MAX_ENTRIES) {
            Iterator<TileId> it = cacheMemory.keySet().iterator();
            cacheMemory.remove(it.next());
        }

        if (Files.exists(imagePath(path, tileId))) {
            try (InputStream fis = new FileInputStream(imagePath(path, tileId).toString())) {
                Image image = new Image(fis);
                cacheMemory.put(tileId, image);
                return image;
            }
        }
        Files.createDirectories(imagePath(path, tileId));
        URL u = new URL(imagePath(Path.of(tileServer), tileId) + ".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try (InputStream i = c.getInputStream();
             OutputStream o = new FileOutputStream(imagePath(path, tileId).toString())) {
            Image image = new Image(i);
            cacheMemory.put(tileId, image);
            i.transferTo(o);
            return image;
        }
    }
    private Path imagePath(Path basePath, TileId tileId) {
        return basePath
                .resolve(Path.of(String.valueOf(tileId.zoomLevel())))
                .resolve(Path.of(String.valueOf(tileId.xTileIndex())))
                .resolve(Path.of(String.valueOf(tileId.yTileIndex())));
    }
}