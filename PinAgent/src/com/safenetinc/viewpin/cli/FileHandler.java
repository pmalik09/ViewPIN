package com.safenetinc.viewpin.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.safenetinc.viewpin.cli.logging.Constants;


public class FileHandler {

		
	public FileHandler()
    {
        //Private constructor - no action required
    }
	
	 /**
     * Copies a file from one location to another
     * @param source The file's source location
     * @param destination The destination location for the copy
     * @throws IOException Thrown if the copy fails
     * @throws FileNotFoundException Thrown if the source file or destination directory do not exist
     */
    public static void copyFile (File source, File destination) throws IOException, FileNotFoundException
    {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    
    /**
     * Deletes a file
     * @param fileToDelete The file to delete
     */
    public static boolean deleteFile (File fileToDelete)
    {
        boolean success = fileToDelete.delete();
        if(!success)
        {
            //nothing to do here
            success=true;
        }
        return success;
    }
    /**
     * Checks whether the file exists in a directory
     * @param filenae The file to check
     * @param dir to check whether the provided dir exists
     */
    public static boolean fileExists (String filename, File dir) {
        boolean exists = false;
		try
		{
			if (new File (dir, filename).exists ()) {
				exists = true;
			} else {
				File[] subdirs = dir.listFiles ();
	 
				int i = 0;
				int n = (subdirs == null) ? 0 : subdirs.length;
	 
				while ((i < n) && ! exists) {
					File subdir = subdirs[i];
	 
					if (subdir.isDirectory ()) {
						exists = fileExists (filename, subdir);
					}
	 
					i ++;
				}
			}
		}
		catch (Throwable t)
		{
			System.out.println("Either the virtual host does not exist or File is missing");
			return exists;
		}
		
		  return exists;
    }

    public static boolean checkFileStatus(String virtualHostName)
	 {
		
		//reader = new XPathReader("E:\\viewPINDev\\PinAgent\\agentconfiguration.xml");
		
		String configFile = Constants.Webapp_Directory +virtualHostName+Constants.Config_Directory+ Constants.LOG_FILE;
 
 		boolean fileStatus = fileExists(Constants.Logging_Config_File, new File(Constants.Webapp_Directory +virtualHostName+Constants.Config_Directory));
     	
		if(false == fileStatus)
		{
			return false;
		}
		
		
		return true;
		
	 }
}
