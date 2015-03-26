/*
 * Created on 3 Sep 2007
 * 
 * 
 */
package com.safenetinc.viewpin.restore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Class to handle copying files
 * 
 * @author Paul Hampton
 */
public class FileCopy
{
    /**
     * Private constructor. As the only method is static there is no need to instantiate this class.
     */
    public FileCopy()
    {
        //Private nothing to do here
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
    
    
    public void copy(String filename, String targetDir) throws IOException
    {
    	try
    	{   		
    	
    		byte b[];
            File fileDest = new File(targetDir + filename);
         
            InputStream stream = getClass().getResourceAsStream(filename);
                    FileOutputStream output = new FileOutputStream(fileDest);

                 
         
                    // Transfer bytes from in to out
                    
                    byte[] buf = new byte[1024];
                  
                    
                    int len;
                    while ((len = stream.read(buf)) > 0)
                    {
                    	   
                    	output.write(buf, 0, len);
                    	   
                    }
                    stream.close();
                   
                    output.close();
                   
    	}
    	catch(Throwable t)
    	{
    		System.out.println("copy failed");
    	}
             

    }

}
