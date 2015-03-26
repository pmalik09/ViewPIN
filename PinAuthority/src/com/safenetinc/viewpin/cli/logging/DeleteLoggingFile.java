package com.safenetinc.viewpin.cli.logging;

import java.io.*;

import org.apache.log4j.Logger;
import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.Common;

public class DeleteLoggingFile {

	Logger    logger                             			  = Logger.getLogger(DeleteLoggingFile.class);
	
    final String APPLICATION_NAME              				  = "DeleteLogFile";
	
	/*
	 * Main  function to view the delete the log file of PIN Authority
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
		
        new DeleteLoggingFile(args);
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		return;
	}
	/*
	 * Default Constructor
	 */
	public DeleteLoggingFile(String args[])
	{
		if(args.length !=0) 
		{
			System.out.println("DeleteLogFile,no option is required to delete the log file");
            return;
		}
		if(false == deleteLogFile())
		{
        	System.out.println("Log file deletion failed.");
        	
        }
		else 
		{
			System.out.println("Log File Deleted");
		}
		return;
	}
	/*
	 * This method deletes the log file generated 
	 */
	private static boolean deleteLogFile()
	{
		 
		  if (true == FileHandler.deleteFile(Constants.LOG_FILE))		  
		  	return true;
		  else
		    return false;
		  
	}
}
