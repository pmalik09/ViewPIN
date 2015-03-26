// $Id: PinAgent/src/com/safenetinc/viewpin/common/xencsigmap/SignatureFactory.java 1.2 2013/09/25 09:45:00IST Malik, Pratibha (Pmalik) Exp  $

package com.safenetinc.viewpin.common.xencsigmap;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

/**
 * Factory class to handle generation of {@link Signature} objects for signing operations
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class SignatureFactory
{
    private static final String ALGORITHM_RSA_MD5    = "MD5withRSA";

    private static final String ALGORITHM_RSA_SHA1   = "SHA1withRSA";

    private static final String ALGORITHM_RSA_SHA256 = "SHA256withRSA";

    private static final String ALGORITHM_RSA_SHA384 = "SHA384withRSA";

    private static final String ALGORITHM_RSA_SHA512 = "SHA512withRSA";

    private SignatureFactory()
    {
        // Private constructor, nothing to do here
    }

    /**
     * Return a new {@link Signature} object
     * 
     * @param signatureMethodAlgorithm String representing the algorithm to use. These are held as constants
     *        within the {@link XmlSignatureMethodAlgorithms} class
     * @return The new Signature object
     * @throws NoSuchAlgorithmException Thrown if an invalid signature algorithm is specified
     * @throws NoSuchProviderException Thrown if an error occurs with the underlying provider
     */
    public static Signature getInstance (String signatureMethodAlgorithm) throws NoSuchAlgorithmException, NoSuchProviderException
    {
        Signature signatureEngine;
        String algorithm;

        signatureEngine = null;
        algorithm = null;

        if (signatureMethodAlgorithm.compareTo(XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_MD5) == 0)
        {
            algorithm = ALGORITHM_RSA_MD5;
        }
        else if (signatureMethodAlgorithm.compareTo(XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_SHA1) == 0)
        {
            algorithm = ALGORITHM_RSA_SHA1;
        }
        else if (signatureMethodAlgorithm.compareTo(XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_SHA256) == 0)
        {
            algorithm = ALGORITHM_RSA_SHA256;
        }
        else if (signatureMethodAlgorithm.compareTo(XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_SHA384) == 0)
        {
            algorithm = ALGORITHM_RSA_SHA384;
        }
        else if (signatureMethodAlgorithm.compareTo(XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_SHA512) == 0)
        {
            algorithm = ALGORITHM_RSA_SHA512;
        }
        else
        {
            throw new NoSuchAlgorithmException();
        }

        signatureEngine = Signature.getInstance(algorithm, "LunaProvider");

        return signatureEngine;
    }
}