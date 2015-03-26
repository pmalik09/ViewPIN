// $Id: PinAgent/src/com/safenetinc/viewpin/common/utils/XMLUtils.java 1.1 2008/09/04 10:47:15IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.safenetinc.viewpin.common.xml.FileSystemResourceResolver;
import com.safenetinc.viewpin.common.xml.ValidationResult;

/**
 * Class to handle XML utility operations
 * 
 * @author Stuart Horler
 *
 *
 */
public class XMLUtils
{

    private XMLUtils()
    {
        //Private constructor - nothing to do here
    }

    /**
     * Method to parse a byte array into an XML document
     * 
     * @param document The byte array to parse
     * @param documentBuilder The document builder to use
     * @return A document representing the contents of the byte array
     * @throws SAXException Thrown if an error occurs during parsing the XML
     * @throws IOException Thrown if an error occurs reading the byte array
     */
    public static Document parseDocument (byte[] document, DocumentBuilder documentBuilder) throws SAXException, IOException
    {
        Document d;
        ByteArrayInputStream bais;
    
        d = null;
        bais = null;
    
        try
        {
            bais = new ByteArrayInputStream(document);
    
            d = documentBuilder.parse(new InputSource(bais));
        }
        finally
        {
            if (bais != null)
            {
                bais.close();
            }
        }
    
        return d;
    }

    /**
     * Method to handle compression of a document
     * 
     * @param document The document to compress
     * @return byte array containing the compressed document
     * @throws TransformerConfigurationException Thrown if an error occurs with the configuration of the
     *         transformation
     * @throws TransformerException Thrown if an error occurs with the transformation
     * @throws IOException Thrown if a general error occurs during the serialisation
     */
    public static byte[] compressDocument (org.w3c.dom.Document document) throws TransformerConfigurationException, TransformerException, IOException
    {
        TransformerFactory tf;
        Transformer t;
        DOMSource ds;
        Deflater deflater;
        DeflaterOutputStream dos;
        ByteArrayOutputStream compressedDocument;
        StreamResult sr;
        byte[] result;
    
        tf = null;
        t = null;
        ds = null;
        deflater = null;
        dos = null;
        compressedDocument = null;
        sr = null;
        result = null;
    
        tf = TransformerFactory.newInstance();
        t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    
        deflater = new Deflater(Deflater.BEST_COMPRESSION);
    
        ds = new DOMSource(document);
    
        try
        {
            compressedDocument = new ByteArrayOutputStream();
    
            dos = new DeflaterOutputStream(compressedDocument, deflater);
    
            sr = new StreamResult(dos);
    
            t.transform(ds, sr);
    
            dos.close();
            compressedDocument.close();
            
            result = compressedDocument.toByteArray();
        }
        finally
        {
            if (dos != null)
            {
                dos.close();
            }
    
            if (compressedDocument != null)
            {
                compressedDocument.close();
            }
        }
    
        return result;
    }

    /**
     * Method to return the first child element of a supplied element. The child element's name must match
     * that supplied in the localName field.
     * 
     * @param parentElement The parent element to search for child elements
     * @param namespaceUri The namespace URI for the element
     * @param localName The name of the child element to match
     * @return The first child element matching the supplied name
     */
    public static Element getFirstChildElementMatchingName (Element parentElement, String namespaceUri, String localName)
    {
        ArrayList<Element> childElements;
        Element firstChildElement;
    
        childElements = null;
        firstChildElement = null;
    
        childElements = XMLUtils.getChildElementsMatchingName(parentElement, namespaceUri, localName);
    
        if (childElements.size() >= 1)
        {
            firstChildElement = childElements.get(0);
        }
    
        return firstChildElement;
    }

    /**
     * Method to validate a document against the associated schema
     * 
     * @param document The document to validate
     * @param schema The schema to validate against
     * @param validator The validator to use
     * @return a {@link ValidationResult} result representing the result of the validation
     * @throws SAXException Thrown if an error occurred during parsing
     * @throws IOException Thrown if an error occurred reading the schema
     */
    public static ValidationResult validateAgainstSchema (final Document document, final Schema schema, final ThreadLocal<Validator> validator) throws SAXException, IOException
    {
        ValidationResult validationResult;
    
        validationResult = null;
    
        if (validator.get() == null)
        {
            validator.set(schema.newValidator());
        }
    
        validator.get().reset();
    
        validationResult = new ValidationResult();
    
        validator.get().setErrorHandler(validationResult);
    
        validator.get().validate(new DOMSource(document));
    
        return validationResult;
    }

    /**
     * Method to return all child elements matching a given name
     * 
     * @param parentElement The parent element to search for child elements
     * @param namespaceUri The namespace URI for the element
     * @param localName The name that child elements must match
     * @return An ArrayList containing the matching child elements
     */
    public static ArrayList<Element> getChildElementsMatchingName (final Element parentElement, final String namespaceUri, final String localName)
    {
        ArrayList<Element> childElements;
        NodeList childNodes;
        Element nextChildElement;
    
        childElements = null;
        childNodes = null;
        nextChildElement = null;
    
        childElements = new ArrayList<Element>();
    
        childNodes = parentElement.getChildNodes();
    
        for (int i = 0; i < childNodes.getLength(); i++)
        {
            if (childNodes.item(i).getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }
    
            nextChildElement = (Element) childNodes.item(i);
    
            if (nextChildElement.getNamespaceURI().compareTo(namespaceUri) != 0)
            {
                continue;
            }
    
            if (nextChildElement.getLocalName().compareTo(localName) != 0)
            {
                continue;
            }
    
            childElements.add(nextChildElement);
        }
    
        return childElements;
    }

    /**
     * Method to load a {@link Schema} object from a given filename
     * 
     * @param schemaFilename The filename for the schema
     * @return The schema object
     * @throws SAXException Thrown if an error occurs parsing the schema
     * @throws IOException Thrown if an error occurs reading the schema
     */
    public static Schema loadSchema (final String schemaFilename) throws SAXException, IOException
    {
        Schema schema;
        SchemaFactory schemaFactory;
        FileSystemResourceResolver fsrr;
        ValidationResult schemaValidator;
        File schemaFile;
    
        schema = null;
        schemaFactory = null;
        fsrr = null;
        schemaValidator = null;
        schemaFile = null;
    
        schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    
        fsrr = new FileSystemResourceResolver();
        fsrr.add("http://www.w3.org/TR/xmlenc-core/xenc-schema.xsd", "schemas/www/w3/org/TR/xmlenc-core/xenc-schema.xsd");
        fsrr.add("http://www.w3.org/TR/xmldsig-core/xmldsig-core-schema.xsd", "schemas/www/w3/org/TR/xmldsig-core/xmldsig-core-schema.xsd");
        fsrr.add("http://www.w3.org/2001/XMLSchema.dtd", "schemas/www/w3/org/2001/XMLSchema.dtd");
        fsrr.add("datatypes.dtd", "schemas/datatypes.dtd");
        fsrr.add("http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd", "schemas/www/w3/org/TR/xmldsig-core/xmldsig-core-schema.xsd");
        schemaFactory.setResourceResolver(fsrr);
    
        schemaValidator = new ValidationResult();
        schemaFactory.setErrorHandler(schemaValidator);
    
        // Get schema file resource
        schemaFile = Utils.getFileResource(schemaFilename, false, false);
    
        // Validate schema
        schema = schemaFactory.newSchema(schemaFile);
    
        // Is schema valid?
        if (schemaValidator.isValid() == false)
        {
            // Schema is not valid
            throw schemaValidator.getException();
        }
    
        return schema;
    }

    /**
     * Serialises a Node into a byte array
     * 
     * @param node The node to serialise
     * @return byte array containing the Node to serialise
     * @throws TransformerConfigurationException Thrown if an error occurs with the configuration of the
     *         transformation
     * @throws TransformerException Thrown if an error occurs with the transformation
     * @throws IOException Thrown if a general error occurs during the serialisation
     */
    public static byte[] serialise (Node node) throws TransformerConfigurationException, TransformerException, IOException
    {
        ByteArrayOutputStream serialisedNode;
        TransformerFactory tf;
        Transformer t;
        DOMSource ds;
        StreamResult sr;
        byte[] result;
    
        serialisedNode = null;
    
        tf = null;
        t = null;
        ds = null;
        sr = null;
        result = null;
    
        tf = TransformerFactory.newInstance();
    
        t = tf.newTransformer();
    
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    
        ds = new DOMSource(node);
    
        try
        {
            serialisedNode = new ByteArrayOutputStream();
    
            sr = new StreamResult(serialisedNode);
    
            t.transform(ds, sr);
            
            serialisedNode.close();
    
            result = serialisedNode.toByteArray();
        }
        finally
        {
            if (serialisedNode != null)
            {
                serialisedNode.close();
            }
        }
    
        return result;
    }
}
