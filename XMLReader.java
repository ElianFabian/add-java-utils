import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class XMLReader
{
    public final String filename;
    public final Document document;

    public static class Node
    {
        public Element content;

        //region Methods
        public String getString(String childName)
        {
            return content.getElementsByTagName(childName).item(0).getTextContent();
        }

        public int getInt(String childName)
        {
            return Integer.parseInt(getString(childName));
        }

        public float getFloat(String childName)
        {
            return Float.parseFloat(getString(childName));
        }

        public String getAttributeContent(String name)
        {
            return content.getAttribute(name);
        }
        //endregion
    }

    //region Constructors

    public XMLReader(String filename)
    {
        Document document = null;

        try
        {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = docBuilder.parse(filename);
        }
        catch (ParserConfigurationException | IOException | SAXException e)
        {
            e.printStackTrace();
        }

        this.document = document;
        this.filename = document.getDocumentURI();
    }

    //endregion

    //region Methods

    public void readNodesByName(String nodename, Consumer<Node> node)
    {
        Node currentNode = new Node();
        NodeList nodeList = document.getElementsByTagName(nodename);

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            currentNode.content = (Element) nodeList.item(i);

            node.accept(currentNode);
        }
    }

    public List<HashMap<String, String>> readNodesByName(String nodename)
    {
        List<HashMap<String, String>> objects = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName(nodename);

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            NodeList children = (NodeList) nodeList.item(i);

            HashMap<String, String> object = new HashMap<>();

            for (int j = 0; j < children.getLength(); j++)
            {
                org.w3c.dom.Node child = children.item(j);

                // Excludes the text nodes
                if (Text.class.isAssignableFrom(child.getClass())) continue;

                object.put(child.getNodeName(), child.getTextContent());
            }

            objects.add(object);
        }

        return objects;
    }

    //endregion
}
