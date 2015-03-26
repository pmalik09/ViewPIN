// $Id: PinAgent/src/com/safenetinc/viewpin/agent/otp/PadEncryptedPin.java 1.1 2008/09/04 10:46:18IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.otp;

/**
 * Class to represent a PIN encrypted with a one time pad
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class PadEncryptedPin
{
    private String padEncryptedPin;

    private String padKeyCookieName;

    /**
     * Constructor
     * 
     * @param padEncryptedPin The encrypted PIN
     * @param padKeyCookieName The name of the cookie the pad key is to be stored in
     */
    public PadEncryptedPin(String padEncryptedPin, String padKeyCookieName)
    {
        super();

        setPadEncryptedPin(padEncryptedPin);
        setPadKeyCookieName(padKeyCookieName);
    }

    private void setPadEncryptedPin (String padEncryptedPin)
    {
        this.padEncryptedPin = padEncryptedPin;
    }

    /**
     * @return The encrypted PIN
     */
    public String getPadEncryptedPin ()
    {
        return this.padEncryptedPin;
    }

    private void setPadKeyCookieName (String padKeyCookieName)
    {
        this.padKeyCookieName = padKeyCookieName;
    }

    /**
     * @return The name of the cookie which holds the pad key
     */
    public String getPadKeyCookieName ()
    {
        return this.padKeyCookieName;
    }
}