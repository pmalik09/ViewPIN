/*
 * Created on Jul 17, 2007
 * 
 * 
 */
package com.safenetinc.luna.virtualhosts;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.safenetinc.Common;
import com.safenetinc.luna.fileutils.DirectoryHandler;
import com.safenetinc.luna.fileutils.FileCopy;
import com.safenetinc.luna.fileutils.FileDelete;
import com.safenetinc.luna.virtualhosts.exception.ServerXmlException;




/**
 * LunaSP command line application used to setup a LunaSP for virtual hosting.
 * This application does the following things to tomcat's server.xml:<br>
 * - Removes the default connectors on ports 8080 and 8443<br>
 * - Sets a predetermined 'secure' set of SSL ciphers<br>
 * - Adds the host settings for the virtual host<br>
 * - Creates the directories necessary for virtual hosting<br>
 * See <a href="http://tomcat.apache.org/tomcat-5.5-doc/virtual-hosting-howto.html">The Tomcat Documentation</a> for more information
 * @author Paul Hampton
 */
public class AddVirtualHost
{

	private static final String PINAGENT_WAR_FILE						 = "/PinAgent.war";
 
    private static final int    LUNA_DEFAULT_HTTPS_PORT         		 = 8443;
 
    private static final int    LUNA_DEFAULT_HTTP_PORT         			 = 8080;

    private static final int    HIGH_PORT_NUMBER_RANGE          		 = 65536;

    private static final int    LOW_PORT_NUMBER_RANGE           		 = 1024;

    private static final String APPLICATION_NAME                		 = "VirtualHostsConfiguration";

    private static final String CERTIFICATESKI_COMMAND_LINE_ARG 		 = "certificateski";

    private static final String HOSTNAME_COMMAND_LINE_ARG       		 = "hostname";

    private static final String PORT_COMMAND_LINE_ARG           		 = "port";

    private static final String SERVER_NAME                     		 = "ViewPIN";

    private static final String SOURCE_FILE_PATH                 		 = "/usr-files/";

    private static final String AGENT_CONFIGURATION_FILE_NAME        	 = "agentconfiguration.xml";

    private static final String CONFIGURATION_DESTINATION_PATH_START 	 = "/usr/tomcat/webapps/";

    private static final String CONFIGURATION_DESTINATION_DIR        	 = "/configuration/";
    
    private FileCopy fileCopy				                     		 = new FileCopy();
    
    private static final String AGENT_LOG_CONFIGURATION_FILE_NAME        = "pinagent-log4j.xml";
	
	private static final String AGENT_LOG_DTD_CONFIGURATION_FILE_NAME    = "log4j.dtd";
    		
    private static final String AGENT_LOG_FILE_NAME        = "agent.log";
	
	private static final String AGENT_VERSION_FILE_NAME = "PinAgentVersion.txt";
	
	
	
	private static final String BACKUP_AGENT_LOG_CONFIGURATION_FILE_NAME = "pinagent-log4j.xml.bak";
    
    /**
     * The agentConfigurationXMLconfig object. This is used to retrieve configuration entries from the
     * agentconfiguration.xml file.
     */
    private static XMLConfiguration   config                        		   = new XMLConfiguration();


    /**
     * Default constructor
     */
    public AddVirtualHost()
    {
        super();
    }

    /**
     * Creates a virtual host on a LunaSP. This method expects several things to be in place before it is run:<br>
     * <br>
     * -The LunaSP command <code>webs server port addSecure</code> to have been run already in order to 
     * create a tomcat connector on the port the user wishes to access their new virtual host from.<br>
     * <br>
     * -A SSL certificate to be in place within the HSM of the Luna. The Subject Key Identifier (SKI) of this
     * certificate is passed to this method which will set this SKI as the SSL certificate for the port indicated
     * by the port parameter.
     * 
     * @param hostname The hostname of the virtual host to create for example viewpin.safenet-inc.com
     * @param port The port to use for this virtual host
     * @param keyAlias The Subject Key Identifier of the SSL certificate to be used on the port
     */
    public void createVirtualHost (String hostname, int port, String keyAlias)
    {

		
    	try
		{
			fileCopy.copy(PINAGENT_WAR_FILE,"/usr-files/");			
		}
		catch (IOException e)
		{
			  System.err.println("Error - Could not Find war file");
			  return;
		}
		
		try
		{
			fileCopy.copy("/"+AGENT_CONFIGURATION_FILE_NAME,"/usr-files/");			
		}
		catch (IOException e)
		{
			  System.err.println("Error - Could not Find agent configuration file");
			  return;
		}
	
		try
		{
			fileCopy.copy("/"+AGENT_LOG_CONFIGURATION_FILE_NAME,"/usr-files/");			
		}
		catch (IOException e)
		{
			  System.err.println("Error - Could not Find log4j configuration file");
			  return;
		}
		
		try
		{
			fileCopy.copy("/"+AGENT_LOG_DTD_CONFIGURATION_FILE_NAME,"/usr-files/");			
		}
		catch (IOException e)
		{
			  System.err.println("Error - Could not Find DTD file");
			  return;
		}
		
		try
		{
			fileCopy.copy("/"+AGENT_VERSION_FILE_NAME,"/usr-files/");			
		}
		catch (IOException e)
		{
			  System.err.println("Error - Could not Find version file");
			  return;
		}
    	
        // Now ensure the hostname is in lowercase
        String hostnameLowerCase = hostname.toLowerCase();

        // Check that the port is configured
        try
        {
            if (!ServerXmlHandler.checkForConnectorOnPort(port))
            {
                System.out.println("No connector configured on this port, please choose a port with a connector configured (use command \"websvc server port addSecure\" to add a port)");
                return;
            }
            // Now check that the virtual host name doesn't already exist
            if (ServerXmlHandler.virtualHostExists(hostnameLowerCase))
            {
                System.out.println("A virtual host with that name already exists. Please choose another hostname");
                return;
            }
           
        }
        catch (ServerXmlException sxe)
        {
            System.err.println("Could not modify the connector " + sxe.getMessage());
            return;
        }

        // Now add the virtual host directory configuration
        try
        {
            DirectoryHandler.createVirtualHostRootDirectories(hostnameLowerCase);
        }
        catch (Exception e)
        {
            System.err.println("Error - Could not create directories");
			e.printStackTrace();
            return;
        }
        try
        {

            // Check to see if ports 8080 and 8443 are still active, if so, disable
           /* if (ServerXmlHandler.checkForConnectorOnPort(LUNA_DEFAULT_HTTP_PORT))
            {
                ServerXmlHandler.deleteConnector(LUNA_DEFAULT_HTTP_PORT);
            }
            if (ServerXmlHandler.checkForConnectorOnPort(LUNA_DEFAULT_HTTPS_PORT))
            {
                ServerXmlHandler.deleteConnector(LUNA_DEFAULT_HTTPS_PORT);
            }
			*/
            //Create the private key stub file for our SSL certificate
            ExtractCertificatesForSSL certificateHandler = new ExtractCertificatesForSSL();
            String privateKeyFile = certificateHandler.createPrivateKeyStubFile(keyAlias);
            
            //Create the certificate file for our SSL certificate
            String certificateFile = certificateHandler.createCertificateFile(keyAlias);
            
            // Modify the connector
            ServerXmlHandler.changeCertificateForConnector(port, certificateFile, privateKeyFile);
            
            // Now add our new host
            ServerXmlHandler.addVirtualHost(hostnameLowerCase, port);

            // Configure the SSL ciphers
            ServerXmlHandler.setSecureCiphersForConnectors();

            // Tune the connector
            ServerXmlHandler.setConnectionParameters(SERVER_NAME);
        }
        catch (ServerXmlException e)
        {
            System.err.println("Error - Could not add entries to server.xml " + e.getMessage());
            return;
        }
        catch(CertificateException e)
        {
            System.err.println("Error - Could not locate private key associated with certificate " + e.getMessage());
            return;
        }
        catch(NoSuchAlgorithmException e)
        {
            System.err.println("Error - Could not add entries to server.xml " + e.getMessage());
            return;
        }
        catch(KeyStoreException e)
        {
            System.err.println("Error - Could not add entries to server.xml " + e.getMessage());
            return;
        }
        catch(IOException e)
        {
            System.err.println("Error - Could not add entries to server.xml " + e.getMessage());
            return;
        }
	
		//deploy to Virtual Host

		  // Lowercase the hostname
        String lowerCaseHostname = hostname.toLowerCase();

        // Now copy the file
        File inputFile = new File(SOURCE_FILE_PATH + "/" + PINAGENT_WAR_FILE );
        File outputFile = new File(CONFIGURATION_DESTINATION_PATH_START + "/" + lowerCaseHostname + "/" + PINAGENT_WAR_FILE);
		try
		{
			   FileCopy.copyFile(inputFile, outputFile);
		}
		catch (IOException e)
		{
			  System.err.println("Error - Could not Find war file");
			  return;
		}
     
        // Delete the original file
        FileDelete.deleteFile(inputFile);
        
        setConfiguration (lowerCaseHostname);
           
        setLogConfiguration(lowerCaseHostname);
		
		setLogFile(lowerCaseHostname);

		setVersion(lowerCaseHostname);

        DOMParser parser = new DOMParser();
		Element valueEle = null;
		Element paramEle = null;
		Element SKIEle = null;
		Document dom = null;
		Document doc = null;
		 
		try
			{
				
				 parser.parse(CONFIGURATION_DESTINATION_PATH_START + lowerCaseHostname + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME);
				 
			}
			catch (SAXException c)
			{
				c.printStackTrace();
			}
			catch (IOException c)
			{
				c.printStackTrace();
			}
			
			doc = parser.getDocument();
			NodeList servicelist;
			if(doc != null)
			{
				servicelist = doc.getElementsByTagName("PinAgent");
			}
			else
			{
				return;
			}			
			for(int i=0;i<servicelist.getLength();i++)
			{
			   	
			   valueEle = (Element)servicelist.item(0);
			
			 
			
			
		
			}
			
			
			NodeList paramList = valueEle.getElementsByTagName("Name");
								
			if(paramList != null && paramList.getLength() > 0)
			{



			   for(int j = 0; j< paramList.getLength(); j++)
			   {
				paramEle = (Element)paramList.item(0);
			//paramName = paramEle.getAttribute("name");

				break;

			   }
			   paramEle.setTextContent(lowerCaseHostname);
			   				
			}
			
			paramList = null;
			paramList = valueEle.getElementsByTagName("Port");
			
			if(paramList != null && paramList.getLength() > 0)
			{



			   for(int j = 0; j< paramList.getLength(); j++)
			   {
				paramEle = (Element)paramList.item(0);
			//paramName = paramEle.getAttribute("name");

				break;

			   }
			   paramEle.setTextContent(String.valueOf(port));
			   				
			}
			
			paramList = null;
			paramList = valueEle.getElementsByTagName("SSLCertificate");
			
			if(paramList != null && paramList.getLength() > 0)
			{



			   for(int j = 0; j< paramList.getLength(); j++)
			   {
				paramEle = (Element)paramList.item(0);
				break;

			   }
			   
			   				
			}
			NodeList SKIList = paramEle.getElementsByTagName("SubjectKeyIdentifier");
			if(SKIList != null && SKIList.getLength() > 0)
			{

			 

			   for(int j = 0; j< SKIList.getLength(); j++)
			   {
				SKIEle = (Element)SKIList.item(0);
				break;

			   }
			   SKIEle.setTextContent(keyAlias);
			   				
			}
			TransformerFactory tFactory = TransformerFactory.newInstance();
			try
			{
					Transformer transformer = tFactory.newTransformer();	
					doc.normalize();

					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(CONFIGURATION_DESTINATION_PATH_START + lowerCaseHostname + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME);
					transformer.transform(source, result);
			}
			catch (TransformerConfigurationException e)
			{
				System.err.println("Could not write to the configuration");
				  return;
			}
			catch (TransformerException e)
			{
				System.err.println("Could not write to the configuration");
				  return;
			}
			
            System.err.println("Virtual host " + lowerCaseHostname + " added ");
			  return;
    }

    /**
     * Main method for this application. Responsible for informing the user of the correct
     * command-line arguments to use when invoking the application and in turn validating
     * those parameters before calling the {@link #createVirtualHost(String, int, String)} method.
     * @param args Standard command line arguments
     */
    public static void main (final String args[])
    
    {
        // parse the command line arguments
        final Options options = new Options();

        OptionBuilder.withArgName(HOSTNAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The name of the virtual host to add, for example xyz.com");
        final Option hostname = OptionBuilder.create(HOSTNAME_COMMAND_LINE_ARG);

        OptionBuilder.withArgName(PORT_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The port number to run the new virtual host on (This is an existing port created with the command \"websvc server port addSecure\")");
        final Option port = OptionBuilder.create(PORT_COMMAND_LINE_ARG);

        OptionBuilder.withArgName(CERTIFICATESKI_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The identifier of the SSL certificate to use for this server");
        final Option certificateski = OptionBuilder.create(CERTIFICATESKI_COMMAND_LINE_ARG);

        options.addOption(hostname);
        options.addOption(port);
        options.addOption(certificateski);
		
		final CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
        try
        {
            cmd = parser.parse(options, args);
        }
        catch (final ParseException e)
        {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }

        // Turn the port number into an int
        final String portValueString = cmd.getOptionValue(PORT_COMMAND_LINE_ARG);
        int portValue = 0;
        try
        {
            portValue = Integer.parseInt(portValueString);
        }
        catch (final NumberFormatException nfe)
        {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }
        if (portValue <= LOW_PORT_NUMBER_RANGE || portValue > HIGH_PORT_NUMBER_RANGE)
        {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }
		
		String host_name = cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG);
		if(isValidHostname(host_name)==false)
		{
			return ;
		}
		
		
		boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
        final AddVirtualHost hostAdder = new AddVirtualHost();
		
		try
		{
			hostAdder.createVirtualHost(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG), portValue, cmd.getOptionValue(CERTIFICATESKI_COMMAND_LINE_ARG));
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("Illegal arguemnts passed");
			System.out.println("AddVirtualHost failed");
			return;
		}
		catch(NegativeArraySizeException e)
		{
			System.out.println("Arguments invalid");
			System.out.println("AddVirtualHost failed");
			return;
		}
		catch(ClassCastException e)
		{
			System.out.println("Could not execute AddVirtualHost");
			System.out.println("AddVirtualHost failed");
			return;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			System.out.println("Passed values null not accepted");
			System.out.println("AddVirtualHost failed");
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Check the arguments passed");
			System.out.println("AddVirtualHost failed");
			return;
		}
      
        
      //  File configurationFile = new File(CONFIGURATION_DESTINATION_PATH_START + cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG) + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME);
        
      //  System.out.println("configuration file = " + configurationFile);
	  if(isLoggedIn==false)
	  {
		Common.partition_logout();
	  }
		
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

    

        f = new File(u.getPath());

        if (validateFile(f, directory, needWriteAccess) == false)
        {
        	System.err.println("file " + filename + " failed validation");

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
    private static boolean validateFile (File file, boolean directory, boolean needWriteAccess)
    {
        boolean valid;
		//commenting to resolve klocwork issue id:5
        
        valid = false;
        
		//commenting to resolve klocwork issue id:5
      //  filename = file.getAbsolutePath();

        if (file.exists() == false)
        {
          //  getLogger().error(filename + " does not exist");

            valid = false;

            return valid;
        }

        if (directory == false)
        {
            if (file.isFile() == false)
            {
           //     getLogger().error(filename + " not a file");

                valid = false;

                return valid;
            }
        }
        else
        {
            if (file.isDirectory() == false)
            {
             //   getLogger().error(filename + " not a directory");

                valid = false;

                return valid;
            }
        }

        if (file.canRead() == false)
        {
           // getLogger().error("unable to read " + filename);

            valid = false;

            return valid;
        }

        if (needWriteAccess == true)
        {
            if (file.canWrite() == false)
            {
              //  getLogger().error("unable to write " + filename);

                valid = false;

                return valid;
            }
        }

        valid = true;

        return valid;
    }
    /**
     * Copies the agentconfiguration.xml file from <code>/usr/tomcat/webapps/<virtual host>/PinAgent/classes/configuration</code> to the
     * <code>/usr/tomcat/webapps/<virtual host>/configuration</code> directory
     * 
     * @param virtualHostName The virtual host to set the configuration for
     */
    
    public void setConfiguration (String virtualHostName)
    {
        

        // Check that the configuration directory exists. If not, create it
        File configurationDirectory = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR);
        if (!configurationDirectory.exists())
        {
            boolean success = configurationDirectory.mkdir();
            if (!success)
            {
                System.err.println("Could not rename configuration file");
                return;
            }
        }

        // Now we can copy the file
        File configurationSource = new File(SOURCE_FILE_PATH + AGENT_CONFIGURATION_FILE_NAME);
        File configurationDestination = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME);

       
        if (configurationDestination.exists())
        {
            // Rename the current file
            Date date = new Date();
            boolean success = configurationDestination.renameTo(new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME
                    + ".backup" + date.toString()));
            if (!success)
            {
                System.err.println("Could not rename configuration file");
                return;
            }
        }

        // Do the copy
        try
        {
            FileCopy.copyFile(configurationSource, configurationDestination);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error - configuration file not found, has it been uploaded and imported?");
            return;
        }
        catch (IOException e)
        {
            System.err.println("Fatal error copying file, unable to set configuration");
            e.printStackTrace();
            return;
        }

        // Now delete the configuration file from /usr-files to prevent possible future confusion
        FileDelete.deleteFile(configurationSource);
     
    }

    
    /**
     * Copies the pinagent-log4j.xml file from <code>/usr-files</code> to the
     * <code>/usr/tomcat/webapps/<virtual host>/configuration</code> directory.
     * 
     * @param virtualHostName The virtual host to set configuration for.
     */
    public void setLogConfiguration (String virtualHostName)
    {
    	// Check that the configuration directory exists. If not, create it
        File configurationDirectory = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR);
        if (!configurationDirectory.exists())
        {
            boolean success = configurationDirectory.mkdir();
            if (!success)
            {
                System.err.println("Could not delete configuration directory");
                return;
            }
        }

        // Now we can copy the file
        File configurationSource = new File(SOURCE_FILE_PATH + AGENT_LOG_CONFIGURATION_FILE_NAME);
		File configurationDTDSource = new File(SOURCE_FILE_PATH + AGENT_LOG_DTD_CONFIGURATION_FILE_NAME);
        File configurationDestination = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_LOG_CONFIGURATION_FILE_NAME);
		File configurationDTDDestination = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_LOG_DTD_CONFIGURATION_FILE_NAME);

      
        if (configurationDestination.exists())
        {
            // Rename the current file
            Date date = new Date();
            boolean success = configurationDestination.renameTo(new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME
                    + ".backup" + date.toString()));
            if (!success)
            {
                System.err.println("Could not rename configuration file");
                return;
            }

        }
		if (configurationDTDDestination.exists())
        {
            // Rename the current file
            Date date = new Date();
            boolean success = configurationDestination.renameTo(new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME
                    + ".backup" + date.toString()));
            if (!success)
            {
                System.err.println("Could not rename configuration file");
                return;
            }

        }

        // Do the copy
        try
        {
            FileCopy.copyFile(configurationSource, configurationDestination);
			FileCopy.copyFile(configurationDTDSource, configurationDTDDestination);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error - configuration file not found, has it been uploaded and imported?");
            return;
        }
        catch (IOException e)
        {
            System.err.println("Fatal error copying file, unable to set configuration");
            e.printStackTrace();
            return;
        }

        // Now delete the configuration file from /usr-files to prevent possible future confusion
        FileDelete.deleteFile(configurationSource);
        FileDelete.deleteFile(configurationDTDSource);
        
    }
    
   /**
     * Copies the pinagent-version file from <code>/usr-files</code> to the
     * <code>/usr/tomcat/webapps/<virtual host>/configuration</code> directory.
     * 
     * @param virtualHostName The virtual host to set configuration for.
     */
    public void setVersion (String virtualHostName)
    {
    	// Check that the configuration directory exists. If not, create it
        File configurationDirectory = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR);
        if (!configurationDirectory.exists())
        {
            boolean success = configurationDirectory.mkdir();
            if (!success)
            {
                System.err.println("Could not make configuration directory");
                return;
            }
        }

        // Now we can copy the file
        File configurationSource = new File(SOURCE_FILE_PATH + AGENT_VERSION_FILE_NAME);
        File configurationDestination = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_VERSION_FILE_NAME);

      
        if (configurationDestination.exists())
        {
            // Rename the current file
            Date date = new Date();
            boolean success = configurationDestination.renameTo(new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_VERSION_FILE_NAME
                    + ".backup" + date.toString()));
            if (!success)
            {
                System.err.println("Could not rename version file");
                return;
            }

        }

        // Do the copy
        try
        {
            FileCopy.copyFile(configurationSource, configurationDestination);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error - version file not found, has it been uploaded and imported?");
            return;
        }
        catch (IOException e)
        {
            System.err.println("Fatal error copying file, unable to view version");
            e.printStackTrace();
            return;
        }

        // Now delete the configuration file from /usr-files to prevent possible future confusion
        FileDelete.deleteFile(configurationSource);
     
    }
    
    /**
     * Copies the pinagent-version file from <code>/usr-files</code> to the
     * <code>/usr/tomcat/webapps/<virtual host>/configuration</code> directory.
     * 
     * @param virtualHostName The virtual host to set configuration for.
     */
    public void setLogFile(String virtualHostName)
    {
    	// Check that the configuration directory exists. If not, create it
        File configurationDirectory = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR);
        String logFileName = new String(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_LOG_FILE_NAME);
        if (!configurationDirectory.exists())
        {
            boolean success = configurationDirectory.mkdir();
            if (!success)
            {
                System.err.println("Could not make configuration directory");
                return;
            }
        }

        // Now we can copy the file
       String configurationSource = new String(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_LOG_CONFIGURATION_FILE_NAME);
       String configurationDestination = new String(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + BACKUP_AGENT_LOG_CONFIGURATION_FILE_NAME);

       File sourceFile = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_LOG_CONFIGURATION_FILE_NAME);
       File destinationFile = new File(CONFIGURATION_DESTINATION_PATH_START + virtualHostName + CONFIGURATION_DESTINATION_DIR + BACKUP_AGENT_LOG_CONFIGURATION_FILE_NAME);
 
        
        DOMParser parser = new DOMParser();
        Document doc ;
        Element valueEle = null;
        Element paramEle = null;
        String valueName = null ;
        String paramName = null;
        
        //copy the original file to a backup first
		
		 try
		 {
			 FileCopy.copyFile(sourceFile,destinationFile);
		
		 }
		catch(IOException e)
		{
			System.out.println("Could not Update Configuration File");
			return ;
		}
		 //parse the document
		 try
		 {
			// parser.parse("E:\\PINagentCLI\\pinagent-log4j.xml");
			 parser.parse(configurationDestination);
			 doc = parser.getDocument();
		 }
		 catch (SAXException c)
		 {
			c.printStackTrace();
			return;
		 }
		 catch (IOException c)
		 {
			c.printStackTrace();
			return ;
		 }
        
		//searches for appender
			NodeList nodes = doc.getElementsByTagName("appender");
			try
				{
					
					for(int i=0;i<nodes.getLength();i++)
					{
					   	
					   valueEle = (Element)nodes.item(i);
					   
					   if(valueEle != null)
					   valueName = valueEle.getAttribute("name");
			
					 
					   if((valueName != null) &&
						 (valueName.trim().compareToIgnoreCase("FILE") == 0))
					   {
						   
							break;
					   }
					}
					NodeList paramlist = null;
					if(valueEle != null)
						paramlist = valueEle.getElementsByTagName("param");
										
					if(paramlist != null && paramlist.getLength() > 0)
					{
					
					   
					   for(int j = 0; j< paramlist.getLength(); j++)
					   {
						paramEle = (Element)paramlist.item(j);
						paramName = paramEle.getAttribute("name");
						

						if((paramName != null) &&
							(paramName.trim().compareToIgnoreCase("File") == 0))
						{
							 
							paramEle.removeAttribute("value");
							paramEle.setAttribute("value", logFileName);
							
			
					   }
					 }
					
				}
				}
				catch(Exception e)
				{
					System.err.println("Could not update log file ");
					return ;
				}
			 
	  

	//transform the file to xml format 
			TransformerFactory tFactory = TransformerFactory.newInstance();
			try
			{
					Transformer transformer = tFactory.newTransformer();	
					doc.normalize();

					DOMSource source = new DOMSource(doc);
					
					//StreamResult result = new StreamResult("E:\\PINagentCLI\\pinagent-log4j.xml");
					StreamResult result = new StreamResult(configurationDestination);
					transformer.transform(source, result);
			}
			catch (TransformerConfigurationException e)
			{
				System.err.println("Could not update log file");
					return ;
				
			}
			catch (TransformerException e)
			{
				System.err.println("Could not update log file ");
					return ;
			}
			 try
			 {
				 FileCopy.copyFile(destinationFile, sourceFile);
			
			 }
			catch(IOException e)
			{
				System.out.println("Could not Update Configuration File");
				return ;
			}
			
			 
	        FileDelete.deleteFile(destinationFile);
	     
    }
	
	private static boolean isValidHostname(String s) 
	{
		 final char[] chars = s.toCharArray();
		 for (int x = 0; x < chars.length; x++) 
		 {      
		   final char c = chars[x];
		   if ((c >= 'a') && (c <= 'z')) continue; 
		   if ((c >= 'A') && (c <= 'Z')) continue; 
		   if ((c >= '0') && (c <= '9')) continue; 
		   if (c == '.' || c == '_' || c == '-') continue;
		   
		   System.out.println("Please enter the valid hostname.");
		   return false;
		 }  
		 return true;
	}
	
	
   
}
