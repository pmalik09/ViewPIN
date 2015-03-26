// $Id: PinAgent/src/com/safenetinc/viewpin/common/xml/CachedDocumentBuilder.java 1.3 2012/01/24 14:59:37IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.common.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import java.security.CodeSource;
import java.text.MessageFormat;



/**
 * Class to manage a {@link DocumentBuilder} instance, allowing for caching
 * @author Stuart Horler
 */
public class CachedDocumentBuilder
{
    private static final String ELEMENT_ATTRIBUTE_LIMIT = "20";
    private static final String ENTITY_EXPANSION_LIMIT = "1024";
   private static final Logger logger = Logger.getLogger(CachedDocumentBuilder.class);
    private static ThreadLocal<DocumentBuilder> documentBuilder = null;
	// private static ThreadLocal<DocumentBuilder> documentBuilder = new ThreadLocal<DocumentBuilder>();
    //DocumentBuilderFactoryImpl dbfImpl  = new DocumentBuilderFactoryImpl(); 
    static
    {
        setDocumentBuilder(new ThreadLocal<DocumentBuilder>()); 
        
        // Limit maximum number of attributes allowing in an element
        System.setProperty("elementAttributeLimit", ELEMENT_ATTRIBUTE_LIMIT);
        
        // Disable entity expansion
        System.setProperty("entityExpansionLimit", ENTITY_EXPANSION_LIMIT);
        
        getLogger().debug("element attribute limit = " + ELEMENT_ATTRIBUTE_LIMIT);
        
       getLogger().debug("entity expansion limit = " + ENTITY_EXPANSION_LIMIT);
    }
    
    private CachedDocumentBuilder()
    {
        super();
    }
    
    /**
     * Method to return the cached {@link DocumentBuilder}
     * @param errorHandler The {@link ErrorHandler} to use
     * @return The cached {@link DocumentBuilder}
     * @throws ParserConfigurationException Thrown if an error occurs whilst configuring the {@link DocumentBuilder}
     */
    public static DocumentBuilder getCachedDocumentBuilder(ErrorHandler errorHandler) throws ParserConfigurationException
    {
        if(getDocumentBuilder().get() == null)
        {
            DocumentBuilder db;
            DocumentBuilderFactory dbf;
            
            try
            {
            	//getLogger().debug("inside try catch block...");
            	//getLogger().debug("documentbuildfactory is being loaded from "+ getJaxpImplementationInfo("DocumentBuilderFactory", DocumentBuilderFactory.newInstance().getClass())); 
            	dbf = DocumentBuilderFactory.newInstance();
            	dbf.setNamespaceAware(true);
            	dbf.setValidating(false);
            	dbf.setExpandEntityReferences(false);
            	dbf.setIgnoringComments(false);
            	dbf.setXIncludeAware(false);
            	dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            
	            db = dbf.newDocumentBuilder();
	            
	            getDocumentBuilder().set(db);
            }
            catch(Exception e)
            {
            	getLogger().error("error while creating document.");
            }
        }
        
        getDocumentBuilder().get().reset();
        
        getDocumentBuilder().get().setEntityResolver(new NullEntityResolver());
        
        if(errorHandler != null)
        {
            getDocumentBuilder().get().setErrorHandler(errorHandler);
        }
        
        return getDocumentBuilder().get();
    }

    private static ThreadLocal<DocumentBuilder> getDocumentBuilder()
    {
        return CachedDocumentBuilder.documentBuilder;
    }

    private static void setDocumentBuilder(ThreadLocal<DocumentBuilder> documentBuilder)
    {
        CachedDocumentBuilder.documentBuilder = documentBuilder;
    }
    
   private static final Logger getLogger()
    {
       return CachedDocumentBuilder.logger;
    }
   private static String getJaxpImplementationInfo(String componentName, Class componentClass)
   {

   CodeSource source = componentClass.getProtectionDomain().getCodeSource();  
	return MessageFormat.format( "{0} implementation: {1} loaded from: {2}",componentName,componentClass.getName(),
	source == null ? "Java Runtime" : source.getLocation());
} 
}