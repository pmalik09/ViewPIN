// $Id: PinAgent/src/com/safenetinc/viewpin/common/xml/LoadAndSaveInput.java 1.1 2008/09/04 10:47:40IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xml;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * @author Stuart Horler
 *
 *
 */
public class LoadAndSaveInput implements LSInput
{
    private InputStream byteStream = null;
    private String systemId;
    private String stringData;
    private String publicId;
    private String encoding;
    private Reader characterStream;
    private boolean certifiedText;
    private String baseURI;
    
    /**
     * @see org.w3c.dom.ls.LSInput#getBaseURI()
     */
    public String getBaseURI() 
    {
        return this.baseURI;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#getByteStream()
     */
    public InputStream getByteStream() 
    {
        return this.byteStream;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#getCertifiedText()
     */
    public boolean getCertifiedText()
    {
        return this.certifiedText;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#getCharacterStream()
     */
    public Reader getCharacterStream() 
    {
        return this.characterStream;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#getEncoding()
     */
    public String getEncoding() 
    {
        return this.encoding;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#getPublicId()
     */
    public String getPublicId() 
    {
        return this.publicId;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#getStringData()
     */
    public String getStringData() 
    {
        return this.stringData;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#getSystemId()
     */
    public String getSystemId() 
    {
        return this.systemId;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#setBaseURI(java.lang.String)
     */
    public void setBaseURI(String baseURI) 
    {
        this.baseURI = baseURI;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#setByteStream(java.io.InputStream)
     */
    public void setByteStream(InputStream byteStream) 
    {
        this.byteStream = byteStream;   
    }

    /**
     * @see org.w3c.dom.ls.LSInput#setCertifiedText(boolean)
     */
    public void setCertifiedText(boolean certifiedText) 
    {
        this.certifiedText = certifiedText;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#setCharacterStream(java.io.Reader)
     */
    public void setCharacterStream(Reader characterStream)
    {
        this.characterStream = characterStream;
}

    /**
     * @see org.w3c.dom.ls.LSInput#setEncoding(java.lang.String)
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#setPublicId(java.lang.String)
     */
    public void setPublicId(String publicId)
    {
        this.publicId = publicId;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#setStringData(java.lang.String)
     */
    public void setStringData(String stringData) 
    {
        this.stringData = stringData;
    }

    /**
     * @see org.w3c.dom.ls.LSInput#setSystemId(java.lang.String)
     */
    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }
}
