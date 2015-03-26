// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/Pin.java 1.2 2008/11/14 12:05:19IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority;

import com.safenetinc.viewpin.authority.exceptions.InvalidPinException;

/**
 * Class to hold a PIN
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class Pin
{
    private static final String VALID_PIN_REGEX = "^[0-9]{4,12}$";

    private String              pin             = null;

    /**
     * Constructor
     * 
     * @param pin String containing the PIN
     * @throws InvalidPinException Thrown if an invalid PIN is specified
     */
    public Pin(String pin) throws InvalidPinException
    {
        super();

        if (pin == null)
        {
            throw new InvalidPinException("is null");
        }
/*
        if (isPinValid(pin) == false)
        {
            throw new InvalidPinException("invalid format");
        }
*/
        setPin(pin);
    }

    private boolean isPinValid (String pinToValidate)
    {
        boolean valid;

        valid = false;

        if (pinToValidate.matches(VALID_PIN_REGEX) == true)
        {
            valid = true;
        }

        return valid;
    }

    private void setPin (String pin)
    {
        this.pin = pin;
    }

    /**
     * @return The PIN held by this class
     */
    public String getPin ()
    {
        return this.pin;
    }
}
