// $Id: PinAgent/src/com/safenetinc/viewpin/common/xml/FileSystemResourceResolver.java 1.1 2008/09/04 10:47:38IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.safenetinc.viewpin.agent.Processor;
import com.safenetinc.viewpin.common.utils.Utils;

/**
 * Class to handle loading resources from the filesystem
 * 
 * @author Stuart Horler
 */
public class FileSystemResourceResolver implements LSResourceResolver
{
    private static Logger           logger            = Logger.getLogger(Processor.class);

    private HashMap<String, String> systemIdentifiers = null;

    /**
     * Creates a new FileSystemResourceResolver
     */
    public FileSystemResourceResolver()
    {
        super();

        setSystemIdentifiers(new HashMap<String, String>());
    }

    /**
     * Adds a system identifier
     * 
     * @param systemId The identifier
     * @param file The associated file
     */
    public void add (String systemId, String file)
    {
        getSystemIdentifiers().put(systemId, file);
    }

    /**
     * Resolves a resource
     * 
     * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public LSInput resolveResource (@SuppressWarnings("unused")
    String type, @SuppressWarnings("unused")
    String namespaceURI, @SuppressWarnings("unused")
    String publicId, String systemId, @SuppressWarnings("unused")
    String baseURI)
    {
        LoadAndSaveInput lasi;
        String sid;
        File resourceFile;
        FileInputStream fis;

        lasi = null;
        sid = null;
        resourceFile = null;
        fis = null;

        lasi = new LoadAndSaveInput();

        if (this.systemIdentifiers.containsKey(systemId) == false)
        {
            getLogger().error("system identifier " + systemId + " not found");

            return lasi;
        }

        // Replace system identifier
        sid = this.systemIdentifiers.get(systemId);

        try
        {
            // Retrieve schema resource file
            resourceFile = Utils.getFileResource(sid, false);
        }
        catch (IOException ioe)
        {
            getLogger().error("retrieving schema resource file " + ioe.getMessage());

            return lasi;
        }

        // Ensure resource file exists
        if (resourceFile.exists() == false)
        {
            getLogger().error("resource file " + resourceFile + " does not exist");

            return lasi;
        }

        // Ensure resource file can be read
        if (resourceFile.canRead() == false)
        {
            getLogger().error("resource file " + resourceFile + " cannot be read");

            return lasi;
        }

        try
        {
            fis = new FileInputStream(resourceFile);
        }
        catch (FileNotFoundException fnfe)
        {
            getLogger().error("resource file " + resourceFile + " not found");

            return lasi;
        }

        lasi.setByteStream(fis);

        return lasi;
    }

    private void setSystemIdentifiers (HashMap<String, String> systemIdentifiers)
    {
        this.systemIdentifiers = systemIdentifiers;
    }

    private HashMap<String, String> getSystemIdentifiers ()
    {
        return this.systemIdentifiers;
    }

    private static Logger getLogger ()
    {
        return logger;
    }
}