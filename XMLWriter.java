import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class XMLWriter
{
    //region Attributes

    /**
     * This class will be used when an object that contains an object is turn into a node.
     * If the inner object doesn't implement this interface then it will not be converted into a node
     * (it will only be converted into string).
     */
    public interface XMLObject { }

    /**
     * The fields that present this annotation won't appear when an object is converted into a node.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ignore { }

    Document document;
    String filename;

    //endregion

    //region Constructos
    public XMLWriter(String filename, Document document)
    {
        this.filename = filename;
        this.document = document;
    }
    //endregion

    //region Methods
    
    public <T> Element objectToNode(String nodename, T object, Set<String> attributesToIgnore)
    {
        Element node = document.createElement(nodename);

        if (attributesToIgnore == null) loopThroughFields(object, (field, value) ->
        {
            addFieldsAsNodes(node, field, value);
        });
        else loopThroughFields(object, (field, value) ->
        {
            if (!attributesToIgnore.contains(field.getName()))
            {
                addFieldsAsNodes(node, field, value);
            }
        });
        return node;
    }

    private void addFieldsAsNodes(Element node, Field field, Object value)
    {
        if (field.isAnnotationPresent(Ignore.class)) return;

        if (XMLObject.class.isAssignableFrom(field.getType()))
        {
            node.appendChild(objectToNode(field.getName(), value));
        }
        else
        {
            Element e = document.createElement(field.getName());
            e.appendChild(document.createTextNode(value.toString()));
            node.appendChild(e);
        }
    }

    public <T> Element objectToNode(String nodoNombre, T objeto)
    {
        return objectToNode(nodoNombre, objeto, null);
    }

    public <T> Element objectsToNodes(Element parentNode, String childrenNodeName, List<T> objectList, Set<String> attributesToIgnore)
    {
        Element node = (Element) parentNode.cloneNode(true);

        objectList.forEach(objeto ->

                node.appendChild(objectToNode(
                        childrenNodeName,
                        objeto,
                        attributesToIgnore
                ))
        );
        return node;
    }
    public <T> Element objectsToNodes(Element parentNode, String childrenNodeName, List<T> objectList)
    {
        return objectsToNodes(parentNode, childrenNodeName, objectList, null);
    }

    public void save()
    {
        try
        {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
        }
    }

    private static <T> void loopThroughFields(T object, BiConsumer<Field, Object> field_value)
    {
        // https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
        for (Field field : object.getClass().getDeclaredFields())
        {
            if (field == null || java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);

            Object value = null;
            try
            {
                value = field.get(object);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            if (value == null) continue;

            field_value.accept(field, value);
        }
    }

    public boolean isDocumentValid(String xsdFile)
    {
        try
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(filename));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(filename)));
        }
        catch (IOException | SAXException e)
        {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    //endregion
}
