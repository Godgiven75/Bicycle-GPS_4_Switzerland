package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Locale;
import java.util.StringJoiner;

import static java.nio.file.Files.newBufferedWriter;

/**
 * Classe publique non instanciable représentant un générateur d'itinéraire au
 * format GPX.
 */
public class GpxGenerator {
    private GpxGenerator() {}

    /**
     * Retourne le document GPX correspondant à l'itinéraire et son profil.
     *
     * @param route l'itinéraire
     * @param profile le profil de cet itinéraire
     *
     * @return le document GPX correspondant à l'itinéraire et son profil
     */
    public static Document createGpx(Route route, ElevationProfile profile) {
        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        // Il faudrait plutôt un itérateur pour l'élégance

        double position = 0.0;
        for (int i = 0; i < route.edges().size(); i++) {

            Edge e = route.edges().get(i);
            PointCh p = e.fromPoint();
            double lon = Math.toDegrees(p.lon());
            double lat = Math.toDegrees(p.lat());

            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat", String.valueOf(lat));
            rtept.setAttribute("lon", String.valueOf(lon));

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.format(Locale.ROOT,"%.2f",profile.elevationAt(position)));

            position += e.length();
        }
        /*Edge lastEdge = route.edges().get(route.edges().size() - 1);
        Element rtept = doc.createElement("rtept");
        rte.appendChild(rtept);
        rtept.setAttribute("lat", String.valueOf(lastEdge.toPoint().lat()));
        rtept.setAttribute("lon", String.valueOf(lastEdge.toPoint().lon()));
        Element ele = doc.createElement("ele");
        rtept.appendChild(ele);
        ele.setTextContent(String.valueOf(profile.elevationAt(position + lastEdge.length())));*/

        return doc;
    }
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Ne devrait jamais se produire
        }
    }

    /**
     * Ecrit le document GPX correspondant à l'itinéraire et au profil de cet
     * itinéraire dans le fichier.
     *
     * @param path le nom du fichier
     * @param route l'itinéraire
     * @param profile le profil de l'itinéraire
     *
     * @throws IOException en cas d'erreur entrée/sortie
     */
    public static void writeGpx(String path, Route route, ElevationProfile profile)
            throws IOException {
        Document doc = createGpx(route, profile);
        Writer w = newBufferedWriter(Path.of(path));

        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));

        } catch(TransformerException e) {
            throw new Error(e); // Ne devrait jamais se produire
        }
    }
}
