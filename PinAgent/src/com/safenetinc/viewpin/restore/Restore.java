package com.safenetinc.viewpin.restore;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.xpath.XPathConstants;




import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.cli.PosixParser;
import java.util.StringTokenizer;


import com.safenetinc.viewpin.backup.exception.ServerXmlException;
import com.safenetinc.Common;
import com.safenetinc.viewpin.backup.ServerXmlHandler;
import com.safenetinc.viewpin.backup.Utils;


/**
 * This class restore the PINagents from the zip
 * @author Pmalik
 *
 */

public class Restore {

	
	private static final String APPLICATION_NAME                = "Restore";
	
	private static Vector<String> configFileList 				= new Vector<String>();
	
	private static Vector<String> fileList 						= new Vector<String>();
	
	private static Vector<String> hostsList 					= new Vector<String>();
	
	private AddVirtualHost addVirtualHost						= new AddVirtualHost();
	
	public Restore(String[] args)
	{
		try
        {
			
			processCommandLine(args);
            String virtualHost;
            String fileEntry;
            
            virtualHost = null;
            fileEntry = null;
            
            //get virtual names and deflate the zip file
            if(false == unzip())
            {
            	System.out.println("Restore Failed");
            	return;
            }
            //iterate over the hostlist and find configuration file
            ListIterator<String> fileIter = fileList.listIterator();
            while(fileIter.hasNext())
            {
            	fileEntry = fileIter.next();
            	virtualHost = getVirtualHostName((String)fileEntry);
            	 if(true == hostsList.isEmpty())           	
  	            	hostsList.add(virtualHost);
  	            else if(false == hostsList.contains(virtualHost))
  	            	hostsList.add(virtualHost);
            }
            
            
            ListIterator<String> iter = hostsList.listIterator();
            
            while(iter.hasNext())
            {
            	
            	if (false == createVirtualHost((String)iter.next()))
            	{
            		System.out.println("Restore Failed");
                	return;
                
            	}
            			
            }
     	
   
         }
        catch(Exception pe)
        {
        	System.out.println("Restore Failed");
            return;
        }
        System.out.println("Restore Successful");
	}
	public static void main(String[] args)
	{
		
		if(args.length >=1 )
		{
		  System.out.println("Invalid Argument");
		  System.out.println("Restore PINAgent Failed");
		  return;
		}
		
		boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
		
		new Restore(args); 
		
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
			
	}
		
	private boolean unzip()
	{
				
		try
		{
			String zipFileName = Constants.USR_FILE_DIR + Constants.ZIP_FILE;
			
			//check for logging file
			if(false == Utils.validateFile(new File(zipFileName), false))
			{
				System.out.println("Could not find BackUp File");
				return false;
			}
			ZipFile zf = new ZipFile(zipFileName);

           
            
            // Enumerate each entry
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) 
			{
                
                // Get the entry and its name
                ZipEntry zipEntry = (ZipEntry)entries.nextElement();
                String zipEntryName = zipEntry.getName();
              
                
                //check for ensuring duplicate entries are not created in hosts list 
                if(true == fileList.isEmpty())           	
                	fileList.add(zipEntryName);
                else if(false == fileList.contains(zipEntryName))
                	fileList.add(zipEntryName);
                
                
                int lastDirSep;
                if ( (lastDirSep = zipEntryName.lastIndexOf('/')) > 0 ) {
                    String dirName = zipEntryName.substring(0, lastDirSep);
                    (new File(dirName)).mkdirs();
                }
                
                
                if (!zipEntryName.endsWith("/")) {
                    OutputStream out = new FileOutputStream(zipEntryName);
                    InputStream in = zf.getInputStream(zipEntry);
                    
                    byte[] buf = new byte[1024];
                    int len;
                    while((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
    
                    // Close streams
                    out.close();
                    in.close();
                              
                }
            }
			zf.close();
		}
		catch(IOException e)
		{
			
			System.out.println("Could not open the backup file" );
            return false;
		}
		
		return true;
	}
	
	 /**
     * Gets the virtual hosts configured within a Luna SP to System.out
     * creates a vector list
     */
	
	private static boolean getListOfHosts()
	 {
		 
	        try
	        {
	        	hostsList = ServerXmlHandler.getVirtualHostsList();
	        }
	        catch (ServerXmlException e)
	        {
	            System.out.println("Unable to obtain list of virtualHosts");
	            return false;
	        }
	        return true;
	 }
	 

	/*
	 * Processes the command line options
	 */
	private void processCommandLine(String[] args) throws ParseException
    {
        Options commandLineOptions;
        CommandLineParser clp;
       
        commandLineOptions = null;
        clp = null;
       
        commandLineOptions = new Options();
    
        clp = new PosixParser();

        try
        {
            clp.parse(commandLineOptions, args);
        }
        catch(ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();

            formatter.printHelp(APPLICATION_NAME, commandLineOptions, true);

            throw pe;
        }
    }

	/*
	 * Creates a virtual host with the name passed
	 * @param virtualHost: the name of virtual host to be created
	 */
	private boolean createVirtualHost(String virtualHost)
	{
		String configurationFile 					= Constants.WEBAPP_DIR + virtualHost + Constants.CONFIG_DIR +Constants.AGENT_CONFIG_FILE;
		String expression 							= null;
		String SSLSubjectKeyIdentifier 				= null;
		int port 									= 0;
	
		try
		{
			
			XPathReader reader = new XPathReader(configurationFile);
			
			expression = "/PinAgent/SSLCertificate/SubjectKeyIdentifier";
			String SSLSubjectKeyIdentifierXpath = (String)reader.read(expression , XPathConstants.STRING);
			
			if(SSLSubjectKeyIdentifierXpath!= null)
			{
				SSLSubjectKeyIdentifier =  SSLSubjectKeyIdentifierXpath.trim();
			}
			expression = "/PinAgent/Port";
					
			String portXpath = (String)reader.read(expression , XPathConstants.STRING);
			if(portXpath != null)
			{
				port   = Double.valueOf(portXpath).intValue();
			}
			 		
			if (false == addVirtualHost.createVirtualHost(virtualHost, port, SSLSubjectKeyIdentifier))
				return false;
					
		
		}
		catch(Exception e)
		{
			
			System.out.println("Could not create virtual host from the backup file");
			return false;
		}
		return true;
	}
	
	private String getVirtualHostName(String filePath)
	{
		String hostName;
		hostName= null;
		try
		{
		  StringTokenizer tokenizer = new StringTokenizer(filePath, "//"); 
		  Vector<String> tokenList = new Vector<String>();
		  
		  
		

		  String s = new String();
		  while(tokenizer.hasMoreTokens())
		  {
			  tokenList.add(tokenizer.nextToken());
		  }

		  hostName =  (String)tokenList.get(3);
		  
		  return hostName;
		}
		catch(Exception e)
		{
			System.out.println("Could not obtain virtual host name from the backup file");
			return hostName;
		}
	}
	
	

}
