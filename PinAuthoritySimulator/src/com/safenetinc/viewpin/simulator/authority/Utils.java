// $Id: PinAuthoritySimulator/src/com/safenetinc/viewpin/simulator/authority/Utils.java 1.1 2008/09/04 10:49:19IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.safenetinc.viewpin.simulator.authority.xml.SimpleNamespaceContext;
import com.safenetinc.viewpin.simulator.authority.xml.ValidationResult;
import com.safenetinc.viewpin.simulator.authority.xml.resources.FileSystemResourceResolver;

/**
 * Class to handle utility operations for the PINAuthority
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class Utils
{
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private Utils()
    {
        super();
    }

    /**
     * Parses a date time string
     * 
     * @param dateTime string containing a date/time representation
     * @return {@link Date} object matching the supplied date time string
     * @throws ParseException Thrown if an invalid date time string was supplied
     */
    public static final Date parseDateTime (String dateTime) throws ParseException
    {
        Date parsedDate;
        SimpleDateFormat formatter;

        parsedDate = null;
        formatter = null;

        formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        parsedDate = formatter.parse(dateTime);

        return parsedDate;
    }

    /**
     * Formats a {@link Date} object into form yyyy-MM-dd'T'HH:mm:ss'Z'
     * 
     * @param dateToBeFormatted
     * @return String containing the formatted Date
     */
    public static final String formatDate (final Date dateToBeFormatted)
    {
        String formattedDate;
        SimpleDateFormat formatter;

        formattedDate = null;
        formatter = null;

        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        formattedDate = formatter.format(dateToBeFormatted);

        return formattedDate;
    }

    /**
     * Decompresses an compressed byte array
     * 
     * @param compressedData Compressed data array
     * @param decompressionReadBufferLength Length of the decompression buffer
     * @return Decompressed version of supplied compressed data
     * @throws IOException Thrown if invalid compressed data is supplied
     */
    public static byte[] decompress (byte[] compressedData, int decompressionReadBufferLength) throws IOException
    {
        ByteArrayOutputStream decompressedData;
        ByteArrayInputStream bais;
        InflaterInputStream iis;
        byte[] nextDecompressedDataChunk;
        int nextDecompressedDataChunkLength;
        byte[] result;

        decompressedData = null;
        bais = null;
        iis = null;
        nextDecompressedDataChunk = null;
        nextDecompressedDataChunkLength = 0;
        result = null;

        try
        {
            decompressedData = new ByteArrayOutputStream();
            bais = new ByteArrayInputStream(compressedData);
            iis = new InflaterInputStream(bais);

            // Create buffer to hold each decompressed data chunk
            nextDecompressedDataChunk = new byte[decompressionReadBufferLength];

            // Decompress each chunk of compressed data
            while (true)
            {
                // Decompress next chunk of compressed data
                nextDecompressedDataChunkLength = iis.read(nextDecompressedDataChunk, 0, nextDecompressedDataChunk.length);

                // Have we reached the end of compressed data?
                if (nextDecompressedDataChunkLength == -1)
                {
                    // We have reached end of compressed data
                    break;
                }

                // Store next chunk of decompressed data
                decompressedData.write(nextDecompressedDataChunk, 0, nextDecompressedDataChunkLength);
            }
            iis.close();
            bais.close();
            decompressedData.close();
            result = decompressedData.toByteArray();
        }
        finally
        {
            if (iis != null)
            {
                iis.close();
            }

            if (bais != null)
            {
                bais.close();
            }

            if (decompressedData != null)
            {
                decompressedData.close();
            }
        }

        return result;
    }

    /**
     * Serialises an XML Node into a byte array
     * 
     * @param node The Node to serialise
     * @return byte array containing a serialised {@link Node}
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static byte[] serialise (Node node) throws TransformerConfigurationException, TransformerException, IOException
    {
        ByteArrayOutputStream serialisedNode;
        TransformerFactory tf;
        Transformer t;
        DOMSource ds;
        StreamResult sr;
        byte[] result;

        serialisedNode = null;
        tf = null;
        t = null;
        ds = null;
        sr = null;
        result = null;

        tf = TransformerFactory.newInstance();
        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        t = tf.newTransformer();

        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        ds = new DOMSource(node);

        try
        {
            serialisedNode = new ByteArrayOutputStream();

            sr = new StreamResult(serialisedNode);

            t.transform(ds, sr);

            serialisedNode.close();
            result = serialisedNode.toByteArray();
        }
        finally
        {
            if (serialisedNode != null)
            {
                serialisedNode.close();
            }
        }

        return result;
    }

    /**
     * Pads an array to a multiple of the supplied block size
     * 
     * @param plainText The array to pad
     * @param blockSize The block size to use
     * @param randomNumberGenerator RNG used to generate the padding
     * @return Padded byte array
     * @throws BadPaddingException Thrown if invalid plainText was specified
     */
    public static byte[] pad (byte[] plainText, int blockSize, SecureRandom randomNumberGenerator) throws BadPaddingException
    {
        byte[] paddedPlainText;
        int totalPaddingBytesRequired;
        byte[] paddingBytes;

        paddedPlainText = null;
        totalPaddingBytesRequired = 0;
        paddingBytes = null;

        if (plainText.length < 1)
        {
            throw new BadPaddingException("at least one byte expected");
        }

        if ((plainText.length % blockSize) != 0)
        {
            // Calculate how many bytes are required to pad plain text block
            totalPaddingBytesRequired = blockSize - (plainText.length % blockSize);
        }
        else
        {
            // Plain text is a multiple of the block size, add one full block of padding
            totalPaddingBytesRequired = blockSize;
        }

        // Allocate buffer to hold padding bytes
        paddingBytes = new byte[totalPaddingBytesRequired];

        // Fill padding bytes buffer with random numbers
        randomNumberGenerator.nextBytes(paddingBytes);

        // Indicate total padding bytes in last byte of padding block
        paddingBytes[totalPaddingBytesRequired - 1] = (byte) totalPaddingBytesRequired;

        // Allocate buffer to hold plain text plus padding
        paddedPlainText = new byte[plainText.length + paddingBytes.length];

        // Concatenate plain text and padding
        System.arraycopy(plainText, 0, paddedPlainText, 0, plainText.length);

        System.arraycopy(paddingBytes, 0, paddedPlainText, plainText.length, paddingBytes.length);

        return paddedPlainText;
    }

    /**
     * Method to create an initialisation vector
     * 
     * @param blockSize The size of the IV
     * @param randomNumberGenerator RNG to use for generation
     * @return new {@link IvParameterSpec}
     */
    public static IvParameterSpec generateInitialisationVector (int blockSize, SecureRandom randomNumberGenerator)
    {
        byte[] iv;
        IvParameterSpec ivps;

        iv = null;
        ivps = null;

        iv = new byte[blockSize];

        randomNumberGenerator.nextBytes(iv);

        ivps = new IvParameterSpec(iv);

        return ivps;
    }

    /**
     * Creates an XPath for use by the PINAuthority
     * 
     * @return {@link XPath} configured with the appropriate ViewPIN namespaces
     * @throws XPathFactoryConfigurationException Thrown if an error occurs instantiating the {@link XPath}
     */
    public static XPath createXPath () throws XPathFactoryConfigurationException
    {
        XPathFactory xpf;
        XPath xp;
        SimpleNamespaceContext nsc;

        xpf = null;
        xp = null;
        nsc = null;

        // Initialise XPath object
        xpf = XPathFactory.newInstance();
        xpf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        xp = xpf.newXPath();

        nsc = new SimpleNamespaceContext();
        nsc.addNamespace(ViewPinConstants.VIEWPIN_NAMESPACE_PREFIX, ViewPinConstants.VIEWPIN_NAMESPACE_URI);
        nsc.addNamespace(ViewPinConstants.XENC_NAMESPACE_PREFIX, ViewPinConstants.XENC_NAMESPACE_URI);
        nsc.addNamespace(ViewPinConstants.DSIG_NAMESPACE_PREFIX, ViewPinConstants.DSIG_NAMESPACE_URI);
        xp.setNamespaceContext(nsc);

        return xp;
    }

    /**
     * Opens a KeyStore ready for use
     * 
     * @param keyStoreFile The KeyStore file
     * @param keyStorePassword The associated KeyStore password
     * @return The {@link KeyStore}
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public static KeyStore initKeyStore (File keyStoreFile, char[] keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        KeyStore ks;
        FileInputStream fis;

        ks = null;
        fis = null;

        try
        {
            fis = new FileInputStream(keyStoreFile);

            ks = KeyStore.getInstance("JKS");
            ks.load(fis, keyStorePassword);
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }

        return ks;
    }

    /**
     * Parses a byte array into an XML document
     * 
     * @param document The document in byte array form
     * @param documentBuilder The {@link DocumentBuilder} to use
     * @return The XML document
     * @throws SAXException
     * @throws IOException
     */
    public static Document parseDocument (byte[] document, DocumentBuilder documentBuilder) throws SAXException, IOException
    {
        Document d;
        ByteArrayInputStream bais;

        d = null;
        bais = null;

        try
        {
            bais = new ByteArrayInputStream(document);

            d = documentBuilder.parse(new InputSource(bais));
        }
        finally
        {
            if (bais != null)
            {
                bais.close();
            }
        }

        return d;
    }

    /**
     * Validates a document against its schema
     * 
     * @param document The document to validate
     * @param schema The schema to validate against
     * @return The {@link ValidationResult} associated with the validation
     * @throws SAXException
     * @throws IOException
     */
    public static ValidationResult validateDocument (Document document, Schema schema) throws SAXException, IOException
    {
        ValidationResult schemaValidationResult;
        Validator schemaValidator;

        schemaValidationResult = null;
        schemaValidator = null;

        schemaValidator = schema.newValidator();

        schemaValidationResult = new ValidationResult();
        schemaValidator.setErrorHandler(schemaValidationResult);

        schemaValidator.validate(new DOMSource(document));

        return schemaValidationResult;
    }

    /**
     * Loads a schema ready for use. All schemas for ViewPIN use should be loaded from disk and not URL,
     * preventing external tampering from changing the operation of the ViewPIN system.
     * 
     * @param schemaFile The schema to load
     * @return The Schema object
     * @throws SAXException
     */
    public static Schema loadSchema (URL schemaFile) throws SAXException
    {
        Schema schema;
        SchemaFactory schemaFactory;
        FileSystemResourceResolver fsrr;
        ValidationResult schemaValidator;

        schema = null;
        schemaFactory = null;
        fsrr = null;
        schemaValidator = null;

        schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        fsrr = new FileSystemResourceResolver();
        fsrr.add("http://www.w3.org/TR/xmlenc-core/xenc-schema.xsd", "schemas/www/w3/org/TR/xmlenc-core/xenc-schema.xsd");
        fsrr.add("http://www.w3.org/TR/xmldsig-core/xmldsig-core-schema.xsd", "schemas/www/w3/org/TR/xmldsig-core/xmldsig-core-schema.xsd");
        fsrr.add("http://www.w3.org/2001/XMLSchema.dtd", "schemas/www/w3/org/2001/XMLSchema.dtd");
        fsrr.add("datatypes.dtd", "schemas/datatypes.dtd");
        fsrr.add("http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd", "schemas/www/w3/org/TR/xmldsig-core/xmldsig-core-schema.xsd");
        schemaFactory.setResourceResolver(fsrr);

        schemaValidator = new ValidationResult();
        schemaFactory.setErrorHandler(schemaValidator);

        // Validate schema
        schema = schemaFactory.newSchema(schemaFile);

        // Is schema valid?
        if (schemaValidator.isValid() == false)
        {
            // Schema is not valid
            throw schemaValidator.getException();
        }

        return schema;
    }

    /**
     * Compresses the supplied XML document
     * 
     * @param document The document to compress
     * @return The compressed XML document
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static byte[] compressDocument (org.w3c.dom.Document document) throws TransformerConfigurationException, TransformerException, IOException
    {
        TransformerFactory tf;
        Transformer t;
        DOMSource ds;
        Deflater deflater;
        DeflaterOutputStream dos;
        ByteArrayOutputStream compressedDocument;
        StreamResult sr;
        byte[] result;

        tf = null;
        t = null;
        ds = null;
        deflater = null;
        dos = null;
        compressedDocument = null;
        sr = null;
        result = null;

        tf = TransformerFactory.newInstance();
        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        deflater = new Deflater(Deflater.BEST_COMPRESSION);

        ds = new DOMSource(document);

        try
        {
            compressedDocument = new ByteArrayOutputStream();

            dos = new DeflaterOutputStream(compressedDocument, deflater);

            sr = new StreamResult(dos);

            t.transform(ds, sr);

            dos.close();
            compressedDocument.close();
            result = compressedDocument.toByteArray();
        }
        finally
        {
            if (dos != null)
            {
                dos.close();
            }

            if (compressedDocument != null)
            {
                compressedDocument.close();
            }
        }

        return result;
    }
}