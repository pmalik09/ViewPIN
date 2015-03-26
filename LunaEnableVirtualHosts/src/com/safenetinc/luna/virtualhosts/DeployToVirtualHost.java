/*
 * Created on 3 Sep 2007
 * 
 * 
 */
package com.safenetinc.luna.virtualhosts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.safenetinc.luna.fileutils.*;

/**
 * Class to copy files from the /usr-files directory to the appropriate virtual host directory. Used instead
 * of the spa deploy webapplication command when virtual hosting is in use on a Luna SP
 * 
 * @author Paul Hampton
 * 
 * 
 */
public class DeployToVirtualHost
{
    private static final String APPLICATION_NAME                  = "DeployToVirtualHost";

    private static final String FILENAME_COMMAND_LINE_ARG         = "file";

    private static final String VIRTUAL_HOSTNAME_COMMAND_LINE_ARG = "virtualHost";

    private static final String SOURCE_FILE_PATH                  = "/usr-files";

    private static final String DEST_FILE_PATH                    = "/usr/tomcat/webapps";

    /**
     * Constructor
     */
    public DeployToVirtualHost()
    {
        //No action required
    }

    /**
     * Deploys (copies) files to a virtual host's root directory. The files are sourced from the
     * <code>/usr-files</code> directory, consequently this method assumes that the user has already
     * imported the required file from the Luna SP ctp directory into the <code>/usr-files</code> directory.
     * 
     * @param filename The file to deploy
     * @param hostname The name of the virtual host to deploy the file to
     * @throws IOException Thrown if an IO error occurs during the file copy
     * @throws FileNotFoundException Thrown if the file specified in the filename parameter doesn't exist
     */
    public static void deployFileToVirtualHost (String filename, String hostname) throws IOException,
            FileNotFoundException
    {
        // Lowercase the hostname
        String lowerCaseHostname = hostname.toLowerCase();

        // Now copy the file
        File inputFile = new File(SOURCE_FILE_PATH + "/" + filename);
        File outputFile = new File(DEST_FILE_PATH + "/" + lowerCaseHostname + "/" + filename);
        FileCopy.copyFile(inputFile, outputFile);
        // Delete the original file
        FileDelete.deleteFile(inputFile);

    }

    /**
     * Main method for this application. Responsible for informing the user of the correct command-line
     * arguments to use when invoking the application and in turn validating those parameters before calling
     * the {@link #deployFileToVirtualHost(String, String)} method.
     * 
     * @param args Standard command line arguments
     */
    public static void main (String[] args)
    {
        // parse the command line arguments
        Options options = new Options();

        OptionBuilder.withArgName(FILENAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The name of the war file to deploy");
        Option filename = OptionBuilder.create(FILENAME_COMMAND_LINE_ARG);

        OptionBuilder.withArgName(VIRTUAL_HOSTNAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The virtual host to deploy to");
        Option hostname = OptionBuilder.create(VIRTUAL_HOSTNAME_COMMAND_LINE_ARG);

        options.addOption(filename);
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
        String filenameString = cmd.getOptionValue(FILENAME_COMMAND_LINE_ARG);

        // Check we have a war file
        if (filenameString.endsWith(".war") == false)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }

        String hostnameString = cmd.getOptionValue(VIRTUAL_HOSTNAME_COMMAND_LINE_ARG);

        try
        {

            DeployToVirtualHost.deployFileToVirtualHost(filenameString, hostnameString);
        }
        catch (FileNotFoundException fnfe)
        {
            System.err.println("Could not find the file specified");
        }
        catch (IOException ioe)
        {
            System.err.println("An error occurred during deployment " + ioe.getMessage());
        }
    }

}
