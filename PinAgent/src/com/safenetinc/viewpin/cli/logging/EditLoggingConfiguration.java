package com.safenetinc.viewpin.cli.logging;

import java.io.*;

import org.apache.xerces.parsers.DOMParser;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

import org.xml.sax.SAXException;
import javax.xml.transform.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.net.*;
import java.net.UnknownHostException;

import com.safenetinc.Common;
import com.safenetinc.viewpin.cli.FileHandler;
/**
 * This class edits the Logging configuration
 * @author Pmalik
 *
 */
public class EditLoggingConfiguration {

	LoggingConfigurationElements loggingConfigurationElements 				  = new LoggingConfigurationElements();
	
	private static Logger    logger                             			  = Logger.getLogger(EditLoggingConfiguration.class);
	
    private InputStreamReader isr 											  = new InputStreamReader( System.in );
	
    private BufferedReader stdin											  = new BufferedReader( isr );

    private static final String APPLICATION_NAME              				  = "EditLoggingConfiguration";
	
    private static final String SERVER_NAME                    				  = "ViewPIN";
    
	private static final String HOSTNAME_COMMAND_LINE_ARG       			  = "hostname";
	
	/*
	 * Default Constructor
	 */
	public EditLoggingConfiguration(String HostName)
	{
		if(false == updatePINAgentLoggingConfiguration(HostName))
		{
        	System.out.println("Edit Configuration failed.");
        	return;
        }
		else 
		{
			System.out.println("Edit Configuration Successful.");
		}
	}
	
	/*
	 * Main  function to view the logging configuration of PIN Agent
	 * @param args
	 * EditLoggingConfiguration -name "name of the pinagent" for eg www.mybank.com
	 */
	public static void main(String[] args)
	{
		// parse the command line arguments
			
        final Options options = new Options();

        OptionBuilder.withArgName(HOSTNAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The name of the virtual host added, for example xyz.com");
        final Option hostname = OptionBuilder.create(HOSTNAME_COMMAND_LINE_ARG);
        
        options.addOption(hostname);
		
		     
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
		
		if(args.length !=2)
		{
			final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
		}
		
        //check if the file exits
        if( false == ReadLoggingConfiguration.checkFileStatus(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG)))
        {
        	System.out.println("Either the virtual host with this name does not exist or Configuration File is missing");
        	return;
        }
        
        boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
		
		new EditLoggingConfiguration(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG));
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		return;
	}
	
	/*
	 * UpdatePINAgentLoggingConfiguration
	 * updates the logging elements of PIN Agent
	 * @param: virtual hostname of the PIN Agent whose elements are to be edited 
	 * reads the element from the file
	 * updates it if the input is correct
	 * calls the updateFile to update the element
	 */
	public boolean updatePINAgentLoggingConfiguration(String virtualHostName)
	{
		String setValue = null;
		String elementValue = null;
		String element = null;
		// get value of console output
		setValue = null;
					
		element = Constants.ELEMENT_APPENDER;
		
		/*
		//read threshold of console output
		elementValue = Constants.CONSOLE_APPENDER_NAME;
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
        	logger.error("Could not read Console Appender Threshold Value");
        	return false;
        }
		
		//print Console Appender Threshold
		System.out.println(Constants.CONSOLE_APPENDER_THRESHOLD + "[" + 
				loggingConfigurationElements.getConsoleAppenderThreshold() + "]" + ":");
		
		//edit  Console Appender Threshold
		try
		{
			setValue = stdin.readLine();
			
			if(setValue == null)
			{
				logger.error("Console Appender Value cannot be set null");
				return false;
			}
			
			if(setValue.length() != 0)
				if(checkValue(setValue)==false)
					return false;
				else
					updatePINAgentConfigurationFile(element, elementValue, Constants.THRESHOLD_VALUE, setValue, virtualHostName);
				
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
        	return false;
		}
		
		*/
		
		//read threshold of file output
		elementValue = Constants.FILE_APPENDER_NAME;
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
        	logger.error("Could not read Console Appender Threshold Value");
        	return false;
        }
		
		//print FILE Appender Threshold
		System.out.println(Constants.FILE_APPENDER_THRESHOLD + "[" + 
				loggingConfigurationElements.getFileAppenderThreshold() + "]" + ":");
		
		//edit  FILE Appender Threshold
		try
		{
			setValue = stdin.readLine();
			
			if(setValue == null)
			{
				logger.error("File Appender Value cannot be set null");
				return false;
			}
			
			if(setValue.length() != 0)
				if(checkValue(setValue)==false)
					return false;
				else
					updatePINAgentConfigurationFile(element, elementValue, Constants.THRESHOLD_VALUE, setValue, virtualHostName);
				
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
        	return false;
		}
			
		//Read threshold  of syslog Appender
		elementValue =  Constants.SYSLOG_APPENDER_NAME;
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
			logger.error("Could not read Syslog Appender Threshold Value");
        	return false;
        }
		//print Syslog Appender Threshold
		System.out.println(Constants.SYSLOG_APPENDER_THRESHOLD + "[" + 
				loggingConfigurationElements.getSyslogAppenderThreshold() + "]" + ":");
				
		//edit Syslog Appender Threshold
		try
		{
			setValue = stdin.readLine();
			
			if(setValue == null)
			{
				logger.error("Syslog Appender Value cannot be set null");
				return false;
			}
			
			if(setValue.length() != 0)
				if(checkValue(setValue)==false)
					return false;
				else
					updatePINAgentConfigurationFile(element, elementValue, Constants.THRESHOLD_VALUE, setValue, virtualHostName);

		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
        	return false;
		}
		
		
		//Read threshold  of syslog IP only if Syslog Appender is not OFF
		elementValue =  Constants.SYSLOG_APPENDER_NAME;
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
			logger.error("Could not read Syslog Appender Threshold Value");
        	return false;
        }
		
		if(loggingConfigurationElements.getSyslogAppenderThreshold().compareToIgnoreCase("OFF")!=0) 
		{
			if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.SYSLOGHOST_VALUE))
			{
				logger.error("Could not read Syslog Appender Syshost Value");
				return false;
			}
			//print Syslog IP
			System.out.println(Constants.SYSLOG_APPENDER_IP + "[" + 
					loggingConfigurationElements.getsyslogAppenderIP() + "]" + ":");
				
			//edit Syslog IP
			try
			{
				setValue = stdin.readLine();
		
				if(setValue == null)
				{
					System.out.println("Syslog IP Value cannot be set null");
					return false;
				}
			
				//TODO: add check for IP/hostname
			
				if(setValue.length() != 0)
					if(checkAddress(setValue)==false)
					{
						logger.error("Please provide a valid Ip Address and Hostname.");
						return false;
					}
					else
					{
						if(CheckConnection(setValue)==false)
							return false;
						else
							updatePINAgentConfigurationFile(element, elementValue, Constants.SYSLOGHOST_VALUE, setValue, virtualHostName);
					}

			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		}
		
		//Read value of viewPIN priority
		element = Constants.ELEMENT_CATEGORY;
		elementValue = Constants.VIEWPIN_CATEGORY_NAME;
		
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue, "test"))
		{
			logger.error("Could not read ViewPIN Appender Threshold Value");
        	return false;
        }
		
		//Print ViewPIN Threshold
		System.out.println(Constants.VIEWPIN_APPENDER_THRESHOLD + "[" + 
				loggingConfigurationElements.getViewPINThreshold()+ "]" + ":");
		//edit Syslog IP
		try
		{
			setValue = stdin.readLine();
			
			if(setValue == null)
			{
				logger.error("ViewPINThreshold cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(checkValue(setValue)==false)
					return false;
				else
					updatePINAgentConfigurationFile(element, elementValue, "test", setValue, virtualHostName);

		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}		
	
	
	/*
	 * UpdatePINAgentConfigurationFile
	 * @param element: name of the element to be updated
	 * @param elementValue: the value of the element to be updated
	 * @param parameterName: the value of the 
	 * @param paramValue: new value of the element
	 * updates the copied file 
	 * if update is successfull
	 * copies the new file to original file
	 */
	public boolean updatePINAgentConfigurationFile(String element, String elementValue, String parameterName, String paramValue, String virtualHostName)
	{
		 DOMParser parser = new DOMParser();
		 Document doc ;
		 Element valueEle = null;
		 Element paramEle = null;
		 String valueName = null ;
		 String paramName = null;
		 String threshCheck = null;
		 
		 //check the value if its not syslog host
		if(0 != parameterName.compareToIgnoreCase(Constants.SYSLOGHOST_VALUE))
		{
		 if(false == checkValue(paramValue))
			{
				return false;
			}
		}
		//copy the original file to a backup first
			
		 try
		 {
			FileHandler.copyFile(new File(Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+Constants.Logging_Config_File),
						new File(Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+Constants.New_Logging_Config_File));
		
		 }
		catch(IOException e)
		{
			System.out.println("Could not Update Configuration File");
			return false;
		}
		 //parse the document
		 try
		 {
			// parser.parse("E:\\PINagentCLI\\pinagent-log4j.xml");
			 parser.parse(Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+Constants.New_Logging_Config_File);
			 doc = parser.getDocument();
		 }
		 catch (SAXException c)
		 {
			c.printStackTrace();
			return false;
		 }
		 catch (IOException c)
		 {
			c.printStackTrace();
			return false;
		 }
		
		 //compare if element is of category type
		if(0 != element.compareToIgnoreCase(Constants.ELEMENT_CATEGORY))
		{
			//searches for appender
			NodeList nodes = doc.getElementsByTagName(element);
					
			
			try
			{
				
				for(int i=0;i<nodes.getLength();i++)
				{
				   	
				   valueEle = (Element)nodes.item(i);
				   
				   if(valueEle != null)
				   valueName = valueEle.getAttribute(Constants.ATTRIBUTE_NAME);
		
				   //check for stdout/syslogudp
				   if((valueName != null) &&
					 (valueName.trim().compareToIgnoreCase(elementValue) == 0))
				   {
					   
						break;
				   }
				}
				NodeList paramlist = null;
				if(valueEle != null)
					paramlist = valueEle.getElementsByTagName(Constants.ELEMENT_PARAM);
									
				if(paramlist != null && paramlist.getLength() > 0)
				{
				
				   for(int j = 0; j< paramlist.getLength(); j++)
				   {
					paramEle = (Element)paramlist.item(j);
					paramName = paramEle.getAttribute(Constants.ATTRIBUTE_NAME);
					
					 //check for syslogHost/threshold
					if((paramName != null) &&
						(paramName.trim().compareToIgnoreCase(parameterName) == 0))
					{
						paramEle.removeAttribute(Constants.ATTRIBUTE_VALUE);
						if(0 == parameterName.compareToIgnoreCase(Constants.SYSLOGHOST_VALUE))
							paramEle.setAttribute(Constants.ATTRIBUTE_VALUE, paramValue);
						else
							paramEle.setAttribute(Constants.ATTRIBUTE_VALUE, paramValue.toUpperCase());
					}
		
				   }
				 }
				else
				{
					logger.error("Could not update Configuration file");
					return false;
				}
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		}
		else if(0 == element.compareToIgnoreCase(Constants.ELEMENT_CATEGORY))
		{
			NodeList nodes = doc.getElementsByTagName(element);
					
			
			try
			{
				for(int i=0;i<nodes.getLength();i++)
				{
				   	
				   valueEle = (Element)nodes.item(i);
				 
				   valueName = valueEle.getAttribute(Constants.ATTRIBUTE_NAME);
		
				   if((valueName != null)&&
				      (valueName.trim().compareToIgnoreCase(elementValue) == 0))
				   {
					  break;
				   }
				}
		
				NodeList paramlist = null;
				if(valueEle != null)
					paramlist = valueEle.getElementsByTagName(Constants.ELEMENT_PRIORITY);
									
				if(paramlist != null && paramlist.getLength() > 0)
				{
					//check for syslogHost/threshold
				   for(int j = 0; j< paramlist.getLength(); j++)
				   {
					 paramEle = (Element)paramlist.item(j);
					 paramEle.removeAttribute(Constants.ATTRIBUTE_VALUE);
					 paramEle.setAttribute(Constants.ATTRIBUTE_VALUE,paramValue.toUpperCase() );
				
				   }
				 }
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		}
			
		//transform the file to xml format 
		TransformerFactory tFactory = TransformerFactory.newInstance();
		try
		{
				Transformer transformer = tFactory.newTransformer();	
				doc.normalize();

				DOMSource source = new DOMSource(doc);
				
				//StreamResult result = new StreamResult("E:\\PINagentCLI\\pinagent-log4j.xml");
				StreamResult result = new StreamResult(Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+Constants.New_Logging_Config_File);
				transformer.transform(source, result);
		}
		catch (TransformerConfigurationException e)
		{
			logger.error(e.getMessage());
			return false;
		}
		catch (TransformerException e)
		{
			logger.error(e.getMessage());

			return false;
		}
		
		//copy the new document created to the original document
		
		 try
		 {
			FileHandler.copyFile(new File(Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+Constants.New_Logging_Config_File),
					new File(Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+Constants.Logging_Config_File));
		
		 }
		catch(IOException e)
		{
			System.out.println("Could not Update Configuration File");
			return false;
		}
		
		//delete the backed up from usr-files
		if(false == FileHandler.deleteFile(new File(Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+Constants.New_Logging_Config_File)))
		{
			System.out.println("Could not Update Configuration File");
			return false;
		}	
		return true;
		
	}
	
	/*
	 * CheckValue
	 * @param value: the value to be checked if its OFF/DEBUG/INFO/ERR/WARN
	 * if none of these return false
	 * else
	 * return true
	 */
	private boolean checkValue(String value)
	{
		if(value != null)
		{
			if((0 ==value.compareToIgnoreCase(Constants.OFF)) ||
						(0 == value.compareToIgnoreCase(Constants.DEBUG)) ||
						(0 == value.compareToIgnoreCase(Constants.FATAL)) ||
						(0 == value.compareToIgnoreCase(Constants.INFO))||
						(0 == value.compareToIgnoreCase(Constants.WARN)) ||
						(0 == value.compareToIgnoreCase(Constants.ERROR)))
				return true;
			else
			{
				logger.error("Value cant be set to " + value);
				logger.error("Permitted Values are OFF/DEBUG/WARN/INFO/ERROR/FATAL");
				return false;
			}
		}
		else
		{
			logger.error("Invalid Value");
			logger.error("Permitted Values are OFF/DEBUG/WARN/INFO/ERROR/FATAL");
			return false;
		}
							
	}
	
	private boolean checkAddress(String ipaddr)
	{
		 final char[] chars = ipaddr.toCharArray();
		 for (int x = 0; x < chars.length; x++) {      
		   final char c = chars[x];
		   if ((c >= 'a') && (c <= 'z')) 
				continue; 
		   if ((c >= 'A') && (c <= 'Z')) 
				continue; 
		   if ((c >= '0') && (c <= '9')) 
				continue; 
		   if ((c=='.')||(c=='_')||(c=='-') ) 
				continue;
				
		   return false;
		 }  
		 return true;
	}
	
	private boolean CheckConnection(String IpAddress) 
	{
		// code to resolve the hostname and check the connectivity.
		boolean connected = true;
		try
		{
			InetAddress[] addr= InetAddress.getAllByName(IpAddress);
			for (int i=0;i<=addr.length;i++)
			{
				logger.debug("Address found: " + addr[0]);
			}
		}
		catch(UnknownHostException e)
		{	
			System.out.println("Could not found the Host: " + e.getMessage());
			connected = false;
		}
		catch(SecurityException e)
		{
			logger.error(e.getMessage());
			connected = false;
		}
		
		if(connected)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static Logger getLogger ()
	{
	    return logger;
	}
		
}
