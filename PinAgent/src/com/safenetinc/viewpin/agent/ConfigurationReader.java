// $Id: PinAgent/src/com/safenetinc/viewpin/agent/ConfigurationReader.java 1.1 2008/09/04 10:45:40IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import com.safenetinc.viewpin.agent.exceptions.AgentInitException;
import com.safenetinc.viewpin.common.utils.Utils;

/**
 * Class to manage reading from an XML configuration file
 * @author Stuart Horler/Paul Hampton
 *
 *  $Id: PinAgent/src/com/safenetinc/viewpin/agent/ConfigurationReader.java 1.1 2008/09/04 10:45:40IST Mkhurana Exp  $
 * 
 */
public class ConfigurationReader
{
    static Integer readConfigurationInteger (XMLConfiguration configuration, String configurationKeyName, Logger logger) throws AgentInitException
    {
        Integer configurationValue;

        configurationValue = null;

        try
        {
            configurationValue =  Integer.valueOf(configuration.getInt(configurationKeyName));
        }
        catch (Exception e)
        {
            logger.fatal("reading configuration value " + configurationKeyName + " " + e.getMessage());

            throw new AgentInitException();
        }

        return configurationValue;
    }
    
    static String readConfigurationString (XMLConfiguration configuration, String configurationKeyName, Logger logger) throws AgentInitException
    {
        String configurationValue;

        configurationValue = null;

        try
        {
            configurationValue = configuration.getString(configurationKeyName);
        }
        catch (Exception e)
        {
            logger.fatal("reading configuration value " + configurationKeyName + " " + e.getMessage());

            throw new AgentInitException();
        }

        return configurationValue;
    }

    static Long readConfigurationLong (XMLConfiguration configuration, String configurationKeyName, Logger logger) throws AgentInitException
    {
        Long configurationValue;

        configurationValue = null;

        try
        {
            configurationValue = Long.valueOf(configuration.getLong(configurationKeyName));
        }
        catch (Exception e)
        {
            logger.fatal("reading configuration value " + configurationKeyName + " " + e.getMessage());

            throw new AgentInitException();
        }

        return configurationValue;
    }
    
    static XMLConfiguration parseConfigurationFile(File configurationFile, Logger logger) throws AgentInitException
    {
        XMLConfiguration configuration;

        configuration = null;

        try
        {
            // Parse configuration file
            configuration = new XMLConfiguration(configurationFile);
        }
        catch (ConfigurationException ce)
        {
            logger.fatal("parsing configuration file " + configurationFile.getAbsolutePath() + " " + ce.getMessage());

            throw new AgentInitException();
        }

        logger.debug("parsed configuration file " + configurationFile.getAbsolutePath());

        return configuration;
    }
    
    static File openConfigurationFile(String configurationFilename, Logger logger) throws AgentInitException
    {
        File comDirectory;
        File homeDirectory;
        File configurationDirectory;
        File configurationFile;

        comDirectory = null;
        homeDirectory = null;
        configurationDirectory = null;
        configurationFile = null;

        try
        {
            // Get com directory
            comDirectory = Utils.getDirectoryResource("com", false);
        }
        catch(IOException ioe)
        {
            logger.fatal("loading com directory " + ioe.getMessage());

            throw new AgentInitException();
        }

        logger.debug("got com directory OK");

        // Get home directory
        homeDirectory = comDirectory.getParentFile().getParentFile().getParentFile().getParentFile();

        // Validate home directory file
        if(Utils.validateDirectory(homeDirectory, false) == false)
        {
            // Failed to validate home directory
            logger.fatal("validating home directory " + homeDirectory.getAbsolutePath());

            throw new AgentInitException();
        }

        logger.debug("home directory = " + homeDirectory.getAbsolutePath());

        // Get configuration directory
        configurationDirectory = new File(homeDirectory.getAbsolutePath() + File.separator + "configuration");

        // Validate configuration directory
        if (Utils.validateDirectory(configurationDirectory, false) == false)
        {
            // Failed to validate configuration directory
            logger.fatal("validating configuration directory " + configurationDirectory.getAbsolutePath());

            throw new AgentInitException();
        }

        logger.debug("configuration directory = " + configurationDirectory.getAbsolutePath());

        // Get configuration file
        configurationFile = new File(configurationDirectory.getAbsolutePath() + File.separator + configurationFilename);

        // Validate configuration file
        if(Utils.validateFile(configurationFile, false) == false)
        {
            // Failed to validate configuration file
            logger.fatal("validating configuration file " + configurationFile.getAbsolutePath());

            throw new AgentInitException();
        }

        return configurationFile;
    }
}
