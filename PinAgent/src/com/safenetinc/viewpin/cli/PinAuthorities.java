package com.safenetinc.viewpin.cli;
import java.util.ArrayList;
import com.safenetinc.viewpin.cli.PinAuthority;


/**
 * Class to create an iterator for all
 * the PINAuthorities of PINAgent Configuration file
 * @author Pratibha Malik
 */
public class PinAuthorities {
	
	 private static ArrayList<PinAuthority> pinAuthorities = null;

	    /**
	     * Creates and empty list of card accounts
	     */
	    public PinAuthorities()
	    {
	        super();

	        setPinAuthorities(new ArrayList<PinAuthority>());
	    }

	    /**
	     * Adds a {@link PinAuthority} to the list
	     * 
	     * @param PinAuthority The PinAuthority to add
	     //exception to be thrown
	     */
	    public synchronized void addPinAuthority (PinAuthority pinAuthority) 
	    {
	        
	            getPinAuthorities().add(pinAuthority);
	        
	    }

	    /**
	     * Gets a PinAuthority from the list
	     * 
	     * 
	     * @return The PinAuthority specified by the list
	     */
	    public static PinAuthority getPinAuthority(int index)
	    {
	    	PinAuthority pinAuthority;
	    	PinAuthority nextPinAuthority;

	    	pinAuthority = null;
	    	nextPinAuthority = null;
	    	
/*
	        // Work through each of the existing card accounts
	        for (int i = 0; i < getCardAccounts().size(); i++)
	        {
	            // Get next card account
	            nextCardAccount = getCardAccounts().get(i);

	            // Does this card account have the primary account number that we are looking for?
	            if (nextCardAccount.getPrimaryAccountNumber().equals(primaryAccountNumber) == true)
	            {
	                // We have found the card account with the primary account number we are looking for
	                cardAccount = nextCardAccount;

	                break;
	            }
	        }
*/
	    	pinAuthority = getPinAuthorities().get(index);
	        return pinAuthority;
	    }

	    private static void setPinAuthorities(ArrayList<PinAuthority> newPinAuthorities)
	    {
	        pinAuthorities = newPinAuthorities;
	    }

	    private static ArrayList<PinAuthority> getPinAuthorities()
	    {
	        return pinAuthorities;
	    }
}
