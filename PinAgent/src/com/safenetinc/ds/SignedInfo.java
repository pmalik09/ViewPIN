// $Id: PinAgent/src/com/safenetinc/ds/SignedInfo.java 1.1 2008/09/04 10:45:26IST Mkhurana Exp  $
package com.safenetinc.ds;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the Signed Info element of an XML Digital Signature document
 * @author Stuart Horler
 *
 */
public class SignedInfo
{
    private CanonicalizationMethod canonicalizationMethod = null;
    private SignatureMethod signatureMethod = null;
    private ArrayList<Reference> references = null;
    
    /**
     * Constructor
     * @param canonicalizationMethod The {@link CanonicalizationMethod} to embed into this document element
     * @param signatureMethod The {@link SignatureMethod} to embed into this document element
     */
    public SignedInfo(CanonicalizationMethod canonicalizationMethod, SignatureMethod signatureMethod)
    {
        super();
        
        setCanonicalizationMethod(canonicalizationMethod);
        setSignatureMethod(signatureMethod);
        setReferences(new ArrayList<Reference>());
    }
    
    /**
     * Add a reference element to this element
     * @param reference The {@link Reference} to add
     */
    public void addReference(Reference reference)
    {
        getReferences().add(reference);
    }
    
    Iterator<Reference> getReferencesIterator()
    {
        return getReferences().iterator();
    }
    
    private void setCanonicalizationMethod(CanonicalizationMethod canonicalizationMethod)
    {
        this.canonicalizationMethod = canonicalizationMethod;
    }
    
    /**
     * @return The {@link CanonicalizationMethod} used by this class
     */
    public CanonicalizationMethod getCanonicalizationMethod()
    {
        return this.canonicalizationMethod;
    }
    
    private void setSignatureMethod(SignatureMethod signatureMethod)
    {
        this.signatureMethod = signatureMethod;
    }
    
    /**
     * @return The {@link SignatureMethod} used by this class
     */
    public SignatureMethod getSignatureMethod()
    {
        return this.signatureMethod;
    }
    
    private void setReferences(ArrayList<Reference> references)
    {
        this.references = references;
    }
    
    private ArrayList<Reference> getReferences()
    {
        return this.references;
    }
    
    Element toDom(Document document)
    {
        Element signedInfoElement;
        Iterator<Reference> referencesIterator;
        Reference nextReference;
        Element nextReferenceElement;
        
        signedInfoElement = null;
        referencesIterator = null;
        nextReference = null;
        nextReferenceElement = null;
        
        signedInfoElement = document.createElementNS(SignatureConstants.DS_NS, "ds:SignedInfo");
        signedInfoElement.appendChild(getCanonicalizationMethod().toDom(document));
        signedInfoElement.appendChild(getSignatureMethod().toDom(document));
        
        referencesIterator = getReferences().iterator();
        
        while(referencesIterator.hasNext() == true)
        {
            nextReference = referencesIterator.next();
            nextReferenceElement = nextReference.toDom(document);
            
            signedInfoElement.appendChild(nextReferenceElement);
        }
        
        return signedInfoElement;
    }
}
