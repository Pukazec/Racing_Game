package leo.skvorc.racinggame.utils;

import leo.skvorc.racinggame.model.CarCollision;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLUtils {
    public static void saveXml(List<CarCollision> collisionList) {
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document xmlDocument = documentBuilder.newDocument();

            Element rootElement = xmlDocument.createElement("Collisions");

            xmlDocument.appendChild(rootElement);

            for (CarCollision carCollision :
                    collisionList) {
                // Collision
                Element collision = xmlDocument.createElement("Collision");
                rootElement.appendChild(collision);

                // Player Name
                Element playerName = xmlDocument.createElement("Player");
                Node playerTextNode = xmlDocument.createTextNode(carCollision.getPlayer());
                playerName.appendChild(playerTextNode);
                collision.appendChild(playerName);

                // Position X
                Element positionX = xmlDocument.createElement("PositionX");
                Node positionXText = xmlDocument.createTextNode(String.valueOf(carCollision.getPositionX()));
                positionX.appendChild(positionXText);
                collision.appendChild(positionX);

                // Position Y
                Element positionY = xmlDocument.createElement("PositionY");
                Node positionYText = xmlDocument.createTextNode(String.valueOf(carCollision.getPositionY()));
                positionY.appendChild(positionYText);
                collision.appendChild(positionY);

                // Rotation
                Element rotation = xmlDocument.createElement("Rotation");
                Node rotationText = xmlDocument.createTextNode(String.valueOf(carCollision.getRotation()));
                rotation.appendChild(rotationText);
                collision.appendChild(rotation);

                // TimeStamp
                Element timeStamp = xmlDocument.createElement("TimeStamp");
                Node timeStampText = xmlDocument.createTextNode(String.valueOf(carCollision.getTimeStamp()));
                timeStamp.appendChild(timeStampText);
                collision.appendChild(timeStamp);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            Source xmlSource = new DOMSource(xmlDocument);
            Result xmlResult = new StreamResult(new File("collisions.xml"));

            transformer.transform(xmlSource, xmlResult);
        } catch (TransformerException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<CarCollision> readXML() {
        List<CarCollision> collisionList = new ArrayList<>();
        try {
            InputStream collisionsStream = new FileInputStream("collisions.xml");

            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document xmlDocument = parser.parse(collisionsStream);

            String parentNodeName = xmlDocument.getDocumentElement().getNodeName();

            NodeList nodeList = xmlDocument.getElementsByTagName("Collision");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node collisionNode = nodeList.item(i);

                if (collisionNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element collisionElement = (Element) collisionNode;

                    CarCollision carCollision = new CarCollision();

                    // Player name
                    String player = collisionElement
                            .getElementsByTagName("Player")
                            .item(0)
                            .getTextContent();
                    System.out.println("Player " + player);
                    carCollision.setPlayer(player);

                    // PositionX
                    String positionX = collisionElement
                            .getElementsByTagName("PositionX")
                            .item(0)
                            .getTextContent();
                    System.out.println("Positiion X " + positionX);
                    carCollision.setPositionX(Double.parseDouble(positionX));

                    // PositionY
                    String positionY = collisionElement
                            .getElementsByTagName("PositionY")
                            .item(0)
                            .getTextContent();
                    System.out.println("Positiion Y " + positionY);
                    carCollision.setPositionY(Double.parseDouble(positionY));

                    // Rotation
                    String rotation = collisionElement
                            .getElementsByTagName("Rotation")
                            .item(0)
                            .getTextContent();
                    System.out.println("Rotation " + rotation);
                    carCollision.setRotation(Double.parseDouble(rotation));

                    // TimeStamp
                    String timeStamp = collisionElement
                            .getElementsByTagName("TimeStamp")
                            .item(0)
                            .getTextContent();
                    System.out.println("TimeStamp " + timeStamp);
                    carCollision.setTimeStamp(Duration.parse(timeStamp));

                    collisionList.add(carCollision);
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        return collisionList;
    }
}
