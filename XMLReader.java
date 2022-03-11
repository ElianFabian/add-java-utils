import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.function.Consumer;

public class XMLReader
{
    String filename;
    Document document;

    public static class Node
    {
        public Element element;

        //region Constructors
        public Node(Element node)
        {
            this.element = node;
        }

        public Node() {}
        //endregion

        //region Methods
        public String getString(String childName)
        {
            return element.getElementsByTagName(childName).item(0).getTextContent();
        }

        public int getInt(String childName)
        {
            return Integer.parseInt(getString(childName));
        }

        public float getFloat(String childName)
        {
            return Float.parseFloat(getString(childName));
        }

        public String getAttribute(String name)
        {
            return element.getAttribute(name);
        }
        //endregion
    }

    public XMLReader(Document document)
    {
        this.document = document;
        this.filename = document.getDocumentURI();
    }
    
    public void readNodesByName(String nodename, Consumer<Node> nodeConsumer)
    {
        Node node = new Node();
        NodeList nodeList = document.getElementsByTagName(nodename);
        
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            node.element = (Element) nodeList.item(i);

            nodeConsumer.accept(node);
        }
    }
}
