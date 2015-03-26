package com.safenetinc.viewpin.authority.xml.signatures;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;

/**
 * Class to handle creating {@link Signature} objects for specified signature types
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
        super();
    }

    /**
     * Constructor
     * 
     * @param signatureMethodAlgorithm The signature algorithm to use
     * @return The {@link Signature} object
     * @throws NoSuchAlgorithmException Thrown if an invalid signature algorithm is passed
     */
    public static Signature getInstance (String signatureMethodAlgorithm) throws NoSuchAlgorithmException
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

        signatureEngine = Signature.getInstance(algorithm);

        return signatureEngine;
    }
}