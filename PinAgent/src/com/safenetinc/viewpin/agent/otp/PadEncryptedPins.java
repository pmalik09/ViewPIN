// $Id: PinAgent/src/com/safenetinc/viewpin/agent/otp/PadEncryptedPins.java 1.1 2008/09/04 10:46:20IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.otp;

import java.util.ArrayList;


/**
 * Class to hold a list of {@link PadEncryptedPin} objects
 * @author Stuart Horler
 *
 *
 */
public class PadEncryptedPins 
{
	private ArrayList<PadEncryptedPin> padEncryptedPins = null;
	private String padKeyCookieDomain = null;
	
    /**
     * Constructor
     * @param padKeyCookieDomain The domain of the cookies that hold the one time pad keys
     */
    public PadEncryptedPins(String padKeyCookieDomain)
    {
        super();
        
        setPadEncryptedPins(new ArrayList<PadEncryptedPin>());
        
        setPadKeyCookieDomain(padKeyCookieDomain);
    }
    
    /**
     * Adds a {@link PadEncryptedPin} object to the list
     * @param padEncryptedPin The {@link PadEncryptedPin} to add
     */
    public void add(PadEncryptedPin padEncryptedPin)
    {
    	getPadEncryptedPins().add(padEncryptedPin);
    }
    
    /**
     * @return The number of {@link PadEncryptedPin} objects held by this class
     */
    public int getTotalPinNumbers()
    {
        return getPadEncryptedPins().size();
    }
    
    /**
     * Returns a {@link PadEncryptedPin} object from the given index
     * @param index The index to retrieve from
     * @return The {@link PadEncryptedPin}
     */
    public PadEncryptedPin getPadEncryptedPin(int index)
    {
    	return this.padEncryptedPins.get(index);
    }
    
    private void setPadEncryptedPins(ArrayList<PadEncryptedPin> padEncryptedPins)
    {
    	this.padEncryptedPins = padEncryptedPins;
    }
    
    private ArrayList<PadEncryptedPin> getPadEncryptedPins()
    {
        return this.padEncryptedPins;
    }
    
    private void setPadKeyCookieDomain(String padKeyCookieDomain)
    {
        this.padKeyCookieDomain = padKeyCookieDomain;
    }
    
    /**
     * @return The domain of the cookies that hold the one time pad keys
     */
    public String getPadKeyCookieDomain()
    {
    	return this.padKeyCookieDomain;
    }
}