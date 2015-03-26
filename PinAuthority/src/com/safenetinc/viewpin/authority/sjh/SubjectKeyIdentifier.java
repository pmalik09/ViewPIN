// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/sjh/SubjectKeyIdentifier.java 1.1 2012/07/19 11:23:42IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.sjh;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;


/**
 * Class to represent a Subject Key Identifier
 * 
 * @author Stuart Horler
 *
 *
 */
public class SubjectKeyIdentifier
{
	private static final int LENGTH_SHA1 = 20;

	private String hexEncoded = null;
    private byte[] unencoded = null;
    private String base64Encoded = null;

    /**
     * Constructor
     * @param hexEncoded The hex encoded SKI
     * @throws InvalidSubjectKeyIdentifierException Thrown if the specified SKI was invalid
     */
    public SubjectKeyIdentifier(String hexEncoded) throws InvalidSubjectKeyIdentifierException
    {
        super();

    	byte[] unencodedSKI;

    	// Ensure hex encoded subject key identifier is not null
    	if(hexEncoded == null)
    	{
    		throw new InvalidSubjectKeyIdentifierException("is null");
    	}

    	try
        {
            // Ensure subject key identifier is correctly encoded
            unencodedSKI = Hex.decodeHex(hexEncoded.toCharArray());
        }
        catch(DecoderException de)
        {
        	throw new InvalidSubjectKeyIdentifierException("invalid hex encoding");
        }

        // Ensure length of subject key identifier is supported
        if(validateLength(unencodedSKI.length) == false)
        {
        	throw new InvalidSubjectKeyIdentifierException("unsupported length");
        }

        setHexEncoded(hexEncoded);
        setUnencoded(unencodedSKI);
        setBase64Encoded(new String(Base64.encodeBase64(getUnencoded(), false)));
    }

    /**
     * Constructor
     * @param unencoded The unencoded raw SKI
     * @throws InvalidSubjectKeyIdentifierException Thrown if the specified SKI was invalid
     */
    public SubjectKeyIdentifier(byte[] unencoded) throws InvalidSubjectKeyIdentifierException
    {
        super();

       	// Ensure unencoded subject key identifier is not null
    	if(unencoded == null)
    	{
    		throw new InvalidSubjectKeyIdentifierException("is null");
    	}

        // Ensure length of subject key identifier is supported
        if(validateLength(unencoded.length) == false)
        {
        	throw new InvalidSubjectKeyIdentifierException("unsupported length");
        }

        setUnencoded(unencoded);
        setHexEncoded(new String(Hex.encodeHex(unencoded)));
        setBase64Encoded(new String(Base64.encodeBase64(getUnencoded(), false)));
    }

    private void setHexEncoded(String hexEncoded)
    {
        this.hexEncoded = hexEncoded;
    }

    /**
     * @return The hex-encoded SKI
     */
    public String getHexEncoded()
    {
        return this.hexEncoded;
    }

    private void setUnencoded(byte[] unencoded)
    {
        this.unencoded = unencoded;
    }

    /**
     * @return The unencoded SKI
     */
    public byte[] getUnencoded()
    {
        return this.unencoded.clone();
    }

    private boolean validateLength(int length)
    {
    	boolean rc;

        rc = false;
        
        if(length == LENGTH_SHA1)
        {
        	rc = true;
        }
        
    	return rc;
    }
    
    private void setBase64Encoded(String base64Encoded) 
    {
		this.base64Encoded = base64Encoded;
	}
    
    /**
     * @return The base64 encoded SKI
     */
    public String getBase64Encoded() 
    {
		return this.base64Encoded;
	}

	/**
	 * Returns the hex encoded version of the SKI
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString()
    {
    	return getHexEncoded();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object)
    {
        boolean equal;
        SubjectKeyIdentifier ski;
        
        equal = false;
        ski = null;
        
        if(object == null)
        {
            return equal;
        }
        
        if(object instanceof SubjectKeyIdentifier == false)
        {
            return equal;
        }
        
      
        
        ski = (SubjectKeyIdentifier)object;
        
        if(ski.getHexEncoded().compareToIgnoreCase(this.getHexEncoded()) == 0)
        {
            equal = true;
        }
        
        return equal;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        assert false: "HashCode not implemented";
        return 42;
    }
}