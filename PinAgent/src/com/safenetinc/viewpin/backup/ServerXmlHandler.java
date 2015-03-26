package com.safenetinc.viewpin.backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import  com.safenetinc.viewpin.backup.exception.ServerXmlException;

/**
 * Class to handle changes to Tomcat's server.xml configuration file.
 * 
 * @author Paul Hampton
 */
public class ServerXmlHandler
{
    private static final String     MAX_KEEPALIVE_REQUESTS         = "1";

    private static final String     CONNECTION_TIMEOUT             = "15000";

    private static final String     MIN_SPARE_THREADS              = "50";

    private static final String     MAX_THREADS                    = "100";

    private static final String     MAX_SPARE_THREADS              = "25";

    private static final String     ACCEPT_COUNT                   = MIN_SPARE_THREADS;

    private static final String     SECURE_CIPHERS_LIST            = "SSL_RSA_WITH_RC4_128_MD5,SSL_RSA_WITH_RC4_128_SHA,SSL_RSA_WITH_3DES_EDE_CBC_SHA,TLS_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_256_CBC_SHA";

    private static final String     CONFIG_PATH_TO_SERVICE         = "Service(0)";

    private static final String     CONFIG_PATH_TO_ENGINE          = CONFIG_PATH_TO_SERVICE + ".Engine(0)";

    private static final String     CONFIG_PATH_TO_HOST_NAMES      = CONFIG_PATH_TO_ENGINE + ".Host[@name]";

    private static final String     CONFIG_PATH_TO_CONNECTOR_PORTS = CONFIG_PATH_TO_SERVICE + ".Connector[@port]";

    private static final String     CONFIG_PATH_TO_CONNECTOR       = CONFIG_PATH_TO_SERVICE + ".Connector(";

    private static final String     CONFIG_PATH_TO_HOST            = CONFIG_PATH_TO_ENGINE + ".Host(";

    private static final String     SERVER_XML_LOCATION            = "/usr/tomcat/conf/server.xml";

    private static final String     SERVER_XML_BACKUP_LOCATION     = "/usr/tomcat/conf/server.xml.orig";

    private static final String     LOCALHOST                      = "localhost";

    private static XMLConfiguration xmlConfig                      = null;

    static
    {
        // first make sure we backup the server.xml & remove the read only attribute
        try
        {
            removeServerXmlReadOnlyAttribute();
        }
        catch (ServerXmlException e)
        {
            System.err.println("Could not remove read only attribute from server.xml - this is a fatal error");
            e.printStackTrace();
        }

        // now load the server.xml file
        try
        {
            xmlConfig = new XMLConfiguration(SERVER_XML_LOCATION);
        }
        catch (ConfigurationException ce)
        {
            System.err.println("Could not load server.xml file - this is a fatal error");
            ce.printStackTrace();
        }
    }

    /**
     * Private constructor as all class methods are static
     */
    private ServerXmlHandler()
    {
        // Private, nothing to do here
    }

    /**
     * Removes the read only attribute from server.xml. This attribute is set by default with a fresh Luna SP
     * jail installation. Java doesn't provide a way of changing the read only attribute (only setting it) so
     * this method renames the file before copying it back to its original name. Removing the read only
     * attribute is a side effect of this process.
     * 
     * @throws ServerXmlException If modifying the file was not possible.
     */
    public static void removeServerXmlReadOnlyAttribute () throws ServerXmlException
    {
        // First lets rename the original server.xml
        File serverxml = new File(SERVER_XML_LOCATION);
        File serverxmlBackup = new File(SERVER_XML_BACKUP_LOCATION);
        boolean success = serverxml.renameTo(serverxmlBackup);
        if (!success)
        {
            System.err.println("Unable to rename the server.xml file");
        }

        /*
         * Now copy the contents of the backup back to the original filename thus removing the read only
         * attribute
         */
        File inputFile = new File(SERVER_XML_BACKUP_LOCATION);
        File outputFile = new File(SERVER_XML_LOCATION);

        try
        {
            FileReader in = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            int c;

            while ((c = in.read()) != -1)
                out.write(c);

            in.close();
            out.close();
        }
        catch (FileNotFoundException fnfe)
        {
            throw new ServerXmlException(fnfe.toString());
        }
        catch (IOException ioe)
        {
            throw new ServerXmlException(ioe.toString());
        }

    }

    /**
     * Adds a virtual host entry to server.xml. This takes the form of a <code><host></code> element. A non
     * standard attribute named correspondingConnectorPort is added by this method, this contains the port
     * number of the associated connector, due to mandating SSL applications such as ViewPIN effectively have
     * a 1-1 mapping between a connector and a virtual host. The correspondingConnectorPort attribute provides
     * a way of tracking this.
     * 
     * @param name The name of the virtual host
     * @param port The port that users will access this virtual host from
     * @throws ServerXmlException
     */
    public static void addVirtualHost (String name, int port) throws ServerXmlException
    {
        // First make sure the user isn't trying to add localhost
        if (name.equalsIgnoreCase(LOCALHOST))
            throw new ServerXmlException("Name cannot be localhost");

        // Ensure the hostname is lowercase
        String lowerCaseName = name.toLowerCase();

        // Count the number of hosts
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_HOST_NAMES);
        int numberOfHosts = prop.size();

        // Add a new host
        xmlConfig.setProperty(CONFIG_PATH_TO_HOST + numberOfHosts + ")[@name]", lowerCaseName);
        // Configure the host
        xmlConfig.setProperty(CONFIG_PATH_TO_HOST + numberOfHosts + ")[@appBase]", "webapps/" + lowerCaseName);
        // Not a tomcat configuration element; used for tracking by this application
        xmlConfig.setProperty(CONFIG_PATH_TO_HOST + numberOfHosts + ")[@correspondingConnectorPort]", "" + port);
        xmlConfig.setProperty(CONFIG_PATH_TO_HOST + numberOfHosts + ").Context[@path]", "/");
        xmlConfig.setProperty(CONFIG_PATH_TO_HOST + numberOfHosts + ").Context[@docbase]", ".");

        try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }

    }

    /**
     * Deletes a virtual host configuration by removing the <code><host></code> element
     * 
     * @param name The name of the virtual host
     * @throws ServerXmlException Thrown if changing the server.xml fails for any reason
     */
    public static void deleteVirtualHost (String name) throws ServerXmlException
    {

        // get the hosts
        Collection<?> prop= xmlConfig.getList(CONFIG_PATH_TO_HOST_NAMES);

        for (int i = 0; i < prop.size(); i++)
        {
            String hostname = xmlConfig.getString(CONFIG_PATH_TO_HOST + i + ")[@name]");
			
			
            if (hostname.equals(name))
            {
                System.out.println("Deleting " + hostname);
				 
                xmlConfig.setProperty(CONFIG_PATH_TO_HOST + i + ")[@name]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_HOST + i + ")[@appBase]", "");
                // Not a tomcat configuration element; used for tracking by this application
                xmlConfig.setProperty(CONFIG_PATH_TO_HOST + i + ")[@correspondingConnectorPort]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_HOST + i + ").Context[@path]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_HOST + i + ").Context[@docbase]", "");
                xmlConfig.clearTree(CONFIG_PATH_TO_HOST + i + ").Context(0)");
                xmlConfig.clearTree(CONFIG_PATH_TO_HOST + i + ")");
            }
        }

        try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }
    }

    /**
     * Uses the correspondingConnectorPort attribute to determine which port a virtual host is being accessed
     * on. Used for mapping hosts to connectors.
     * 
     * @param name The name of the virtual host
     * @return int representing the TCP port of the associated connector
     * @throws ServerXmlException Thrown if reading or parsing the server.xml was not possible
     */
    public static int getPortForVirtualHost (String name) throws ServerXmlException
    {
        // First make sure the user isn't trying to access localhost
        if (name.equalsIgnoreCase(LOCALHOST))
            throw new ServerXmlException("Name cannot be localhost");

        // get the hosts
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_HOST_NAMES);

        for (int i = 0; i < prop.size(); i++)
        {
            String hostname = xmlConfig.getString(CONFIG_PATH_TO_HOST + i + ")[@name]");
            if (hostname.equals(name))
            {
                return xmlConfig.getInt(CONFIG_PATH_TO_HOST + i + ")[@correspondingConnectorPort]");
            }
        }
        return -1;
    }

    /**
     * Creates a {@link java.util.Vector} of {@link java.lang.String} objects representing the names of the
     * virtual hosts configured for this Tomcat install.
     * 
     * @return a {@link java.util.Vector} of {@link java.lang.String} objects
     * @throws ServerXmlException Thrown if reading and parsing the server.xml file was not possible
     */
    public static Vector<String> getVirtualHostsList () throws ServerXmlException
    {

        // Count the number of hosts
        Vector<String> hosts = new Vector<String>();
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_HOST_NAMES);
        Iterator<?> iterator = prop.iterator();
        while (iterator.hasNext())
        {
            String name = (String) iterator.next();
            if (!name.equals(LOCALHOST))
                hosts.add(name);
        }

        return hosts;
    }

    /**
     * Determines whether a given name matches (case insensitive) that of a configured virtual host.
     * 
     * @param hostname The name of the virtual host to look for.
     * @return boolean denoting whether a host with the given name is configured.
     * @throws ServerXmlException Thrown if reading and parsing the server.xml file failed
     */
    public static boolean virtualHostExists (String hostname) throws ServerXmlException
    {
        Vector<String> hosts = getVirtualHostsList();

        for (int i = 0; i < hosts.size(); i++)
        {
            if (hostname.equalsIgnoreCase(hosts.get(i)))
                return true;
        }
        return false;
    }

    /**
     * Adds a connector to the server.xml
     * 
     * @param port The TCP port to use for the connector
     * @param keyAlias The SSL certficate Subject Key Identifier for the SSL certificate to be used with this
     *        connector
     * @throws ServerXmlException Thrown if writing to the server.xml file was not possible for some reason
     * @deprecated Use the Luna SP command webs server port addSecure instead
     */
    @Deprecated
    public static void addConnector (int port, String keyAlias) throws ServerXmlException
    {

        // Count the number of connectors
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        int numberOfConnectors = prop.size();

        // Add our new connector
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@port]", "" + port);
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@acceptCount]", ACCEPT_COUNT);
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@clientAuth]", "false");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@debug]", "0");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@disableUploadTimeout]", "true");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@enableLookups]", "false");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@keyAlias]", keyAlias);
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@keystoreType]", "luna");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@maxSpareThreads]", MAX_SPARE_THREADS);
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@maxThreads]", MAX_THREADS);
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@minSpareThreads]", MIN_SPARE_THREADS);
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@scheme]", "https");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@secure]", "true");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@splabel]", "https");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@sslProtocol]", "TLS");
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@connectionTimeout]", CONNECTION_TIMEOUT);
        xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + numberOfConnectors + ")[@maxKeepAliveRequests]", "1");

        try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }

    }

    /**
     * Modifies a connector to specify a different SSL certificate. Certificates are located by their
     * associated Subject Key Identifiers
     * 
     * @param port The TCP port for the connector to be edited
     * @param certificateAlias The Subject Key Identifier for the SSL certificate the connector will use
     * @throws ServerXmlException Thrown if reading, parsing or writing to the server.xml failed for some
     *         reason
     */
    public static void changeCertificateForConnector (int port, String certificateAlias) throws ServerXmlException
    {

        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        boolean changed = false;
        for (int i = 0; i < prop.size(); i++)
        {
            int connectorPort = xmlConfig.getInt(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]");
            if (connectorPort == port)
            {
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@keyAlias]", certificateAlias);
                changed = true;
            }
        }

        if (!changed)
            throw new ServerXmlException("No connector on that port");
        try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }

    }

    /**
     * Deletes all connectors on a given port. Valid server.xml files will have one connector per port,
     * consequently this method only deletes the first matching connector it finds
     * 
     * @param port The port of the connector to remove
     * @throws ServerXmlException Thrown if reading, parsing or writing the server.xml file fails.
     */
    public static void deleteConnector (int port) throws ServerXmlException
    {

        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        for (int i = 0; i <prop.size(); i++)
        {
            int connectorPort = xmlConfig.getInt(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]");

            if (connectorPort == port)
            {
                // Delete the connector
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@acceptCount]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@clientAuth]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@debug]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@disableUploadTimeout]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@enableLookups]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@keyAlias]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@keystoreType]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@maxSpareThreads]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@maxThreads]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@minSpareThreads]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@scheme]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@secure]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@splabel]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@sslProtocol]", "");
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@redirectPort]", "");
                xmlConfig.clearTree(CONFIG_PATH_TO_CONNECTOR + i + ")");
                break;
            }
        }

        try
        {
            xmlConfig.save();
           // System.out.println("Connector on port " + port + " deleted");
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }
    }

    /**
     * Looks for a connector on a given TCP port.
     * 
     * @param port The TCP port to look for a connector on
     * @return boolean denoting whether a connector exists for the specified port.
     * @throws ServerXmlException Thrown if reading or parsing the server.xml file failed
     */
    public static boolean checkForConnectorOnPort (int port) throws ServerXmlException
    {
        // Count the number of connectors
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        // Check that there isn't already a connector on our port
        for (int i = 0; i < prop.size(); i++)
        {
            int connectorPort = xmlConfig.getInt(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]");

            if (connectorPort == port)
                return true;
        }
        return false;
    }

    /**
     * Method to change the cipher suites for all connectors within a server.xml Required in order to use
     * JDK1.5 with Tomcat 5.5.16 and Firefox but also required in order to prevent tomcat supporting insecure
     * ciphers
     * 
     * @throws ServerXmlException Thrown if reading, parsing or writing the server.xml file
     */
    public static void setSecureCiphersForConnectors () throws ServerXmlException
    {
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        for (int i = 0; i < prop.size(); i++)
        {
            if (xmlConfig.getProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@sslProtocol]") != null)
            {
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@ciphers]", SECURE_CIPHERS_LIST);
            }
        }

        try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }
    }

    /**
     * Method to tune the parameters for all connectors. Sets values that the LunaSP is able to cope with when
     * under heavy load.
     * 
     * @param serverName The server name to return to connecting clients. Replaces the default apache/tomcat
     *        value
     * 
     * @throws ServerXmlException Thrown if saving the configuration changes was not possible
     */
    public static void setConnectionParameters (String serverName) throws ServerXmlException
    {
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        for (int i = 0; i < prop.size(); i++)
        {
            xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@acceptCount]", ACCEPT_COUNT);
            xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@maxSpareThreads]", MAX_SPARE_THREADS);
            xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@maxThreads]", MAX_THREADS);
            xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@minSpareThreads]", MIN_SPARE_THREADS);
            xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@connectionTimeout]", CONNECTION_TIMEOUT);
            xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@maxKeepAliveRequests]", MAX_KEEPALIVE_REQUESTS);
            xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@server]", serverName);
        }

        try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }
    }

}
