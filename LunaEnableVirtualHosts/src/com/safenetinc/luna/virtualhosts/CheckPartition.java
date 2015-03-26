package com.safenetinc.luna.virtualhosts;

import com.safenetinc.luna.LunaTokenManager;
/**
 * Class to check Partition is logged in or not
 * @author Pratibha Malik
 */
public class CheckPartition {

	private CheckPartition()
	{
		super();
	}
	
	
	  /**
     * Method to assess whether the LunaSP is logged into its partition
     * @return boolean denoting logged in status
     */
	public static boolean isPartitionLoggedIn()
    {
        boolean rc;
        LunaTokenManager ltm;
        
        rc = false;
        ltm = null;
        
        // Get instance of LunaTokenManager
        ltm = LunaTokenManager.getInstance();
        
        
        // Is partition logged in?
        if(ltm.isLoggedIn() == true)
        {
            // Partition is logged in
            rc = true;
        }
        else
        {
            // Partition is not logged in
            rc = false;
        }
        
        return rc;
    }
	
}
