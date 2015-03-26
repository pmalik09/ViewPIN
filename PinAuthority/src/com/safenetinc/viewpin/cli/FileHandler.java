package com.safenetinc.viewpin.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;



public class FileHandler {

	private static Logger      logger  = Logger.getLogger(FileHandler.class);
	
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
    public static boolean deleteFile (String filename)
    {
		boolean success = false;
		File fileToDelete = new File(filename);
		if (!fileToDelete.exists()) {
			System.out.println("Required File is not present to begin with!");
			return false;
		}
		try {
			success = fileToDelete.delete();
			if (!success){
				logger.error("Deletion failed.");
			}
		}catch(SecurityException e) {
			logger.error("File Deletion Failed error Message: "+ e.getMessage());
			return false;
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
			logger.error("File is missing");
			return exists;
		}
		
		  return exists;
    }

}
