/*
 * Created on 13 Sep 2007
 * 
 * 
 */
package com.safenetinc.luna.virtualhosts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.safenetinc.luna.fileutils.FileCopy;
import com.safenetinc.luna.fileutils.FileDelete;
import com.safenetinc.luna.virtualhosts.exception.ServerXmlException;

/**
 * Class to work as an SP command for adding and changing configuration of a ViewPin instance running within a
 * tomcat virtual host.
 * 
 * @author Paul Hampton
 * 
 */
public class SetViewPinConfiguration
{

    private static final String USR_FILES                            = "/usr-files/";

    private static final String VIRTUAL_HOST_COMMAND_LINE_ARG        = "virtualHost";

    private static final String APPLICATION_NAME                     = "SetViewPinConfiguration";

    private static final String AGENT_CONFIGURATION_FILE_NAME        = "agentconfiguration.xml";

    private static final String CONFIGURATION_DESTINATION_PATH_START = "/usr/tomcat/webapps/";

    private static final String CONFIGURATION_DESTINATION_DIR        = "/configuration/";

    /**
     * Copies the agentconfiguration.xml file from <code>/usr-files</code> to the
     * <code>/usr/tomcat/webapps/<virtual host>/configuration</code> directory
     * 
     * @param virtualHostName The virtual host to set the configuration for
     */
    public void setConfiguration (String virtualHostName)
    {
        // Set the hostname in question to be lowercase
        String lowerCaseVirtualHostName = virtualHostName.toLowerCase();

        // Now check that the virtual host in question actually exists
        Vector<String> hosts = null;
        try
        {
            hosts = ServerXmlHandler.getVirtualHostsList();
        }
        catch (ServerXmlException xmle)
        {
            System.err.println("Unable to validate the host with server.xml configuration.");
            System.err.println("This may be a result of a corrupt server.xml, has it been manually edited?");
            return;
        }

        boolean foundHostInConfiguration = false;
        for (int i = 0; i < hosts.size(); i++)
        {
            String hostname = hosts.get(i);
            if (hostname.equals(lowerCaseVirtualHostName))
                foundHostInConfiguration = true;
        }
        if (!foundHostInConfiguration)
        {
            System.out.println("There is no virtual host matching the name supplied. Please check and try again");
            return;
        }

        // Check that the configuration directory exists. If not, create it
        File configurationDirectory = new File(CONFIGURATION_DESTINATION_PATH_START + lowerCaseVirtualHostName + CONFIGURATION_DESTINATION_DIR);
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
        File configurationSource = new File(USR_FILES + AGENT_CONFIGURATION_FILE_NAME);
        File configurationDestination = new File(CONFIGURATION_DESTINATION_PATH_START + lowerCaseVirtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME);

        if (configurationDestination.exists())
        {
            // Rename the current file
            Date date = new Date();
            boolean success = configurationDestination.renameTo(new File(CONFIGURATION_DESTINATION_PATH_START + lowerCaseVirtualHostName + CONFIGURATION_DESTINATION_DIR + AGENT_CONFIGURATION_FILE_NAME
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
        System.out.println("Configuration successfully added to virtual host " + lowerCaseVirtualHostName);
    }

    /**
     * Main method for this application. Responsible for informing the user of the correct command-line
     * arguments to use when invoking the application and in turn validating those parameters before calling
     * the {@link #setConfiguration(String)} method.
     * 
     * @param args The command line arguments
     */
    public static void main (String[] args)
    {
        // parse the command line arguments
        Options options = new Options();

        OptionBuilder.withArgName(VIRTUAL_HOST_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The name of the virtual host to import configuration for");
        Option hostname = OptionBuilder.create(VIRTUAL_HOST_COMMAND_LINE_ARG);

        options.addOption(hostname);

        CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
        try
        {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }

        SetViewPinConfiguration configure = new SetViewPinConfiguration();
        configure.setConfiguration(cmd.getOptionValue(VIRTUAL_HOST_COMMAND_LINE_ARG));

    }

}
