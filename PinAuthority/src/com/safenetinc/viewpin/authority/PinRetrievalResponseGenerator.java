// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/PinRetrievalResponseGenerator.java 1.8 2013/09/25 09:46:06IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.safenetinc.viewpin.authority.exceptions.ViewPinRequestException;
import com.safenetinc.viewpin.authority.exceptions.ViewPinResponseException;
import com.safenetinc.viewpin.authority.xml.SimpleNamespaceContext;
import com.safenetinc.viewpin.authority.xml.encryption.XmlEncryptionAlgorithmMapper;
import com.safenetinc.viewpin.authority.xml.signatures.XmlSignature;
import com.safenetinc.viewpin.authority.exceptions.InvalidPrimaryAccountNumberException;

/**
 * Class to create a PinRetrievalResponse for returning to a PINAgent
 * 
 * @author Stuart Horler
 * 
 */
public class PinRetrievalResponseGenerator
{
    private static Logger logger                                          = Logger.getLogger(PinRetrievalResponseGenerator.class);

    private KeyStore      authorityKeyStore                               = null;

    private String        authoritySigningCertificateSubjectKeyIdentifier = null;

    private PrivateKey    authoritySigningKey                             = null;

    /**
     * Creates a new instance of the generator
     * 
     * @param authorityKeyStore The key store holding the certificates for use
     * @param signingSubjectKeyIdentifier The SKI of the signing certificate
     * @throws KeyStoreException Thrown if a problem occurs accessing the key store
     * @throws NoSuchAlgorithmException Thrown if a problem occurs signing the response document
     * @throws CertificateException Thrown if an error occurs with the certificate
     * @throws IOException
     * @throws UnrecoverableKeyException
     */
    public PinRetrievalResponseGenerator(KeyStore authorityKeyStore, String signingSubjectKeyIdentifier) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
            UnrecoverableKeyException
    {
        super();

        PrivateKey authoritySigningKeyToLoad;

        authoritySigningKeyToLoad = null;

        // Store reference to authority key store
        setAuthorityKeyStore(authorityKeyStore);

        // Set authority signing certificate subject key identifier
        setAuthoritySigningCertificateSubjectKeyIdentifier(signingSubjectKeyIdentifier);

        // Get authority signing key
        authoritySigningKeyToLoad = (PrivateKey) getAuthorityKeyStore().getKey(getAuthoritySigningCertificateSubjectKeyIdentifier(), ViewPinConstants.DEFAULT_KEYSTORE_PASSWORD.toCharArray());

        // Did we get authority signing key OK?
        if (authoritySigningKeyToLoad == null)
        {
            // Failed to get authority signing key
            getLogger().error("failed to get authority signing key with subject key identifier " + getAuthoritySigningCertificateSubjectKeyIdentifier());

            throw new KeyStoreException();
        }

        // Store reference to authority signing key
        setAuthoritySigningKey(authoritySigningKeyToLoad);
    }

    /**
     * Generates a PINRetrievalResponse
     * 
     * @param pinRetrievalRequestDocument The request document to read from
     * @param sessionKey The secret key for the session
     * @param encryptionMethodAlgorithm The encryption algorithm to use
     * @param cardAccount The customer account this request pertains to
     * @param signatureMethodAlgorithm The signature algorithm to use
     * @param digestMethodAlgorithm The digest algorithm to use
     * @param randomNumberGenerator The RNG to use
     * @return The {@link PinRetrievalResponse}
     * @throws PinRetrievalRequestException Thrown if an error occurs processing the PINRetrievalRequest
     * @throws PinRetrievalResponseException Thrown if an error occurs generating the PINRetrievalResponse
     */
    public PinRetrievalResponse generatePinRetrievalResponse (Document pinRetrievalRequestDocument, SecretKey sessionKey, String encryptionMethodAlgorithm,CardHolderDetails cardHolderDetails,
            String signatureMethodAlgorithm, String digestMethodAlgorithm, SecureRandom randomNumberGenerator) throws ViewPinRequestException, ViewPinResponseException
    {
    	  PinRetrievalResponse pinRetrievalResponse;
          Document pinRetrievalResponseDocument;
          XPathFactory xpf;
          XPath xp;
          SimpleNamespaceContext nsc;
          String timestamp;
          String transactionIdentifier;
          DocumentBuilderFactory dbf;
          DocumentBuilder db;
          Element rootElement;
          Element timestampElement;
          Element transactionIdentifierElement;
          ArrayList<Pin> cardAccountPins = new ArrayList<Pin>();
          Pin nextPin;
          PrimaryAccountNumber nextPan;
          Element nextCardPinElement;
          Element nextPinElement;
          Element nextPanElement;
          Element nextPinEncryptedDataElement;
          Element nextPanEncryptedDataElement;
          Element signatureElement;
          Element keyInfoElement;
          byte[] compressedPinRetrievalResponseDocument;
          String encodedCompressedPinRetrievalResponseDocument;
        
          pinRetrievalResponse = null;
          pinRetrievalResponseDocument = null;
          xpf = null;
          xp = null;
          nsc = null;
          timestamp = null;
          transactionIdentifier = null;
          dbf = null;
          nextPan = null;
          rootElement = null;
          timestampElement = null;
          transactionIdentifierElement = null;
          nextPin = null;
          nextCardPinElement = null;
          nextPinElement = null;
          nextPanElement = null;
          nextPinEncryptedDataElement = null;
          nextPanEncryptedDataElement = null;
          signatureElement = null;
          keyInfoElement = null;
          compressedPinRetrievalResponseDocument = null;
          encodedCompressedPinRetrievalResponseDocument = null;
         
          // Intialise XPath
          xpf = XPathFactory.newInstance();
          xp = xpf.newXPath();
          nsc = new SimpleNamespaceContext();
          nsc.addNamespace(ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX, ViewPinConstants.VIEWPIN_NAMESPACE_URI);
          xp.setNamespaceContext(nsc);

                           
          try
          {
              // Get time stamp
              timestamp = (String) xp.evaluate("/vp:PinRetrievalRequest/vp:timestamp/text()", pinRetrievalRequestDocument, XPathConstants.STRING);

          }
          catch (Exception e)
          {
              getLogger().error("retrieving timestamp " + e.getMessage());

              throw new ViewPinRequestException("retrieving timestamp");
          }

          try
          {
              // Get transaction identifier
              transactionIdentifier = (String) xp.evaluate("/vp:PinRetrievalRequest/vp:TransactionIdentifier/text()", pinRetrievalRequestDocument, XPathConstants.STRING);
          
          }
          catch (Exception e)
          {
              getLogger().error("retrieving transaction identifier " + e.getMessage());

              throw new ViewPinRequestException("retrieving transaction identifier");
          }
          
         

           // Instantiate document builder factory
          dbf = DocumentBuilderFactory.newInstance();
          dbf.setNamespaceAware(true);

          try
          {
              // Instantiate document builder
              db = dbf.newDocumentBuilder();
          }
          catch (Exception e)
          {
              getLogger().error("instantiating document builder " + e.getMessage());

              throw new ViewPinResponseException("instantiating document builder");
          }

          // Create empty pin retrieval response document
          pinRetrievalResponseDocument = db.newDocument();

          // Create pin retrieval response document root element
          rootElement = pinRetrievalResponseDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX + ":PinRetrievalResponse");
          rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE_URI, ViewPinConstants.NAMESPACE_NAMESPACE_PREFIX + ":" + ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX,
                  ViewPinConstants.VIEWPIN_NAMESPACE_URI);
          rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE_URI, ViewPinConstants.NAMESPACE_NAMESPACE_PREFIX + ":" + ViewPinConstants.XENC_NAMESPACE_PREFIX,
                  ViewPinConstants.XENC_NAMESPACE_URI);
          rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE_URI, ViewPinConstants.NAMESPACE_NAMESPACE_PREFIX + ":" + ViewPinConstants.DSIG_NAMESPACE_PREFIX,
                  ViewPinConstants.DSIG_NAMESPACE_URI);

          
          // Append root element to pin retrieval response document
          pinRetrievalResponseDocument.appendChild(rootElement);
                  
          // Create timestamp element
          timestampElement = pinRetrievalResponseDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX + ":timestamp");

          // Append timestamp value to timestamp element
          timestampElement.appendChild(pinRetrievalResponseDocument.createTextNode(timestamp));

          
          // Append timestamp element to root element of pin retrieval response document
          rootElement.appendChild(timestampElement);

          // Create transaction identifier element
          transactionIdentifierElement = pinRetrievalResponseDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX + ":TransactionIdentifier");

          // Append transaction identifier element to root element of pin retrieval response document
          rootElement.appendChild(transactionIdentifierElement);

          // Append transaction identifier value to transaction identifier element
          transactionIdentifierElement.appendChild(pinRetrievalResponseDocument.createTextNode(transactionIdentifier));
        
          // Get pin numbers associated with card account
          cardAccountPins = cardHolderDetails.getPINNumbers(cardHolderDetails.getPinNumbers());
         
          // Iterate through pin numbers
          for(int i=0;i<cardAccountPins.size();i++)
          {
        	     	  
              // Create CardPin element
              nextCardPinElement = pinRetrievalResponseDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX + ":CardPin");

              // Append CardPin element to root element of pin retrieval response document
              rootElement.appendChild(nextCardPinElement);

              // Create next Pin element
              nextPinElement = pinRetrievalResponseDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX + ":Pin");
              /*
              try
              {
            	  nextPan = new PrimaryAccountNumber(cardHolderDetails.getPrimaryAccountNumber());
              }
              catch(InvalidPrimaryAccountNumberException e)
              {
            	  getLogger().error(e.getMessage());
              }
              // Create next Pan element
              nextPanElement = pinRetrievalResponseDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX + ":PrimaryAccountNumber");
*/
              // Append next pin element to CardPin element
              nextCardPinElement.appendChild(nextPinElement);
              
           // Append next pin element to CardPin element
             // nextCardPinElement.appendChild(nextPanElement);

              // Get next pin
              nextPin = cardAccountPins.get(i);
            
              try
              {
                  // Generate pin EncryptedData element
                  nextPinEncryptedDataElement = generateEncryptedDataElement(pinRetrievalResponseDocument, nextPin.getPin().getBytes(), randomNumberGenerator, sessionKey, encryptionMethodAlgorithm);
              }
              catch (Exception e)
              {
                  getLogger().error("generating pin EncryptedData element " + e.getMessage());

                  throw new ViewPinResponseException("generating pin EncryptedData element");
              }
              /*
              try
              {
                  // Generate pin EncryptedData element
                  nextPanEncryptedDataElement = generateEncryptedDataElement(pinRetrievalResponseDocument, nextPan.getPrimaryAccountNumber().getBytes(), randomNumberGenerator, sessionKey, encryptionMethodAlgorithm);
              }
              catch (Exception e)
              {
                  getLogger().error("generating pin EncryptedData element " + e.getMessage());

                  throw new ViewPinResponseException("generating pin EncryptedData element");
              }
*/
              // Append EncryptedData to pin element
              nextPinElement.appendChild(nextPinEncryptedDataElement);
             
           //   nextPanElement.appendChild(nextPanEncryptedDataElement);
             // doc  = nextPinEncryptedDataElement.toString();
             
          }

          try
          {
              // Create enveloped signature
              signatureElement = XmlSignature.createEnvelopedSignature(pinRetrievalResponseDocument, getAuthoritySigningKey(), signatureMethodAlgorithm, digestMethodAlgorithm);
          }
          catch (Exception e)
          {
              getLogger().error("creating enveloped signature element " + e.getMessage());

              throw new ViewPinResponseException("creating enveloped signature element");
          }

          // Append Signature element to pin retrieval response document
          rootElement.appendChild(signatureElement);

          try
          {
              // Create KeyInfo element
              keyInfoElement = createKeyInfoElement(pinRetrievalResponseDocument);
          }
          catch (Exception e)
          {
              getLogger().error("creating KeyInfo element " + e.getMessage());

              throw new ViewPinResponseException("creating KeyInfo element");
          }

          // Append KeyInfo element to Signature element
          signatureElement.appendChild(keyInfoElement);

                
          try
          {
              // Compress pin retrieval response document
              compressedPinRetrievalResponseDocument = Utils.compressDocument(pinRetrievalResponseDocument);
          }
          catch (Exception e)
          {
              getLogger().error("compressing pin retrieval response document " + e.getMessage());

              throw new ViewPinResponseException("compressing pin retrieval response document");
          }

          // Safe encode compressed pin retrieval response document
          encodedCompressedPinRetrievalResponseDocument = UrlSafeBase64.encode(compressedPinRetrievalResponseDocument);
     
          // Instantiate pin retrieval response
          pinRetrievalResponse = new PinRetrievalResponse(pinRetrievalResponseDocument, encodedCompressedPinRetrievalResponseDocument);

          return pinRetrievalResponse;
    }

    private Element createKeyInfoElement (Document pinRetrievalResponseDocument) throws DecoderException
    {
        Element keyInfoElement;
        Element x509DataElement;
        Element x509SkiElement;
        byte[] hexDecodedAuthoritySigningCertificateSubjectKeyIdentifier;
        String encodedAuthoritySigningCertificateSubjectKeyIdentifier;

        keyInfoElement = null;
        x509DataElement = null;
        x509SkiElement = null;
        hexDecodedAuthoritySigningCertificateSubjectKeyIdentifier = null;
        encodedAuthoritySigningCertificateSubjectKeyIdentifier = null;

        // Create KeyInfo element
        keyInfoElement = XmlSignature.createSignatureElement(pinRetrievalResponseDocument, "KeyInfo");

        // Create X509Data element
        x509DataElement = XmlSignature.createSignatureElement(pinRetrievalResponseDocument, "X509Data");

        // Append X509Data element to KeyInfo element
        keyInfoElement.appendChild(x509DataElement);

        // Create X509SKI element
        x509SkiElement = XmlSignature.createSignatureElement(pinRetrievalResponseDocument, "X509SKI");

        // Append X509SKI element to X509Data element
        x509DataElement.appendChild(x509SkiElement);

        // Hex decode authority signing certificate subject key identifier
        hexDecodedAuthoritySigningCertificateSubjectKeyIdentifier = Hex.decodeHex(getAuthoritySigningCertificateSubjectKeyIdentifier().toCharArray());

        // Encode authority signing certificate subject key identifier
        encodedAuthoritySigningCertificateSubjectKeyIdentifier = new String(Base64.encodeBase64(hexDecodedAuthoritySigningCertificateSubjectKeyIdentifier));

        // Append authority signing certificate subject key identifier to X509SKI element
        x509SkiElement.appendChild(pinRetrievalResponseDocument.createTextNode(encodedAuthoritySigningCertificateSubjectKeyIdentifier));

        return keyInfoElement;
    }

    private Element generateEncryptedDataElement (Document document, byte[] plainText, SecureRandom randomNumberGenerator, SecretKey sessionKey, String encryptionMethod)
            throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException
            
    {
        Element encryptedDataElement;
        Element encryptionMethodElement;
        Element cipherDataElement;
        String transformation;
        Cipher sessionCipher;
        Element cipherValueElement;
        byte[] paddedPlainText;
        IvParameterSpec ivps;
        byte[] cipherText;
        byte[] ivPlusCipherText;
        String encodedIvPlusCipherText;

        encryptedDataElement = null;
        encryptionMethodElement = null;
        cipherDataElement = null;
        transformation = null;
        sessionCipher = null;
        cipherValueElement = null;
        paddedPlainText = null;
        ivps = null;
        cipherText = null;
        ivPlusCipherText = null;
        encodedIvPlusCipherText = null;

        // Create EncryptedData element
        encryptedDataElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE_URI, ViewPinConstants.XENC_NAMESPACE_PREFIX + ":EncryptedData");

        // Set EncryptedData element type attribute
        encryptedDataElement.setAttributeNS(null, "Type", "http://www.w3.org/2001/04/xmlenc#Content");

        // Create EncryptionMethod element
        encryptionMethodElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE_URI, ViewPinConstants.XENC_NAMESPACE_PREFIX + ":EncryptionMethod");

        // Append EncryptionMethod element to EncryptedData element
        encryptedDataElement.appendChild(encryptionMethodElement);

        // Set EncryptionMethod elements algorithm attribute
        encryptionMethodElement.setAttributeNS(null, "Algorithm", encryptionMethod);

        // Create CipherData element
        cipherDataElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE_URI, ViewPinConstants.XENC_NAMESPACE_PREFIX + ":CipherData");

        // Append CipherData to EncrytedData element
        encryptedDataElement.appendChild(cipherDataElement);

        // Create CipherValue element
        cipherValueElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE_URI, ViewPinConstants.XENC_NAMESPACE_PREFIX + ":CipherValue");

        // Append CipherValue element to CipherData element
        cipherDataElement.appendChild(cipherValueElement);

        // Determine session cipher transformation
        transformation = XmlEncryptionAlgorithmMapper.getTransformation(encryptionMethod);

        // Instantiate session cipher
        sessionCipher = Cipher.getInstance("AES/CBC/NoPadding", "LunaProvider");
        
        
     		
        // Generate initialisation vector
        ivps = Utils.generateInitialisationVector(sessionCipher.getBlockSize(), randomNumberGenerator);
        
              
       
        // Initialise session cipher
        sessionCipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivps);

        // Pad plain text
        paddedPlainText = Utils.pad(plainText, sessionCipher.getBlockSize(), randomNumberGenerator);

        // Encrypt plain text
        cipherText = sessionCipher.doFinal(paddedPlainText);

        // Place IV in front of cipher text
        ivPlusCipherText = new byte[ivps.getIV().length + cipherText.length];
        System.arraycopy(ivps.getIV(), 0, ivPlusCipherText, 0, ivps.getIV().length);
        System.arraycopy(cipherText, 0, ivPlusCipherText, ivps.getIV().length, cipherText.length);

        // Encode IV plus cipher text
        encodedIvPlusCipherText = new String(Base64.encodeBase64(ivPlusCipherText));
       

        // Place encoded iv plus cipher text into CipherValue element
        cipherValueElement.appendChild(document.createTextNode(encodedIvPlusCipherText));

        return encryptedDataElement;
    }

    private void setAuthorityKeyStore (KeyStore authorityKeyStore)
    {
        this.authorityKeyStore = authorityKeyStore;
    }

    private KeyStore getAuthorityKeyStore ()
    {
        return this.authorityKeyStore;
    }

    private void setAuthoritySigningCertificateSubjectKeyIdentifier (String authoritySigningCertificateSubjectKeyIdentifier)
    {
        this.authoritySigningCertificateSubjectKeyIdentifier = authoritySigningCertificateSubjectKeyIdentifier;
    }

    private String getAuthoritySigningCertificateSubjectKeyIdentifier ()
    {
        return this.authoritySigningCertificateSubjectKeyIdentifier;
    }

    private void setAuthoritySigningKey (PrivateKey authoritySigningKey)
    {
        this.authoritySigningKey = authoritySigningKey;
    }

    private PrivateKey getAuthoritySigningKey ()
    {
        return this.authoritySigningKey;
    }

    private Logger getLogger ()
    {
        return logger;
    }
}