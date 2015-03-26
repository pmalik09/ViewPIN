/*
 * Created on 13 Sep 2007
 * 
 * 
 */
package com.safenetinc.luna.fileutils;

import java.io.File;

/**
 * Class to wrap the deletion operation on a file
 * 
 * @author Paul Hampton
 */
public class FileDelete
{
    /**
     * Private constructor, as the only method of this class is static there is no need to ever instantiate
     * this class.
     */
    private FileDelete()
    {
        //Private constructor - no action required
    }

    /**
     * Deletes a file
     * @param fileToDelete The file to delete
     */
    public static void deleteFile (File fileToDelete)
    {
        boolean success = fileToDelete.delete();
        if(!success)
        {
            //nothing to do here
            success=true;
        }
    }
}
