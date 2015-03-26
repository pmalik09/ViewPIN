package com.safenetinc.viewpin.cli;

import java.io.*;
import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.viewpin.cli.ConfigurationConstants;

/**
* class to read the PinAuthorityVersion file
*/
public class ReadPinAuthorityVersion {

	static ConfigurationConstants configurationConstants = new ConfigurationConstants();
	
	public ReadPinAuthorityVersion() {
	}
	
	/**
	* Interface to check the file status,is file is approachable or not
	@return boolean true/false
	*/
	
	public static boolean checkVersionFileStatus()
	{
		String version_file = configurationConstants.WebApp_Directory+configurationConstants.PinAuthority_Directory+configurationConstants.Authority_Version_File;
		boolean fileStatus = FileHandler.fileExists(configurationConstants.Authority_Version_File, new File(configurationConstants.WebApp_Directory));
       	if(false == fileStatus)
		{
			return false;
		}
		return true;
	}
	
	public static String readPinAuthorityVersion()
	{
		String version_file = configurationConstants.WebApp_Directory+configurationConstants.PinAuthority_Directory+configurationConstants.Authority_Version_File;
		
		StringBuilder contents = new StringBuilder();
		String line = null;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(version_file));
			line = input.readLine();
			contents.append(line);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contents.toString();
	}
}