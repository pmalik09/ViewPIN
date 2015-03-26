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

import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.Common;

public class EditPinAuthorityLoggingConfiguration {

	LoggingConfigurationElements loggingConfigurationElements 				  = new LoggingConfigurationElements();
	
	private static Logger    logger                             			  = Logger.getLogger(EditPinAuthorityLoggingConfiguration.class);
	
    private InputStreamReader isr 											  = new InputStreamReader( System.in );
	
    private BufferedReader stdin											  = new BufferedReader( isr );

    private static final String APPLICATION_NAME              				  = "EditPinAuthorityLogging";
	
      
	
	/*
	 * Default Constructor
	 */
	public EditPinAuthorityLoggingConfiguration(String args[])
	{
		if(args.length !=0) 
		{
			System.out.println("EditPinAuthorityLogging,no option is required to Edit the Configuration");
            return;
		}
		if(false == updatePINAuthorityLoggingConfiguration())
		{
        	System.out.println("Edit Configuration failed.");
        	
        }
		else 
		{
			System.out.println("Edit Configuration Successful.");
		}
		return;
	}
	
	/*
	 * Main  function to view the logging configuration of PIN Authority
	 * @param args
	  */
	public static void main(String[] args)
	{
		boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
		//check if the file exits
        if( false == ReadLoggingConfiguration.checkFileStatus())
        {
        	System.out.println("Configuration File is missing.");
        	return;
        }
        new EditPinAuthorityLoggingConfiguration(args);
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		return;
	}
	
	/*
	 * UpdatePINAuthorityLoggingConfiguration
	 * updates the logging elements of PIN Authority
	 * reads the element from the file
	 * updates it if the input is correct
	 * calls the updateFile to update the element
	 */
	public boolean updatePINAuthorityLoggingConfiguration()
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
					updatePINAuthorityConfigurationFile(element, elementValue, Constants.THRESHOLD_VALUE, setValue);
				
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
        	return false;
		}
		
		*/
		
		//read threshold of File output
		elementValue = Constants.FILE_APPENDER_NAME;
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
        	logger.error("Could not read File Appender Threshold Value");
        	return false;
        }
	
		//print File Appender Threshold
		System.out.println(Constants.FILE_APPENDER_THRESHOLD + "[" + 
				loggingConfigurationElements.getFileAppenderThreshold() + "]" + ":");
		
		//edit  File Appender Threshold
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
					updatePINAuthorityConfigurationFile(element, elementValue, Constants.THRESHOLD_VALUE, setValue);
				
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
					updatePINAuthorityConfigurationFile(element, elementValue, Constants.THRESHOLD_VALUE, setValue);

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
			
		
				if(setValue.length() != 0)
					if(checkAddress(setValue)==false)
					{
						logger.error("Please provide the valid IpAddress or hostname.");
						return false;
					}
					else
					{
						if(CheckConnection(setValue)==false)
							return false;
						else
							updatePINAuthorityConfigurationFile(element, elementValue, Constants.SYSLOGHOST_VALUE, setValue);
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
					updatePINAuthorityConfigurationFile(element, elementValue, "test", setValue);

		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}		
	
	
	/*
	 * UpdatePINAuthorityConfigurationFile
	 * @param element: name of the element to be updated
	 * @param elementValue: the value of the element to be updated
	 * @param parameterName: the value of the 
	 * @param paramValue: new value of the element
	 * updates the copied file 
	 * if update is successfull
	 * copies the new file to original file
	 */
	public boolean updatePINAuthorityConfigurationFile(String element, String elementValue, String parameterName, String paramValue)
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
			FileHandler.copyFile(new File(Constants.Webapp_Directory+Constants.PinAuthority_Directory+Constants.Logging_Config_File),
						new File(Constants.Webapp_Directory +Constants.PinAuthority_Directory+Constants.New_Logging_Config_File));
		
		 }
		catch(IOException e)
		{
			System.out.println("Could not Update Configuration File");
			return false;
		}
		 //parse the document
		 try
		 {
			 parser.parse(Constants.Webapp_Directory +Constants.PinAuthority_Directory+Constants.New_Logging_Config_File);
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
				 
				   valueName = valueEle.getAttribute(Constants.ATTRIBUTE_NAME);
		
				   //check for FILE/stdout/syslogudp
				   if((valueName != null) &&
					     (valueName.trim().compareToIgnoreCase(elementValue) == 0))
				   {
					   
						break;
				   }
				}
				NodeList paramlist = null;
				if(valueEle != null)
					paramlist = valueEle.getElementsByTagName(Constants.ELEMENT_PARAM);
				else
					paramlist = null;
									
				if(paramlist != null && paramlist.getLength() > 0)
				{
				
				   for(int j = 0; j< paramlist.getLength(); j++)
				   {
					paramEle = (Element)paramlist.item(j);
					paramName = paramEle.getAttribute(Constants.ATTRIBUTE_NAME);
					
					 //check for syslogHost/threshold
					if(( paramName!= null) && 
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
		
				   if((valueName != null) &&
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
				
				StreamResult result = new StreamResult(Constants.Webapp_Directory +Constants.PinAuthority_Directory+Constants.New_Logging_Config_File);
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
			FileHandler.copyFile(new File(Constants.Webapp_Directory+Constants.PinAuthority_Directory+Constants.New_Logging_Config_File),
					new File(Constants.Webapp_Directory +Constants.PinAuthority_Directory+Constants.Logging_Config_File));
		
		 }
		catch(IOException e)
		{
			System.out.println("Could not Update Configuration File");
			return false;
		}
		
		if(false == FileHandler.deleteFile(Constants.Webapp_Directory+Constants.PinAuthority_Directory+Constants.New_Logging_Config_File))
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
		   if ((c=='.')||(c=='-')||(c=='_')) 
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
