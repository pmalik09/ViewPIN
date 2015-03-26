// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/PaddingScheme.java 1.1 2008/09/04 10:46:29IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import javax.crypto.NoSuchPaddingException;

import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedPaddingSchemeException;

/**
 * Class to represent padding schemes supported within the PINAgent system
 * 
 * @author Stuart Horler
 *
 *
 */
public class PaddingScheme
{
    /**
     * The PKCS115 scheme
     */
    public static final String PADDING_SCHEME_PKCS115 = "PKCS115";
    /**
     * The OAEP scheme
     */
    public static final String PADDING_SCHEME_OAEP = "OAEP";

    private String paddingScheme = null;

    private PaddingScheme(String paddingScheme)
    {
        super();

        setPaddingScheme(paddingScheme);
    }

    /**
     * Get an instance of this class representing a particular padding scheme
     * @param paddingScheme The padding scheme to represent
     * @return A new instance of this class representing the padding scheme specified
     * @throws UnsupportedPaddingSchemeException Thrown if an invalid padding scheme was specified
     */
    public static PaddingScheme getInstance(String paddingScheme) throws UnsupportedPaddingSchemeException
    {
        PaddingScheme ps;

        ps = null;

        if(paddingScheme.equalsIgnoreCase(PADDING_SCHEME_PKCS115) == true)
        {
            ps = new PaddingScheme(PADDING_SCHEME_PKCS115);
        }
        else
        {
            if(paddingScheme.equalsIgnoreCase(PADDING_SCHEME_OAEP) == true)
            {
                ps = new PaddingScheme(PADDING_SCHEME_OAEP);
            }
            else
            {
                throw new UnsupportedPaddingSchemeException();
            }
        }

        return ps;
    }

    private void setPaddingScheme(String paddingScheme)
    {
        this.paddingScheme = paddingScheme;
    }

    /**
     * @return The padding scheme this class is representing
     */
    public String getPaddingScheme()
    {
        return this.paddingScheme;
    }
    
    /**
     * @return The transformation string matching the padding scheme represented by this class
     * @throws NoSuchPaddingException Thrown if the padding scheme is unsupported by this method
     */
    public String getTransformation() throws NoSuchPaddingException
	{
        String transformation;

	    transformation = null;

	    if(getPaddingScheme().equalsIgnoreCase(PaddingScheme.PADDING_SCHEME_PKCS115) == true)
	    {
	        transformation = "RSA/ECB/PKCS1Padding";
	    }
	    else
	    {
	        if(getPaddingScheme().equalsIgnoreCase(PaddingScheme.PADDING_SCHEME_OAEP) == true)
	        {
	            transformation = "RSA/NONE/OAEPWithSHA1AndMGF1Padding";
	        }
	        else
	        {
	            throw new NoSuchPaddingException("unsupported padding scheme " + getPaddingScheme());
	        }
	    }
	    
	    return transformation;
	}
}