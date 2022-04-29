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

    /**
     * Enregistrement imbriqué représentant l'identité d'une tuile OSM.
     *
     * @param zoomLevel le niveau de zoom de la tuile
     * @param xTileIndex l'index X de la tuile
     * @param yTileIndex l'index Y de la tuile
     */
    public record TileId(int zoomLevel, int xTileIndex, int yTileIndex) {

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
            int maxIndex = 1 << (zoomLevel + 8);
            return (0 <= xTileIndex && xTileIndex < maxIndex)
                    && (0 <= yTileIndex && yTileIndex < maxIndex);
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
        //preconditions ?
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
        Path imagePath = path
                .resolve(Path.of(String.valueOf(tileId.zoomLevel())))
                .resolve(Path.of(String.valueOf(tileId.xTileIndex())))
                .resolve(Path.of(tileId.yTileIndex() + ".png"));

        if (Files.exists(imagePath)) {
            try (InputStream fis = new FileInputStream(imagePath.toString())) {
                Image image = new Image(fis);
                cacheMemory.put(tileId, image);
                return image;
            }
        }
        Files.createDirectories(path
                .resolve(Path.of(String.valueOf(tileId.zoomLevel())))
                .resolve(Path.of(String.valueOf(tileId.xTileIndex()))));

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://")
                .append(tileServer).append("/")
                .append(tileId.zoomLevel()).append("/")
                .append(tileId.xTileIndex()).append("/")
                .append(tileId.yTileIndex())
                .append(".png");

        URL u = new URL(urlBuilder.toString());

        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");

        try (InputStream i = c.getInputStream();
             OutputStream o = new FileOutputStream(imagePath.toString())) {
            i.transferTo(o);
        }
        FileInputStream f = new FileInputStream(imagePath.toString());
        Image image = new Image(f);
        cacheMemory.put(tileId, image);
        return image;
    }
}