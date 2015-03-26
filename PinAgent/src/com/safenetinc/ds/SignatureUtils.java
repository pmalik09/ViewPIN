// $Id: PinAgent/src/com/safenetinc/ds/SignatureUtils.java 1.1 2008/09/04 10:45:23IST Mkhurana Exp  $
package com.safenetinc.ds;

import java.io.IOException;

import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Node;

/**
 * Utility class to handle XML Digital Signature canonicalization operations
 * @author Stuart Horler
 *
 *
 */
public class SignatureUtils 
{
    private SignatureUtils() 
    {
		super();
	}

    /**
     * Canonicalizes a {@link Node} ommiting any comments
     * @param node The {@link Node} to canonicalize
     * @return The canonicalized {@link Node}
     * @throws IOException Thrown if an error occurs with the canonicalization
     */
    public static byte[] exclusiveCanonicalizeWithoutComments(Node node) throws IOException
    {
        Canonicalizer canonicalizer;
        byte[] canonicalizedNode;
        
        canonicalizer = null;
        canonicalizedNode = null;
        
        try
        {
            canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            canonicalizedNode = canonicalizer.canonicalizeSubtree(node);
        } 
        catch(Exception e)
        {
            throw new IOException(e.getMessage());   
        }
        
        return canonicalizedNode;
    }
}
