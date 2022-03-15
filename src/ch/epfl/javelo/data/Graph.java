package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Représente le graphe JaVelo
 */
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
        String[] fileNames = {"attributes.bin", "edges.bin", "elevations.bin", "nodes.bin", "nodes_osmid.bin", "profile_ids.bin", "sectors.bin"};

        /*for(String fileName : fileNames) {
            Path currentPath = basePath.resolve(fileName);
            try (FileChannel channel = FileChannel.open(currentPath)) {
                osmIdBuffer = channel
                        .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                        .asLongBuffer();
            }
        }*/
        Path attributesPath = basePath.resolve("attributes.")
        return new Graph()
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

    /**
     * Retourne le nombre d'arêtes sortant du noeud d'identité donnée
     * @param nodeId
     * @return le nombre d'arêtes sortant du noeud d'identité donnée
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Retourne l'identité de la edgeIndex-ième arête sortant du noeud d'identité nodeId
     * @param nodeId
     * @param edgeIndex
     * @return l'identité de la edgeIndex-ième arête sortant du noeud d'identité nodeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Retourne l'identité du noeud se trouvant le plus proche du point donné, à la distance maximale donnée (en mètres),
     * ou -1 si aucun noeud ne correspond à ces critères
     * @param point
     * @param searchDistance
     * @return
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        List<GraphSectors.Sector> closeSectors = sectors.sectorsInArea(point, searchDistance);
        double minDistance = 0, distance = 0;
        int closestNodeId = -1;
        for (GraphSectors.Sector s : closeSectors) {
            for (int i = s.startNodeId(); i < s.endNodeId(); i++) {
                distance = point.squaredDistanceTo(new PointCh(nodes.nodeE(i), nodes.nodeN(i)));
                if (distance > minDistance) {
                    minDistance = distance;
                    closestNodeId = i;
                }
            } // DOIT RETOURNER -1 SI PAS DE NOEUDS
        }
        return closestNodeId;
    }

    /**
     * Retourne l'identité du noeud destination de l'arête d'identité donnée
     * @param edgeId
     * @return l'identité du noeud destination de l'arête d'identité donnée
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Retourne vrai ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient
     * @param edgeId
     * @return vrai ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     * @param edgeId
     * @return l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     */
    public AttributeSet edgeAttributes(int edgeId) {
        int attributeSetId = edges.attributesIndex(edgeId);
        return attributeSets.get(attributeSetId);
    }

    /**
     * Retourne la longueur, en mètres, de l'arête d'identité donnée
     * @param edgeId
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Retourne le dénivelé positif total de l'arête d'identité donnée
     * @param edgeId
     * @return le dénivelé positif total de l'arête d'identité donnée
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * Retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction; si l'arête ne possède pas
     * de profil, alors cette fonction doit retourner Double.NaN pour n'importe quel argument
     * @param edgeId
     * @return le profil en long de l'arête d'identité donnée, sous la forme d'une fonction; si l'arête ne possède pas
     * de profil, alors cette fonction doit retourner Double.NaN pour n'importe quel argument
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        float[] samples = edges.profileSamples(edgeId);
        return edges.hasProfile(edgeId)? Functions.sampled(samples, edgeLength(edgeId)) : Functions.constant(Double.NaN);
    }
}
