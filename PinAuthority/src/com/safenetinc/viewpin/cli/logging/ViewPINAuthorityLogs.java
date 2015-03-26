package com.safenetinc.viewpin.cli.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.Common;

public class ViewPINAuthorityLogs {

	// TODO Auto-generated method stub
	Logger    logger                             			  = Logger.getLogger(ViewPINAuthorityLogs.class);
	
    final String APPLICATION_NAME              				  = "ViewLogFile";
	
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
		
        new ViewPINAuthorityLogs(args);
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		return;
	}
	/*
	 * Default Constructor
	 */
	public ViewPINAuthorityLogs(String args[])
	{
		
		if(args.length !=0) 
		{
			System.out.println("ViewPINAuthoritylogs, no option is required to delete the log file");
            return;
		}
		if(false == viewLogFile())
		{
        	System.out.println("View Log file failed. ");
        	
        }
		else 
		{
			System.out.println("View Log file successful.");
		}
		return;
	}
	
	/*
	 * This method displays the log file on console 
	 */
	private static boolean viewLogFile()
	{
		try
		{
			FileInputStream input = new FileInputStream(Constants.LOG_FILE);
			FileChannel channel = input.getChannel();
			byte[] buffer = new byte[256 * 1024 * 1024];
			ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
	
			try {
			    for (int length = 0; (length = channel.read(byteBuffer)) != -1;) {
			        System.out.write(buffer, 0, length);
			        byteBuffer.clear();
			    }
			} catch (IOException e) {	
				System.out.println("Could not read file.");
				return false;
			} finally {
			    try {
					input.close();
				} 
			    catch (IOException e)
			    {
			    	return false;
				}
			}
		}
		catch(FileNotFoundException fne)
		{
			System.out.println("Log file not present.");
			return false;
		}
		catch(Exception e)
		{
			System.out.println("Could not read Log File");
			return false;		
		}
		return true;
	}
}
