// $Id: PinAgent/src/com/safenetinc/ds/Reference.java 1.1 2008/09/04 10:45:14IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the reference element of an XML Digital Signature document
 * 
 * @author Stuart Horler
 *
 *
 */
public class Reference
{
    private DigestMethod digestMethod = null;
    
    private String uri = null;
    private Transforms transforms = null;
    private DigestValue digestValue = null;
    
    /**
     * Constructor
     * @param digestMethod The {@link DigestMethod} to use
     */
    public Reference(DigestMethod digestMethod)
    {
        super();
        
        setDigestMethod(digestMethod);
    }
    
    /**
     * Set the digest value to use
     * @param digestValue The {@link DigestValue}
     */
    public void setDigestValue(DigestValue digestValue)
    {
        this.digestValue = digestValue;
    }
    
    /**
     * Get the {@link DigestValue}
     * @return The {@link DigestValue} used by this class
     */
    public DigestValue getDigestValue()
    {
        return this.digestValue;
    }
    
    private void setDigestMethod(DigestMethod digestMethod)
    {
        this.digestMethod = digestMethod;
    }
    
    /**
     * @return The {@link DigestMethod} used by this class
     */
    public DigestMethod getDigestMethod()
    {
        return this.digestMethod;
    }
    
    /**
     * Set the URI used by this class
     * @param uri The URI in question
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }
    
    /**
     * @return The URI used by this class
     */
    public String getUri()
    {
        return this.uri;
    }
    
    /**
     * @param transforms The {@link Transforms} used by this class
     */
    public void setTransforms(Transforms transforms)
    {
    	this.transforms = transforms;
    }
    
    /**
     * @return The {@link Transforms} used by this class
     */
    public Transforms getTransforms()
    {
    	return this.transforms;
    }
    
    Element toDom(Document document)
    {
        Element referenceElement;
        
        referenceElement = document.createElementNS(SignatureConstants.DS_NS, "ds:Reference");
        referenceElement.setAttribute("URI", getUri());
        referenceElement.appendChild(getTransforms().toDom(document));
        referenceElement.appendChild(getDigestMethod().toDom(document));
        referenceElement.appendChild(getDigestValue().toDom(document));
        
        return referenceElement;
    }
}