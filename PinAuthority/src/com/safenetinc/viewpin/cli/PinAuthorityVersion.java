package com.safenetinc.viewpin.cli;

import java.lang.Number;

import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.safenetinc.viewpin.cli.ConfigurationConstants;
import com.safenetinc.viewpin.cli.ReadPinAuthorityVersion;
import com.safenetinc.viewpin.cli.FileHandler;
                                              

/**
* class to edit the PinAuthorityConfiguration Elements
*/

public class PinAuthorityVersion {
	
	ConfigurationConstants configurationConstants = new ConfigurationConstants();
	ReadPinAuthorityVersion readPinAuthorityVersion = new ReadPinAuthorityVersion();
	private static final String APPLICATION_NAME = "GetPinAuthorityVersion";
	
	/**
	* main function to interact with user
	*/
	public static void main(String[] args)
	{
		if(args.length != 0)
		{
			System.out.println("GetPinAuthorityVersion,no option is required to Displaying Version");
            return;
		}
		if( false == ReadPinAuthorityVersion.checkVersionFileStatus())
        {
        	System.out.println("PINAuthority is not Installed on System.View PINAuthority Version Failed.");
        	return;
        }
		
		new PinAuthorityVersion(args);
	}
	
	
	public PinAuthorityVersion(String[] args) 
	{	
		getVersionInfo(args); 
		System.out.println("View PinAuthority Version successful.");
	}
	
	public boolean getVersionInfo(String[] args) 
	{
		System.out.println(configurationConstants.PinAuthorityVersion + " : " + 
		readPinAuthorityVersion.readPinAuthorityVersion());
		return true;
	}
	
}