// $Id: PinAgent/src/com/safenetinc/ds/KeyInfo.java 1.1 2008/09/04 10:45:11IST Mkhurana Exp  $
package com.safenetinc.ds;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class represent the KeyInfo element of an XML Digital Signature document
 * @author Stuart Horler
 *
 *
 */
public class KeyInfo
{
    private ArrayList<KeyValue> keyValues = null;
    private ArrayList<X509Data> x509Data = null;
    
    /**
     * Default constructor
     */
    public KeyInfo()
    {
        super();
        
        setKeyValues(new ArrayList<KeyValue>());
        setX509Data(new ArrayList<X509Data>());
    }
    
    /**
     * Adds a key value for this class to represnt
     * @param keyValue The key value
     */
    public void addKeyValue(KeyValue keyValue)
    {
        getKeyValues().add(keyValue);
    }
    
    /**
     * Method to add X509 data to the KeyInfo element
     * @param x509DataToAdd The X509 data to add
     */
    public void add(X509Data x509DataToAdd)
    {
    	getX509Data().add(x509DataToAdd);
    }
    
    private void setKeyValues(ArrayList<KeyValue> keyValues)
    {
        this.keyValues = keyValues;
    }
    
    private ArrayList<KeyValue> getKeyValues()
    {
        return this.keyValues;
    }
    
	private void setX509Data(ArrayList<X509Data> x509Data) 
	{
		this.x509Data = x509Data;
	}
	
	private ArrayList<X509Data> getX509Data() 
    {
	    return this.x509Data;
    }

    
    /**
     * Converts this class to an XML element for inclusion in an XML document
     * @param document The document to include within this element
     * @return This class as an {@link Element}
     */
	public Element toDom(Document document)
    {
        Element keyInfoElement;
        Iterator<KeyValue> keyValuesIterator;
        KeyValue nextKeyValue;
        X509Data nextX509Data;
        Element nextX509DataElement;
        
        keyInfoElement = null;
        keyValuesIterator = null;
        nextKeyValue = null;
        nextX509Data = null;
        nextX509DataElement = null;
        
        keyInfoElement = document.createElementNS(SignatureConstants.DS_NS, "ds:KeyInfo");
        
        keyValuesIterator = getKeyValues().iterator();
        
        while(keyValuesIterator.hasNext() == true)
        {
            nextKeyValue = keyValuesIterator.next();
            keyInfoElement.appendChild(nextKeyValue.toDom(document));
        }
        
        for(int i = 0; i < getX509Data().size(); i++)
        {
        	nextX509Data = getX509Data().get(i);
        	nextX509DataElement = nextX509Data.toDom(document);
        	keyInfoElement.appendChild(nextX509DataElement);
        }
        
        return keyInfoElement;
    }
}
