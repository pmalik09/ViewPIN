package com.safenetinc.viewpin.cli;

import java.io.*;
import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.viewpin.cli.Constants;

/**
* class to read the PinAgentVersion file
*/
public class ReadPinAgentVersion {

	static Constants Constants = new Constants();
	
	public ReadPinAgentVersion() {
	}
	
	/**
	* Interface to check the file status,is file is approachable or not
	@return boolean true/false
	*/
	
	public static boolean checkVersionFileStatus(String virtualHostName)
	{
		
		String version_file = Constants.Webapp_Directory +virtualHostName+Constants.Config_Directory
		+ Constants.Agent_Version_File;
    
    	boolean fileStatus = FileHandler.fileExists(Constants.Agent_Version_File, new File(Constants.Webapp_Directory +virtualHostName+
		Constants.Config_Directory));
        	
		if(false == fileStatus)
		{
			return false;
		}
		return true;
		
	}
	
	public static String readPinAgentVersion(String virtualHostName)
	{
		String version_file = Constants.Webapp_Directory +virtualHostName+Constants.Config_Directory
		+ Constants.Agent_Version_File;
		
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