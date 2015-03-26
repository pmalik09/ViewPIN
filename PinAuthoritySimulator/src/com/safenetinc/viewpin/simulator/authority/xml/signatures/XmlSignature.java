package com.safenetinc.viewpin.simulator.authority.xml.signatures;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.safenetinc.viewpin.simulator.authority.Utils;
import com.safenetinc.viewpin.simulator.authority.ViewPinConstants;

/**
 * Class to handle XML Digital Signatures
 * 
 * @author Stuart Horler
 */
public class XmlSignature
{
    private static final Logger logger                                     = Logger.getLogger(XmlSignature.class);

    private static final String EXCLUSIVE_CANONICALIZATON_WITHOUT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";

    private static final String ENVELOPED_SIGNATURE                        = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

    private XmlSignature()
    {
        super();
    }

    /**
     * Method to create an enveloped signature on a document
     * 
     * @param document The document to sign
     * @param signingKey The signing key to use
     * @param signatureMethodAlgorithm The signature algorithm to use
     * @param digestMethodAlgorithm The message digest algorithm to user
     * @return An element containing the signed document
     * @throws InvalidCanonicalizerException Thrown if the canonicaliser is invalid
     * @throws CanonicalizationException Thrown if an error occurs during canonicalisation
     * @throws NoSuchAlgorithmException Thrown if an invalid algorithm type is specified
     * @throws InvalidKeyException Thrown if an invalid key is specified
     * @throws SignatureException Thrown if an error occurred during the signing
     */
    public static Element createEnvelopedSignature (Document document, PrivateKey signingKey, String signatureMethodAlgorithm, String digestMethodAlgorithm) throws InvalidCanonicalizerException,
            CanonicalizationException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        Element signatureElement;
        Element signedInfoElement;
        Element canonicalizationMethodElement;
        Element signatureMethodElement;
        Element referenceElement;
        Element transformsElement;
        Element envelopedSignatureTransformElement;
        Element exclusiveCanonicalizationTransformElement;
        Element digestMethodElement;
        Element digestValueElement;
        Canonicalizer canonicalizer;
        byte[] canonicalizedDocument;
        MessageDigest md;
        byte[] digestedCanonicalizedDocument;
        String encodedDigestedCanonicalizedDocument;
        byte[] canonicalizedSignedInfoElement;
        Signature signatureEngine;
        byte[] signedCanonicalizedSignedInfoElement;
        String encodedSignedCanonicalizedSignedInfoElement;
        Element signatureValueElement;

        signatureElement = null;
        signedInfoElement = null;
        canonicalizationMethodElement = null;
        signatureMethodElement = null;
        referenceElement = null;
        transformsElement = null;
        envelopedSignatureTransformElement = null;
        exclusiveCanonicalizationTransformElement = null;
        digestMethodElement = null;
        digestValueElement = null;
        canonicalizer = null;
        canonicalizedDocument = null;
        md = null;
        digestedCanonicalizedDocument = null;
        encodedDigestedCanonicalizedDocument = null;
        canonicalizedSignedInfoElement = null;
        signatureEngine = null;
        signedCanonicalizedSignedInfoElement = null;
        encodedSignedCanonicalizedSignedInfoElement = null;
        signatureValueElement = null;

        // Create Signature element
        signatureElement = createSignatureElement(document, "Signature");
        signatureElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE_URI, ViewPinConstants.NAMESPACE_NAMESPACE_PREFIX + ":" + ViewPinConstants.DSIG_NAMESPACE_PREFIX,
                ViewPinConstants.DSIG_NAMESPACE_URI);

        // Create SignedInfo element
        signedInfoElement = createSignatureElement(document, "SignedInfo");

        // Append SignedInfo element to Signature element
        signatureElement.appendChild(signedInfoElement);

        // Create canonicalization method element
        canonicalizationMethodElement = createSignatureElement(document, "CanonicalizationMethod");

        // Set canonicalization algorithm attribute
        canonicalizationMethodElement.setAttributeNS(null, "Algorithm", EXCLUSIVE_CANONICALIZATON_WITHOUT_COMMENTS);

        // Append canonicalization method element to SignedInfo element
        signedInfoElement.appendChild(canonicalizationMethodElement);

        // Create SignatureMethod element
        signatureMethodElement = createSignatureElement(document, "SignatureMethod");

        // Set SignatureMethod algorithm
        signatureMethodElement.setAttributeNS(null, "Algorithm", signatureMethodAlgorithm);

        // Append SignatureMethod element to SignedInfo element
        signedInfoElement.appendChild(signatureMethodElement);

        // Create Reference element
        referenceElement = createSignatureElement(document, "Reference");

        // Set Reference element URI attribute
        referenceElement.setAttributeNS(null, "URI", "");

        // Append Reference element to SignedInfo element
        signedInfoElement.appendChild(referenceElement);

        // Create Transforms element
        transformsElement = createSignatureElement(document, "Transforms");

        // Append Transforms element to Reference element
        referenceElement.appendChild(transformsElement);

        // Create enveloped signature Transform element
        envelopedSignatureTransformElement = createSignatureElement(document, "Transform");

        // Set enveloped signature Transform element Algorithm attribute
        envelopedSignatureTransformElement.setAttributeNS(null, "Algorithm", ENVELOPED_SIGNATURE);

        // Append enveloped signature Transform element to Transforms element
        transformsElement.appendChild(envelopedSignatureTransformElement);

        // Create exclusive canonicalization Transform element
        exclusiveCanonicalizationTransformElement = createSignatureElement(document, "Transform");

        // Set exclusive canonicalization Transform element Algorithm attribute
        exclusiveCanonicalizationTransformElement.setAttributeNS(null, "Algorithm", EXCLUSIVE_CANONICALIZATON_WITHOUT_COMMENTS);

        // Append exclusive cannonicalization Transform element to Transforms element
        transformsElement.appendChild(exclusiveCanonicalizationTransformElement);

        // Create DigestMethod element
        digestMethodElement = createSignatureElement(document, "DigestMethod");

        // Set DigestMethod element Algorithm attribute
        digestMethodElement.setAttributeNS(null, "Algorithm", digestMethodAlgorithm);

        // Append DigestMethod element to reference element
        referenceElement.appendChild(digestMethodElement);

        // Create DigestValue element
        digestValueElement = createSignatureElement(document, "DigestValue");

        // Append DigestValue element to Reference element
        referenceElement.appendChild(digestValueElement);

        // Canonicalize document
        canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        canonicalizedDocument = canonicalizer.canonicalizeSubtree(document);

        // Instantiate message digest engine
        md = MessageDigestFactory.getInstance(digestMethodAlgorithm);

        // Digest canonicalized document
        digestedCanonicalizedDocument = md.digest(canonicalizedDocument);

        // Encode canonicalized document
        encodedDigestedCanonicalizedDocument = new String(Base64.encodeBase64(digestedCanonicalizedDocument, false));

        // Append encoded digested canonicalized document to DigestValue element
        digestValueElement.appendChild(document.createTextNode(encodedDigestedCanonicalizedDocument));

        // Canonicalize SignedInfo element
        canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        canonicalizedSignedInfoElement = canonicalizer.canonicalizeSubtree(signedInfoElement);

        // Instantiate Signature engine
        signatureEngine = SignatureFactory.getInstance(signatureMethodAlgorithm);
        signatureEngine.initSign(signingKey);
        signatureEngine.update(canonicalizedSignedInfoElement);
        signedCanonicalizedSignedInfoElement = signatureEngine.sign();

        // Encode signed canonicalized SignedInfo element
        encodedSignedCanonicalizedSignedInfoElement = new String(Base64.encodeBase64(signedCanonicalizedSignedInfoElement, false));

        // Create SignatureValue element
        signatureValueElement = createSignatureElement(document, "SignatureValue");

        // Append encoded signed canonicalized SignedInfo element to SignatureValue element
        signatureValueElement.appendChild(document.createTextNode(encodedSignedCanonicalizedSignedInfoElement));

        // Append SignatureValue element to Signature element
        signatureElement.appendChild(signatureValueElement);

        return signatureElement;
    }

    /**
     * Creates a signature element
     * 
     * @param document
     * @param name
     * @return The signature element
     */
    public static Element createSignatureElement (Document document, String name)
    {
        Element element;

        element = null;

        // Create element
        element = document.createElementNS(ViewPinConstants.DSIG_NAMESPACE_URI, ViewPinConstants.DSIG_NAMESPACE_PREFIX + ":" + name);

        return element;
    }

    /**
     * Verifies the signature in an XML Document
     * 
     * @param pinRetrievalRequestDocument The document to verify
     * @param agentKeyStore The keystore in which the signing certificate can be found
     * @return boolean denoting signature validity
     * 
     * @throws XPathExpressionException
     * @throws KeyStoreException
     * @throws InvalidKeyException
     * @throws InvalidCanonicalizerException
     * @throws CanonicalizationException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws SignatureException
     * @throws XPathFactoryConfigurationException
     */
    public static boolean verifySignature (Document pinRetrievalRequestDocument, KeyStore agentKeyStore) throws XPathExpressionException, KeyStoreException, InvalidKeyException,
            InvalidCanonicalizerException, CanonicalizationException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException,
            XPathFactoryConfigurationException
    {
        boolean signatureValid;
        XPath xp;
        Element pinRetrievalRequestElement;
        Element signatureElement;
        String hexEncodedAgentSigningCertificateSubjectKeyIdentifer;
        Certificate agentSigningCertificate;

        signatureValid = false;
        xp = null;
        pinRetrievalRequestElement = null;
        signatureElement = null;
        hexEncodedAgentSigningCertificateSubjectKeyIdentifer = null;
        agentSigningCertificate = null;

        // Initialise XPath object
        xp = Utils.createXPath();

        // Get PinRetrievalRequest element
        pinRetrievalRequestElement = (Element) xp.evaluate("/vp:PinRetrievalRequest", pinRetrievalRequestDocument, XPathConstants.NODE);

        // Get Signature element
        signatureElement = (Element) xp.evaluate("ds:Signature", pinRetrievalRequestElement, XPathConstants.NODE);

        // Get agent signing certificate subject key identifier
        hexEncodedAgentSigningCertificateSubjectKeyIdentifer = identifyAgentSigningCertificate(signatureElement);

        // Did we get agent signing certificate subject key identifier OK?
        if (hexEncodedAgentSigningCertificateSubjectKeyIdentifer == null)
        {
            // Failed to get agent signing certificate subject key identifier
            getLogger().error("failed to get agent signing certificate subject key identifier");

            signatureValid = false;

            return signatureValid;
        }

        getLogger().debug("agent signing certificate subject key identifier = " + hexEncodedAgentSigningCertificateSubjectKeyIdentifer);

        // Get agent signing certificate from agent key store
        agentSigningCertificate = agentKeyStore.getCertificate(hexEncodedAgentSigningCertificateSubjectKeyIdentifer);

        // Did we get agent signing certificate from key store?
        if (agentSigningCertificate == null)
        {
            // Failed to get agent signing certificate from key store
            getLogger().error("retrieving agent signing certificate " + hexEncodedAgentSigningCertificateSubjectKeyIdentifer);

            signatureValid = false;

            return signatureValid;
        }

        // Verify SignedInfo element
        if (verifySignedInfo(signatureElement, agentSigningCertificate) == false)
        {
            getLogger().debug("verifying SignedInfo element");

            signatureValid = false;

            return signatureValid;
        }

        getLogger().debug("SignedInfo element verified OK");

        // Verify enveloped signature reference
        if (verifyEnvelopedSignatureReference(pinRetrievalRequestDocument) == false)
        {
            // Failed to verify enveloped signature reference
            getLogger().warn("failed to verify enveloped signature reference");

            signatureValid = false;
        }

        getLogger().debug("verified enveloped signature reference ok");

        signatureValid = true;

        return signatureValid;
    }

    private static boolean verifySignedInfo (Element signatureElement, Certificate signingCertificate) throws XPathExpressionException, InvalidCanonicalizerException, CanonicalizationException,
            NoSuchAlgorithmException, InvalidKeyException, SignatureException, XPathFactoryConfigurationException
    {
        boolean signedInfoValid;
        XPath xp;
        Element signedInfoElement;
        Canonicalizer canonicalizer;
        byte[] canonicalizedSignedInfo;
        String encodedSignatureValue;
        byte[] decodedSignatureValue;
        String signatureMethodAlgorithm;
        Signature signatureEngine;

        signedInfoValid = false;
        xp = null;
        signedInfoElement = null;
        canonicalizer = null;
        canonicalizedSignedInfo = null;
        encodedSignatureValue = null;
        decodedSignatureValue = null;
        signatureMethodAlgorithm = null;
        signatureEngine = null;

        // Initialise XPath object
        xp = Utils.createXPath();

        // Get SignedInfo element
        signedInfoElement = (Element) xp.evaluate("ds:SignedInfo", signatureElement, XPathConstants.NODE);

        // Did we get SignedInfo element OK?
        if (signedInfoElement == null)
        {
            // Failed to retrieve SignedInfo element
            getLogger().error("retreiving SignedInfo element");

            signedInfoValid = false;

            return signedInfoValid;
        }

        // Canonicalize SignedInfo element
        canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        canonicalizedSignedInfo = canonicalizer.canonicalizeSubtree(signedInfoElement);

        // Decode signature value
        encodedSignatureValue = (String) xp.evaluate("ds:SignatureValue/text()", signatureElement, XPathConstants.STRING);

        // Did we get encoded signature value OK?
        if (encodedSignatureValue == null)
        {
            // Failed to get encoded signature value
            getLogger().error("retreiving encoded signature value");

            signedInfoValid = false;

            return signedInfoValid;
        }

        // Ensure signature value is correctly encoded
        if (Base64.isArrayByteBase64(encodedSignatureValue.getBytes()) == false)
        {
            // Signature value is not correctly encoded
            getLogger().error("signature value is not correctly encoded");

            signedInfoValid = false;

            return signedInfoValid;
        }

        // Decode signature value
        decodedSignatureValue = Base64.decodeBase64(encodedSignatureValue.getBytes());

        // Get signature method algorithm
        signatureMethodAlgorithm = (String) xp.evaluate("ds:SignatureMethod/@Algorithm", signedInfoElement, XPathConstants.STRING);

        // Did we get signature method algorithm OK?
        if (signatureMethodAlgorithm == null)
        {
            // Failed to retrieve signature method algorithm
            getLogger().error("retrieving signautre method algorithm");

            signedInfoValid = false;

            return signedInfoValid;
        }

        getLogger().debug("signature method algorithm = " + signatureMethodAlgorithm);

        // Get signature factory instance
        signatureEngine = SignatureFactory.getInstance(signatureMethodAlgorithm);

        getLogger().debug("signature engine algorithm = " + signatureEngine.getAlgorithm());
        getLogger().debug("signature engine provider = " + signatureEngine.getProvider());

        // Initialise signature engine for verification
        signatureEngine.initVerify(signingCertificate);

        // Verify signature
        signatureEngine.update(canonicalizedSignedInfo);
        signedInfoValid = signatureEngine.verify(decodedSignatureValue);

        return signedInfoValid;
    }

    private static boolean verifyEnvelopedSignatureReference (Document pinRetrievalRequestDocument) throws XPathExpressionException, InvalidCanonicalizerException, CanonicalizationException,
            NoSuchAlgorithmException, XPathFactoryConfigurationException
    {
        boolean referenceValid;
        XPath xp;
        Element pinRetrievalRequestElement;
        Element signatureElement;
        String encodedDigestValue;
        String digestMethodAlgorithm;
        Canonicalizer canonicalizer;
        byte[] canonicalizedPinRetrievalRequestMinusSignature;
        MessageDigest md;
        byte[] digestedCanonicalizedPinRetrievalRequestMinusSignature;
        String encodedDigestedCanonicalizedPinRetrievalRequestMinusSignature;

        referenceValid = false;
        xp = null;
        pinRetrievalRequestElement = null;
        encodedDigestValue = null;
        digestMethodAlgorithm = null;
        canonicalizer = null;
        canonicalizedPinRetrievalRequestMinusSignature = null;
        md = null;
        digestedCanonicalizedPinRetrievalRequestMinusSignature = null;
        encodedDigestedCanonicalizedPinRetrievalRequestMinusSignature = null;

        // Initialise XPath object
        xp = Utils.createXPath();

        // Get PinRetrievalRequest element
        pinRetrievalRequestElement = (Element) xp.evaluate("/vp:PinRetrievalRequest", pinRetrievalRequestDocument, XPathConstants.NODE);

        // Get Signature element
        signatureElement = (Element) xp.evaluate("ds:Signature", pinRetrievalRequestElement, XPathConstants.NODE);

        // Get digest value
        encodedDigestValue = (String) xp.evaluate("/vp:PinRetrievalRequest/ds:Signature/ds:SignedInfo/ds:Reference/ds:DigestValue/text()", pinRetrievalRequestDocument, XPathConstants.STRING);

        // Get digest method algorithm
        digestMethodAlgorithm = (String) xp.evaluate("/vp:PinRetrievalRequest/ds:Signature/ds:SignedInfo/ds:Reference/ds:DigestMethod/@Algorithm", pinRetrievalRequestDocument, XPathConstants.STRING);

        getLogger().debug("digest method algorithm = " + digestMethodAlgorithm);

        // Remove Signaure element from PinRetrievalRequest
        pinRetrievalRequestElement.removeChild(signatureElement);

        // Canonicalize PinRetrievalRequest minus Signature element element
        canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        canonicalizedPinRetrievalRequestMinusSignature = canonicalizer.canonicalizeSubtree(pinRetrievalRequestElement);

        // Instantiate message digest engine
        md = MessageDigestFactory.getInstance(digestMethodAlgorithm);

        getLogger().debug("message digest engine algorithm = " + md.getAlgorithm());
        getLogger().debug("message digest engine provider = " + md.getProvider());

        // Digest canonicalized pin retrieval request minus signature
        digestedCanonicalizedPinRetrievalRequestMinusSignature = md.digest(canonicalizedPinRetrievalRequestMinusSignature);

        // Encode digested canonicalized pin retrieval request minus signature
        encodedDigestedCanonicalizedPinRetrievalRequestMinusSignature = new String(Base64.encodeBase64(digestedCanonicalizedPinRetrievalRequestMinusSignature, false));

        // Ensure encoded canonicalized pin retrieval request minus signature matches encoded digest value
        if (encodedDigestedCanonicalizedPinRetrievalRequestMinusSignature.equals(encodedDigestValue) == true)
        {
            referenceValid = true;
        }

        return referenceValid;
    }

    private static String identifyAgentSigningCertificate (Element signatureElement) throws XPathExpressionException, XPathFactoryConfigurationException
    {
        String hexEncodedAgentSigningCertificateSubjectKeyIdentifer;
        XPath xp;
        String encodedAgentSigningCertificateSubjectKeyIdentifer;
        byte[] decodedAgentSigningCertificateSubjectKeyIdentifer;

        hexEncodedAgentSigningCertificateSubjectKeyIdentifer = null;
        xp = null;
        encodedAgentSigningCertificateSubjectKeyIdentifer = null;
        decodedAgentSigningCertificateSubjectKeyIdentifer = null;

        // Initialise XPath object
        xp = Utils.createXPath();

        // Get agent signing certificate subject key identifier
        encodedAgentSigningCertificateSubjectKeyIdentifer = (String) xp.evaluate("ds:KeyInfo/ds:X509Data/ds:X509SKI/text()", signatureElement, XPathConstants.STRING);

        // Did we get agent signing certificate subject key identifier OK?
        if (encodedAgentSigningCertificateSubjectKeyIdentifer == null)
        {
            // Failed to get agent signing certificate subject key identifier
            getLogger().error("retrieving agent signing certificate subject key identifier");

            return null;
        }

        // Is agent signing certificate subject key identifier correctly encoded?
        if (Base64.isArrayByteBase64(encodedAgentSigningCertificateSubjectKeyIdentifer.getBytes()) == false)
        {
            // Agent signing certificate subject key identifier is not correctly encoded
            getLogger().error("agent signing certificate subject key identifier is not correctly encoded");

            return null;
        }

        // Base64 decode agent signing certificate subject key identifier
        decodedAgentSigningCertificateSubjectKeyIdentifer = Base64.decodeBase64(encodedAgentSigningCertificateSubjectKeyIdentifer.getBytes());

        // Hex encode agent signing certificate subject key identifier
        hexEncodedAgentSigningCertificateSubjectKeyIdentifer = new String(Hex.encodeHex(decodedAgentSigningCertificateSubjectKeyIdentifer));

        return hexEncodedAgentSigningCertificateSubjectKeyIdentifer;
    }

    private static Logger getLogger ()
    {
        return logger;
    }
}