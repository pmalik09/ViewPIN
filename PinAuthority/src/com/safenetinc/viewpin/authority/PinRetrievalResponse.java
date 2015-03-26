// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/PinRetrievalResponse.java 1.1 2008/09/15 11:03:03IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority;

import org.w3c.dom.Document;

/**
 * Class to hold a PINRetrievalResponse document
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class PinRetrievalResponse
{
    private Document pinRetrievalResponseDocument                  = null;

    private String   encodedCompressedPinRetrievalResponseDocument = null;

    /**
     * Constructor
     * 
     * @param pinRetrievalResponseDocument The PINRetrievalResponse document to store
     * @param encodedCompressedPinRetrievalResponseDocument The encoded compressed version of the
     *        PINRetrievalRequest document
     */
    public PinRetrievalResponse(Document pinRetrievalResponseDocument, String encodedCompressedPinRetrievalResponseDocument)
    {
        super();

        setPinRetrievalResponseDocument(pinRetrievalResponseDocument);
        setEncodedCompressedPinRetrievalResponseDocument(encodedCompressedPinRetrievalResponseDocument);
    }

    private void setEncodedCompressedPinRetrievalResponseDocument (String encodedCompressedPinRetrievalResponseDocument)
    {
        this.encodedCompressedPinRetrievalResponseDocument = encodedCompressedPinRetrievalResponseDocument;
    }

    /**
     * @return The encoded, compressed PINRetrievalRequest document
     */
    public String getEncodedCompressedPinRetrievalResponseDocument ()
    {
        return this.encodedCompressedPinRetrievalResponseDocument;
    }

    private void setPinRetrievalResponseDocument (Document pinRetrievalResponseDocument)
    {
        this.pinRetrievalResponseDocument = pinRetrievalResponseDocument;
    }

    /**
     * @return The PINRetrievalResponse document
     */
    public Document getPinRetrievalResponseDocument ()
    {
        return this.pinRetrievalResponseDocument;
    }
}