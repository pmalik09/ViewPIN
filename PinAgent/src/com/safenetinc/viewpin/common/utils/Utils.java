// $Id: PinAgent/src/com/safenetinc/viewpin/common/utils/Utils.java 1.2 2011/12/20 15:39:39IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.InflaterInputStream;

import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.safenetinc.viewpin.agent.ViewPinConstants;
import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidSubjectKeyIdentifierException;

/**
 * Class to handle utility operations for the PINAgent
 * 
 * @author Stuart Horler
 * 
 */
public class Utils
{
    private static final Logger logger = Logger.getLogger(Utils.class);

    private Utils()
    {
        super();
    }

    /**
     * Method to determine whether a supplied int maps to an ASCII character
     * 
     * @param character int to check
     * @return boolean denoting whether supplied int is an ASCII value
     */
    public static boolean isAsciiDigit (final int character)
    {
        boolean rc;

        rc = false;

        if (character >= '0' && character <= '9')
        {
            rc = true;
        }

        return rc;
    }

    /**
     * Method to handle formatting a date to form yyyy-MM-dd'T'HH:mm:ss'Z'
     * 
     * @param dateToBeFormatted The date object to format
     * @return The date object in formatted string form
     */
    public static String formatDate (Date dateToBeFormatted)
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
     * Method to generate an initialisation vector
     * 
     * @param blockSize The blocksize of the initialisation vector
     * @return The new IvParameterSpec containing the initialisation vector
     * @throws NoSuchAlgorithmException Thrown if an error occurs with entropy generation
     */
    public static IvParameterSpec generateInitialisationVector (int blockSize) throws NoSuchAlgorithmException
    {
        byte[] iv;
        IvParameterSpec ivps;

        iv = null;
        ivps = null;

        iv = EntropyPool.getBytes(blockSize);
    	
		

		ivps = new IvParameterSpec(iv);
        

        return ivps;
    }

    /**
     * Method to decompress a byte array
     * 
     * @param compressedData byte array containing the compressed XML data
     * @param decompressionReadBufferLength The length of the buffer to use during decompression. See
     *        {@link ViewPinConstants} for a sensible default value
     * @return The data decompressed into a new byte array
     * @throws IOException Thrown if an error occurs during decompression
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

                // Store next chunk of compressed data
                decompressedData.write(nextDecompressedDataChunk, 0, nextDecompressedDataChunkLength);
            }
            iis.close();
            decompressedData.close();
            result = decompressedData.toByteArray();
        }
        finally
        {
            if (iis != null)
            {
                iis.close();
            }

            if (decompressedData != null)
            {
                decompressedData.close();
            }
        }

        return result;
    }

    /**
     * Method to get a directory based on supplied name. Resources are loaded from the class loader
     * 
     * @param directoryName The name of the directory to return
     * @param needWriteAccess Denotes whether write access to the directory is required
     * @return The directory
     * @throws IOException Thrown if an I/O error occurs when reading the directory
     */
    public static File getDirectoryResource (final String directoryName, final boolean needWriteAccess) throws IOException
    {
        File f;

        f = null;

        f = getFileResource(directoryName, true, needWriteAccess);

        return f;
    }

    /**
     * Method to get a file based on supplied name. Resources are loaded from the class loader
     * 
     * @param filename The name of the file to return
     * @param needWriteAccess Denotes whether write access to the file is required
     * @return The file
     * @throws IOException hrown if an I/O error occurs when reading the file
     */
    public static File getFileResource (String filename, boolean needWriteAccess) throws IOException
    {
        File f;

        f = null;

        f = getFileResource(filename, false, needWriteAccess);

        return f;
    }

    static File getFileResource (String filename, boolean directory, boolean needWriteAccess) throws IOException
    {
        File f;
        URL u;

        f = null;
        u = null;

        u = Thread.currentThread().getContextClassLoader().getResource(filename);

        if (u == null)
        {
            getLogger().error("file " + filename + " not found");

            throw new FileNotFoundException("file " + filename + " not found");
        }

        getLogger().debug("file resource URI path = " + u.getPath());

        f = new File(u.getPath());

        if (validateFile(f, directory, needWriteAccess) == false)
        {
            getLogger().error("file " + filename + " failed validation");

            throw new IOException();
        }

        return f;
    }

    /**
     * Method to validate a file. Checks that the file is present and that write access, if required is
     * available.
     * 
     * @param file The file to validate
     * @param needWriteAccess Denotes whether write access is required
     * @return boolean indicating whether validation was successful
     */
    public static boolean validateFile (File file, boolean needWriteAccess)
    {
        boolean valid;

        valid = false;

        valid = validateFile(file, false, needWriteAccess);

        return valid;
    }

    /**
     * Method to validate a directory. Checks that the directory is present and that write access, if required
     * is available.
     * 
     * @param directory The directory to validate
     * @param needWriteAccess Denotes whether write access is required
     * @return boolean indicating whether validation was successful
     */
    public static boolean validateDirectory (File directory, boolean needWriteAccess)
    {
        boolean valid;

        valid = false;

        valid = validateFile(directory, true, needWriteAccess);

        return valid;
    }

    private static boolean validateFile (File file, boolean directory, boolean needWriteAccess)
    {
        boolean valid;
        String filename;

        valid = false;
        filename = null;

        filename = file.getAbsolutePath();

        if (file.exists() == false)
        {
            getLogger().error(filename + " does not exist");

            valid = false;

            return valid;
        }

        if (directory == false)
        {
            if (file.isFile() == false)
            {
                getLogger().error(filename + " not a file");

                valid = false;

                return valid;
            }
        }
        else
        {
            if (file.isDirectory() == false)
            {
                getLogger().error(filename + " not a directory");

                valid = false;

                return valid;
            }
        }

        if (file.canRead() == false)
        {
            getLogger().error("unable to read " + filename);

            valid = false;

            return valid;
        }

        if (needWriteAccess == true)
        {
            if (file.canWrite() == false)
            {
                getLogger().error("unable to write " + filename);

                valid = false;

                return valid;
            }
        }

        valid = true;

        return valid;
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

        formatter = new SimpleDateFormat(ViewPinConstants.DATE_TIME_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        parsedDate = formatter.parse(dateTime);

        return parsedDate;
    }

    /**
     * Method to apply a {@link SimpleDateFormat} object to the supplied date. Formats dates in accordance
     * with the formatting pattern specified in {@link ViewPinConstants}
     * 
     * @param dateTime The date/time to format
     * @return String containing the formatted date/time
     */
    public static final String formatDateTime (Date dateTime)
    {
        String formattedDateTime;
        SimpleDateFormat formatter;

        formattedDateTime = null;
        formatter = null;

        formatter = new SimpleDateFormat(ViewPinConstants.DATE_TIME_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        formattedDateTime = formatter.format(dateTime);

        return formattedDateTime;
    }

    /**
     * Method to return the SKI containing within a KeyInfoElement
     * 
     * @param keyInfoElement The KeyInfoElement to extract the SKI from
     * @return A {@link SubjectKeyIdentifier} object containing the SKI from the KeyInfoElement
     * @throws InvalidSubjectKeyIdentifierException Thrown if the specified SKI is invalid
     */
    public static SubjectKeyIdentifier getX509Ski (final Element keyInfoElement) throws InvalidSubjectKeyIdentifierException
    {
        SubjectKeyIdentifier subjectKeyIdentifier;
        Element x509DataElement;
        Element x509SkiElement;
        String encodedSubjectKeyIdentifer;
        byte[] decodedSubjectKeyIdentifer;

        subjectKeyIdentifier = null;
        x509DataElement = null;
        x509SkiElement = null;
        encodedSubjectKeyIdentifer = null;
        decodedSubjectKeyIdentifer = null;

        // Get X509Data element
        x509DataElement = XMLUtils.getFirstChildElementMatchingName(keyInfoElement, ViewPinConstants.DSIG_NAMESPACE_URI, "X509Data");

        // Did we get X509Data element OK?
        if (x509DataElement == null)
        {
            // Failed to get X509Data element
            getLogger().error("retrieving X509Data element");

            return null;
        }

        // Get X509SKI element
        x509SkiElement = XMLUtils.getFirstChildElementMatchingName(x509DataElement, ViewPinConstants.DSIG_NAMESPACE_URI, "X509SKI");

        // Did get get X509SKI element OK?
        if (x509SkiElement == null)
        {
            // Failed to get X509SKI element
            getLogger().error("retrieving X509SKI element");

            return null;
        }

        // Get encoded subject key identifier
        encodedSubjectKeyIdentifer = x509SkiElement.getTextContent();

        // Is subject key identifier correctly encoded?
        if (Base64.isArrayByteBase64(encodedSubjectKeyIdentifer.getBytes()) == false)
        {
            // Subject key identifier is not correctly encoded
            getLogger().error("incorrectly encoded subject key identifier");

            return null;
        }

        // Decode subject key identifier
        decodedSubjectKeyIdentifer = Base64.decodeBase64(encodedSubjectKeyIdentifer.getBytes());

        // Instantiate subject key identifier
        subjectKeyIdentifier = new SubjectKeyIdentifier(decodedSubjectKeyIdentifer);

        return subjectKeyIdentifier;
    }

    /**
     * Method to generate a random string of specified length
     * 
     * @param length The length of the string
     * @return The random string
     * @throws NoSuchAlgorithmException Thrown if an error occurs with the underlying entropy generation
     */
    public static String generateRandomString (final int length) throws NoSuchAlgorithmException
    {
        byte[] randomString;

        randomString = null;

        randomString = EntropyPool.getBytes(length);

        return new String(Hex.encodeHex(randomString));
    }

    private static Logger getLogger ()
    {
        return Utils.logger;
    }
}