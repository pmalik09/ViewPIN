/*
 * Created on Jul 18, 2007
 */
package com.safenetinc.luna.fileutils;

import java.io.File;
import java.io.IOException;

/**
 * Helper class to handle creation and deletion of directories used in conjunction with Tomcat virtual hosting
 * 
 * @author Paul Hampton
 */
public class DirectoryHandler
{

    private static final String TOMCAT_WEBAPPS_PATH   = "/usr/tomcat/webapps";

    private static final String ROOT_DIRECTORY_MARKER = "ROOT";

    /**
     * Private constructor, all methods in this class are static so there is no need to instantiate this
     * class.
     */
    private DirectoryHandler()
    {
        // Private - no action required
    }

    /**
     * Method to create a virtual host root directory inside the tomcat webapps directory. Creates a directory
     * with the name specified and a subdirectory with the name ROOT denoting that the enclosing directory is
     * a virtual host root.
     * 
     * @param directoryName The name of the directory to create
     * @throws IOException Thrown if it was not possible to create the directories
     */
   // public static void createVirtualHostRootDirectories (String directoryName) throws IOException
    public static void createVirtualHostRootDirectories (String directoryName) 
    {
        // Ensure the hostname is lowercase
        String lowerCaseDirectoryName = directoryName.toLowerCase();
		try
		{
        boolean success = new File(TOMCAT_WEBAPPS_PATH + "/" + lowerCaseDirectoryName + "/" + ROOT_DIRECTORY_MARKER).mkdirs();
       /* if (!success)
		{
			//IOException.printStackTrace();
            throw new IOException("Could not create directories");
			}*/
		}
		catch(Exception ioe)
		{
			ioe.printStackTrace();
		}
    }

    /**
     * Deletes a directory and all files and directories it contains. Operates in the same way as rm -rf
     * 
     * @param directoryName The name of the directory to delete
     * @throws IOException Thrown if an error occured during deletion
     */
    public static void deleteDirectories (String directoryName) throws IOException
    {
        // Ensure the hostname is lowercase
        String lowerCaseDirectoryName = directoryName.toLowerCase();

        boolean success = deleteFileTree(new File(TOMCAT_WEBAPPS_PATH + "/" + lowerCaseDirectoryName));
        if (!success)
            throw new IOException("Could not delete directories");
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
        if (path.exists())
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
        return (path.delete());
    }
}
