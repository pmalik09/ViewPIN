package com.safenetinc.viewpin.cli.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Appender;

import com.safenetinc.viewpin.cli.FileHandler;

public class ReadLoggingConfiguration extends org.apache.log4j.xml.DOMConfigurator {
	
	
	 
	 private static 	  Logger  logger                         					   = Logger.getLogger(ReadLoggingConfiguration.class);
	 
	 static LoggingConfigurationElements loggingConfigurationElements 				   = new LoggingConfigurationElements();
	 	 
	 static String configFile 														   = null;
	 
	 static File loggingConfigurationFile											   = null;		
	 
	 static Document dom															   = null;
	 
	 static Document doc 															   = null;
	 
	 public static boolean checkFileStatus()
 	 {
		
		configFile = Constants.Webapp_Directory + Constants.PinAuthority_Directory+Constants.Logging_Config_File;
		   
    	boolean fileStatus = FileHandler.fileExists(Constants.Logging_Config_File, new File(Constants.Webapp_Directory));
        	
		if(false == fileStatus)
		{
			return false;
		}
		
		
		return true;
		
	 }
	 
	 public static boolean readLoggingConfiguration(String element, String elementName, String parameterName)
	{
		 DOMParser parser = new DOMParser();
		 Element valueEle = null;
		 Element paramEle = null;
		 String valueName = null ;
		 String paramName = null;
		 String threshCheck = null;
		 
		 try
		 {
			 parser.parse(configFile);
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
		
		 try
		 {
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
			
					   if((valueName !=null)&&
					   	  (valueName.trim().compareToIgnoreCase(elementName) == 0))
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
						
						 //check for stdout/syslogudp
						if((paramName != null) &&
							(paramName.trim().compareToIgnoreCase(parameterName) == 0))
						{
							threshCheck = paramEle.getAttribute(Constants.ATTRIBUTE_VALUE);
						}
			
					   }
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
			
					   if((valueName !=null)&&
						   (valueName.trim().compareToIgnoreCase(elementName) == 0))
					   {
						  break;
					   }
					}
			
					NodeList paramlist = valueEle.getElementsByTagName(Constants.ELEMENT_PRIORITY);
										
					if(paramlist != null && paramlist.getLength() > 0)
					{
						//check for syslogHost/threshold
					   for(int j = 0; j< paramlist.getLength(); j++)
					   {
						paramEle = (Element)paramlist.item(j);
						paramName = paramEle.getAttribute(Constants.ATTRIBUTE_VALUE);
					
					   }
					 }
				}
				catch(Exception e)
				{
					logger.error(e.getMessage());
					return false;
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			return false;
		}
			
		if (elementName == "stdout")
			loggingConfigurationElements.setConsoleAppenderThreshold(threshCheck);
		else if (elementName == "FILE")
			loggingConfigurationElements.setFileAppenderThreshold(threshCheck);
		else if((elementName == "syslogudp") && (parameterName != "syslogHost"))
			loggingConfigurationElements.setSyslogAppenderThreshold(threshCheck);
		else if((elementName == "syslogudp") && (parameterName == "syslogHost"))
			loggingConfigurationElements.setSyslogAppenderIP(threshCheck);
		else if(elementName == "com.safenetinc.viewpin") 
			loggingConfigurationElements.setViewPINThreshold(paramName);
		
		return true;
	 }
	
	 private   Logger getLogger ()
    {
        return logger;
    }
	
}
