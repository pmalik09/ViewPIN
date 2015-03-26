package com.safenetinc.viewpin.restore;

import java.security.SecureRandom;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;



public class XPathReader {

	
	private String xmlFile 		  = null;
	
	private static Logger logger  = Logger.getLogger(Restore.class);	
    
    private Document xmlDocument;
    
    private XPath xPath 		  = null;
   
    
    public XPathReader(String xmlFile)
    {
        try
        {
        this.xmlFile = xmlFile;
        initObjects();
        }
        catch(Exception e)
        {
        	getLogger().fatal("could not read file " + e.getMessage());
        }
    }
        
        
    private void initObjects()
    {        
        try {
            
			xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);            
            xPath =  XPathFactory.newInstance().newXPath();

		} catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }       
    }
        
    public Object read(String expression,QName returnType)
    {
        try {
			
            XPathExpression xPathExpression = 
			xPath.compile(expression);
           
			return xPathExpression.evaluate(xmlDocument, returnType);

        } catch (XPathExpressionException ex) {
            logger.error(ex.getMessage());
            return null;
        }

    }
    private static Logger getLogger ()
	 {
	       return logger;
	  }
	 
}
