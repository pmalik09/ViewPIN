package com.safenetinc.viewpin.authority.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.safenetinc.viewpin.authority.xml.resources.NullEntityResolver;

/**
 * Class to handle creation of a <code>DocumentBuilder</code> with safe parsing features enabled.
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class SafeParser
{
    private static final String ELEMENT_ATTRIBUTE_LIMIT = "20";

    private static final String ENTITY_EXPANSION_LIMIT  = "1024";

    static
    {
        // Limit maximum number of attributes allowing in an element
        System.setProperty("elementAttributeLimit", ELEMENT_ATTRIBUTE_LIMIT);

        // Disable entity expansion
        System.setProperty("entityExpansionLimit", ENTITY_EXPANSION_LIMIT);
    }

    private SafeParser()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceAware Should the parser be namespace aware?
     * @param validating Should the parser validate?
     * @return A {@link DocumentBuilder}
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder getInstance (boolean namespaceAware, boolean validating) throws ParserConfigurationException
    {
        DocumentBuilder db;
        DocumentBuilderFactory dbf;

        db = null;
        dbf = null;

        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(namespaceAware);
        dbf.setValidating(validating);

        // Reduce risk of denial of service attacks
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // Prevent any entity references that may have been injected from being expanded
        dbf.setExpandEntityReferences(false);

        // Instantiate document builder
        db = dbf.newDocumentBuilder();

        // Prevent any injected external entity references from being resolved
        db.setEntityResolver(new NullEntityResolver());

        return db;
    }
}