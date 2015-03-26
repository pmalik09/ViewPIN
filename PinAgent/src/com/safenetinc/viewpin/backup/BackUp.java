package com.safenetinc.viewpin.backup;

import org.apache.log4j.Logger;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.util.Vector;
import java.util.ListIterator;
import java.util.Vector;
import java.io.*;
import java.util.zip.*;


import com.safenetinc.Common;
import com.safenetinc.viewpin.cli.FileHandler;




import com.safenetinc.viewpin.backup.exception.ServerXmlException;

/**
 * This class implements methods for taking a backup of deployed PINAgent/s
 * @author Pmalik
 *
 */

public class BackUp {

	
	private static final String HOSTNAME_COMMAND_LINE_ARG       = "hostname";
	
	private static final String BACKUP_ALL_COMMAND_LINE_ARG     = "all";
	
	private static final String APPLICATION_NAME                = "BackUp";
	
	private static Vector<String> configFileList 				= new Vector<String>();
	
	private static Vector<String> hostsList 					= new Vector<String>();
	
	public BackUp(String virtualHostName)
	{
		//get filenames 
		
		if(false == createFileListFromVirtualHost(virtualHostName))
		{
			System.out.println("BackUp Failed");
			return;
		}
		//zip them
		if(false == zip(configFileList))
		{
			System.out.println("BackUp Failed");
			return;
		}
		
		if(false == createBackUp())
		{
			System.out.println("BackUp Failed");
			return;
		}
		
		System.out.println("BackUp Successful");
		return;
		
	}
	public BackUp()
	{
		//get hostnames 
		if(false == getListOfHosts())
		{
			System.out.println("BackUp Failed");
			return;
		}
		ListIterator<String> iter = hostsList.listIterator();

		//create the config list by iterating over hostList iterator  
		 while (iter.hasNext()) 
		 {
			 String virtualHostName = (String)iter.next();
			 
			 if(false == createFileListFromVirtualHost(virtualHostName))
				{
					System.out.println("BackUp Failed");
					return;
				}
		
		 }
		
		 //zip them
		if(false == zip(configFileList))
		{
			System.out.println("BackUp Failed");
			return;
		}
		if(false == createBackUp())
		{
			System.out.println("BackUp Failed");
			return;
		}
		System.out.println("BackUp Successful");
		return;
		
	}
	public static void main(String[] args)
	{
		// parse the command line arguments
		final Options options = new Options();
      
        OptionBuilder.withArgName(HOSTNAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The name of the virtual host whose configuration is to back Up , for example xyz.com");
        final Option hostName = OptionBuilder.create(HOSTNAME_COMMAND_LINE_ARG);
        
        OptionBuilder.withArgName(BACKUP_ALL_COMMAND_LINE_ARG);
        OptionBuilder.withDescription("Configuration Back up of all the virtual Hosts");
        final Option backUpAll = OptionBuilder.create(BACKUP_ALL_COMMAND_LINE_ARG);
            
        options.addOption(hostName);
        options.addOption(backUpAll);
		
		final CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
        
        try
        {
            cmd = parser.parse(options, args);
            if(cmd.hasOption(HOSTNAME_COMMAND_LINE_ARG)&& cmd.hasOption(BACKUP_ALL_COMMAND_LINE_ARG))
            {
            	System.out.println("You can either backUp a single virtualHost or all the virtualHosts");
            	return;
            }
            if((false == cmd.hasOption(HOSTNAME_COMMAND_LINE_ARG)) && (false == cmd.hasOption(BACKUP_ALL_COMMAND_LINE_ARG)))
            {
            	 final HelpFormatter formatter = new HelpFormatter();
                 formatter.printHelp(APPLICATION_NAME, options);
                 return;
             }
            
                        	
        }
        catch (final ParseException e)
        {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }
		
		boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
		
        if(cmd.hasOption(HOSTNAME_COMMAND_LINE_ARG))
        	new BackUp(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG));
        else if(cmd.hasOption(BACKUP_ALL_COMMAND_LINE_ARG))
		{
			
        	new BackUp();
		}
 
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		
		return;
		
	}
	
	private boolean createFileList()
	{
		
		
		return true;
	}
	
	/*
	 * @param virtuaHostName: name of the virtual host whose
	 * configuration is to be backed up
	 * Method creates a array of configuration files
	 * configuration file and logging file
	 * returns the array
	 */
	private boolean createFileListFromVirtualHost(String virtualHostName)
	{
				
		String configFileName = Constants.WEBAPP_DIR +virtualHostName+Constants.CONFIG_DIR+ Constants.AGENT_CONFIG_FILE;;
		String logFileName =  Constants.WEBAPP_DIR +virtualHostName+Constants.CONFIG_DIR+ Constants.LOGGING_CONFIG_FILE; 
		String versionFileName =  Constants.WEBAPP_DIR +virtualHostName+Constants.CONFIG_DIR+ Constants.AGENT_VERSION_FILE; 
		try
		{
			//check for configuration file
			if(false == Utils.validateFile(new File(configFileName), false))
			{
				System.out.println("Either the virutal host is not installed or the configuration file is missing");
				return false;
			}
			//check for logging file
			if(false == Utils.validateFile(new File(logFileName), false))
			{
				System.out.println("Either the virutal host is not installed or the configuration file is missing");
				return false;
			}
			//check for version file
			if(false == Utils.validateFile(new File(versionFileName), false))
			{
				System.out.println("Either the virutal host is not installed or the version file is missing");
				return false;
			}
			
			//add to FileNamebuffer
			if(false == configFileList.add(configFileName))
			{
				System.out.println("Could not create Zip file list");
				return false;
			}
			if(false == configFileList.add(logFileName))
			{
				System.out.println("Could not create Zip file list");
				return false;
			}
			if(false == configFileList.add(versionFileName))
			{
				System.out.println("Could not create Zip file list");
				return false;
			}
		}
		catch(Exception e)
		{
			System.out.println("Could not create Zip file list");
			return false;
		}
		
		return true;
	}
	 /**
     * Gets the virtual hosts configured within a Luna SP to System.out
     * creates a vector list
     */
	 public static boolean getListOfHosts()
	 {
		 
	        try
	        {
	        	hostsList = ServerXmlHandler.getVirtualHostsList();
	        }
	        catch (ServerXmlException e)
	        {
	            
	        	System.out.println("Unable to obtain list of virtualHosts" + e.getMessage());
	            return false;
	        }
	        return true;
	 }
	 
	
	
	/*
	 * @param fileNames: files to include in the ZIP file
	 * returns true if the zip if created successfully
	 * 
	*/
	private static boolean zip(Vector<String> configFileList)
	{
	   // Create a buffer for reading the files
	    byte[] buf = new byte[1024];
	 
	    
	    try {
	    	
	    	ListIterator<String> iter = configFileList.listIterator();
		   
		      if(configFileList.size() == 0)
		      {
		    	  System.out.println("No virtualHost found");
		          return false;
		      }
		        // Create the ZIP file
		        String outFilename = Constants.USR_FILE_DIR + Constants.ZIP_FILE;
	    	  //String outFilename = "config.zip";
		        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
		    
		        // Compress the files
		        while (iter.hasNext()) {
		        	
		        	String fileName = (String)iter.next();
		            FileInputStream in = new FileInputStream(fileName);
		    
		            // Add ZIP entry to output stream.
		            out.putNextEntry(new ZipEntry(fileName));
		    
		            // Transfer bytes from the file to the ZIP file
		            int len;
		            while ((len = in.read(buf)) > 0) {
		                out.write(buf, 0, len);
		            }
		    
		            // Complete the entry
		            out.closeEntry();
		            in.close();
		        }
		    
		        // Complete the ZIP file
		        out.close();
		    
	    } 
	    catch (IOException e) 
	      {
	    	System.out.println("Could not create backUp files");
	    	//e.printStackTrace();
	    	return false;
	      }
	 return true; 
	}
	
	/**
	 * 
	 * @return
	 */
	private static boolean createBackUp()
	{
		boolean success = false;
		
		try
		{
				//create a backup Direcotry
				success = new File(Constants.USR_FILE_DIR+Constants.BACKUP_DIR).mkdirs();
		
				 if (!success)
				 {
					System.out.println("Could not create directories");
				 	return success;
				 }
				 
					
					// File (or directory) to be moved
			    File file = new File(Constants.USR_FILE_DIR+Constants.ZIP_FILE);
			    
			    // Destination directory
			    File dir = new File(Constants.USR_FILE_DIR+Constants.BACKUP_DIR);
			    
			    // Move file to new directory
			    success = file.renameTo(new File(dir, file.getName()));
			    if (!success) 
			        // File was not successfully moved
				{
			        System.out.println(" File was not successfully moved");
			        return success;
			    }
					//copy("/usr-files/configuration.zip","/usr-files/backup");
					 
				try
			     {
				     File inFolder=new File(Constants.USR_FILE_DIR+Constants.BACKUP_DIR);
				     File outFolder=new File(Constants.USR_FILE_DIR+Constants.BACKUP_FILE);
				     ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));
				     BufferedInputStream in = null;
				
				     byte[] data    = new byte[1000];
				     String files[] = inFolder.list();
				     for (int i=0; i<files.length; i++)
				     {
				    	 in = new BufferedInputStream(new FileInputStream(inFolder.getPath() + "/" + files[i]), 1000);                  
				    	 out.putNextEntry(new ZipEntry(files[i])); 
				    	 
				    	 int count;
					      while((count = in.read(data,0,1000)) != -1)
					      {
					          out.write(data, 0, count);
					     }
					      
					     out.closeEntry();
			      }
				     out.flush();
				     out.close();
				     in.close();
				     
			      }
			      catch(Exception e)
		          {
			    	  System.out.println("Unable to create the backup");
			          return false;
		          } 
			      //now delete the configuration.zip and the backup dir
			      if(false == deleteDirectories(Constants.USR_FILE_DIR+Constants.BACKUP_DIR))
			    	  return false;
			      else if(false == deleteFile(new File(Constants.USR_FILE_DIR+Constants.ZIP_FILE)))
			    	  return false;
			     
		}
		catch(Exception e)
		{  
			
			System.out.println("Could not create backup directory" + e.getMessage());
			return false;
		}
	
			    return true;    
	     
	
		 
	}
	
	
	 /**
     * Deletes a directory and all files and directories it contains. Operates in the same way as rm -rf
     * 
     * @param directoryName The name of the directory to delete
     * @throws IOException Thrown if an error occured during deletion
     */
    public static boolean deleteDirectories (String directoryName) 
    {
        // Ensure the hostname is lowercase
        String lowerCaseDirectoryName = directoryName.toLowerCase();
        
        try
        {
	        boolean success = deleteFileTree(new File(lowerCaseDirectoryName));
	        if (!success)
	           return false;
	        else 
	        	return true;
        }
        catch(Exception e)
        {
        	System.out.println("Unable to delete backup directory");
	        return false;
        }
        
    }

    /**
     * Method to delete a file and directory structure. Navigates recursively down the structure deleting as
     * it goes
     * 
     * @param path The path to delete
     * @return boolean denoting success
     */
    private static boolean deleteFileTree (File path)
    {
        boolean result = false;
       
        try
        {
	        if ((path != null )&&
				 (path.exists()))
	        {
				
					File[] files = path.listFiles();
					
					if ((files !=null) && (files.length != 0))
					{
						for (int i = 0; i < files.length; i++)
						{
							if (files[i].isDirectory())
							{
								deleteFileTree(files[i]);
							}
							else
							{
								result = files[i].delete();
							}
						}
					}
				}
				if (!result)
				{
					// Could not delete the file, this is non critical so ignore
					result = true;
				}
        }
        catch(Exception e)
        {
        	System.out.println("Unable to delete backup directory");
	        return false;
        }
		 if ( (path != null )&&
				 (path.exists()))
			return (path.delete());
		 else
			 return false;
    }
    
    /**
     * Deletes a file
     * @param fileToDelete The file to delete
     */
    public static boolean deleteFile (File fileToDelete)
    {
        boolean success = fileToDelete.delete();
        try
        {
	        if(!success)
	        {
	            //nothing to do here
	            success=true;
	        }
	        return success;
        }
        catch(Exception e)
        {
        	System.out.println("Unable to delete configuration file");
	        return false;
        }
    }
}
