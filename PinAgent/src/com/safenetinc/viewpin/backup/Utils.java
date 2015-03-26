// $Id: PinAgent/src/com/safenetinc/viewpin/backup/Utils.java 1.2 2008/12/09 11:04:49IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.backup;

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
  

    private Utils()
    {
        super();
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
            System.out.println("file " + filename + " not found");

            throw new FileNotFoundException("file " + filename + " not found");
        }

        System.out.println("file resource URI path = " + u.getPath());

        f = new File(u.getPath());

        if (validateFile(f, directory, needWriteAccess) == false)
        {
        	System.out.println("file " + filename + " failed validation");

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
        	System.out.println(filename + " does not exist");

            valid = false;

            return valid;
        }

        if (directory == false)
        {
            if (file.isFile() == false)
            {
            	System.out.println(filename + " not a file");

                valid = false;

                return valid;
            }
        }
        else
        {
            if (file.isDirectory() == false)
            {
            	System.out.println(filename + " not a directory");

                valid = false;

                return valid;
            }
        }

        if (file.canRead() == false)
        {
        	System.out.println("unable to read " + filename);

            valid = false;

            return valid;
        }

        if (needWriteAccess == true)
        {
            if (file.canWrite() == false)
            {
            	System.out.println("unable to write " + filename);

                valid = false;

                return valid;
            }
        }

        valid = true;

        return valid;
    }

  

    
}