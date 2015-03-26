package com.safenetinc.viewpin.simulator.authority.xml.resources;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Class to handle loading resources from the filesystem
 * 
 * @author Stuart Horler
 */
public class FileSystemResourceResolver implements LSResourceResolver
{
    private static Logger           logger            = Logger.getLogger(FileSystemResourceResolver.class);

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
    public LSInput resolveResource (@SuppressWarnings("unused") String type, @SuppressWarnings("unused") String namespaceURI, @SuppressWarnings("unused") String publicId, String systemId,
            @SuppressWarnings("unused") String baseURI)
    {
        LoadAndSaveInput lasi;
        InputStream is;

        lasi = null;
        is = null;

        lasi = new LoadAndSaveInput();

        if (getSystemIdentifiers().containsKey(systemId) == false)
        {
            getLogger().warn("system identifier " + systemId + " not found");

            return lasi;
        }

        // Replace system identifier
        String idString = this.systemIdentifiers.get(systemId);

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(idString);

        if (is == null)
        {
            getLogger().error("getting resource as stream");

            return lasi;
        }

        lasi.setByteStream(is);

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

    private Logger getLogger ()
    {
        return logger;
    }
}