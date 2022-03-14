package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Graph {
    public final GraphNodes nodes;
    public final GraphSectors sectors;
    public final GraphEdges edges;
    public final List<AttributeSet> attributeSets;


    /**
     * Retourne le graphe avec les noeuds, secteurs, arêtes et ensemble d'attributs donnés
     * @param nodes
     * @param sectors
     * @param edges
     * @param attributeSets
     */
    public Graph (GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }

    /**
     * Retourne le graphe JaVelo obtenu à partir des fichiers se trouvant dans le répertoire dont le chemin d'accès est
     * basePath, ou lève IOException en cas d'erreur d'entrée/sortie, p. ex. si l'un des fichiers attendu n'existe pas.
     * @param basePath
     * @return retourne le graphe JaVelo obtenu à partir des fichiers se trouvant dans le répertoire dont le chemin
     * d'accès est basePath, ou lève IOException en cas d'erreur d'entrée/sortie, p. ex. si l'un des fichiers attendu
     * n'existe pas.
     * @throws IOException
     */
    Graph loadFrom(Path basePath) throws IOException {

    }

    /**
     * Retourne le nombre total de noeuds dans le graphe
     * @return le nombre total de noeuds dans le graphe
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Retourne la position du noeud d'identité donnée
     * @param nodeId
     * @return la position du noeud d'identité donnée
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

}
