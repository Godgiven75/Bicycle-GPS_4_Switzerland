package ch.epfl.javelo.routing;

/**
 * Représente une fonction de coût
 */
public interface CostFunction {

    /**
     * Retourne le facteur par lequel la longueur de l'arête d'identité edgeId, partant du noeud d'identité nodeId, doit
     * être multipliée; ce facteur doit impérativement être supérieur ou égal à 1
     * @param nodeId l'identité du noeud
     * @param edgeId l'identité de l'arête
     * @return le facteur par lequel la longueur de l'arête d'identité edgeId, partant du noeud d'identité nodeId, doit
     * être multipliée; ce facteur doit impérativement être supérieur ou égal à 1
     */
    double costFactor(int nodeId, int edgeId);

}
