// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/CardAccount.java 1.1 2008/09/04 10:38:18IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to represent a customer's account holding with our example bank
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class CardAccount
{
    private PrimaryAccountNumber primaryAccountNumber                 = null;

    private String               cardHolderVerificationValue          = null;

    private ExpiryDate           expiryDate                           = null;

    private ArrayList<Pin>       pinNumbers                           = null;

    private boolean              expiryDateAuthenticationToBeEnforced = false;

    private AuthenticationState  authenticationState                  = null;

    /**
     * Constructor
     * 
     * @param primaryAccountNumber The PAN for the customer's card
     * @param cardHolderVerificationValue The CVV for the customer's card
     * @param expiryDate The expiry date for the customer's card
     * @param expiryDateAuthenticationToBeEnforced boolean denoting whether expiry date checking (along with
     *        CVV) is being enforced
     */
    public CardAccount(PrimaryAccountNumber primaryAccountNumber, String cardHolderVerificationValue, ExpiryDate expiryDate, boolean expiryDateAuthenticationToBeEnforced)
    {
        setPrimaryAccountNumber(primaryAccountNumber);
        setCardHolderVerificationValue(cardHolderVerificationValue);
        setExpiryDate(expiryDate);
        setExpiryDateAuthenticationToBeEnforced(expiryDateAuthenticationToBeEnforced);

        setPinNumbers(new ArrayList<Pin>());

        setAuthenticationState(new AuthenticationState());
    }

    /**
     * Adds a PIN to the card
     * 
     * @param pin The PIN to add
     */
    public void addPin (Pin pin)
    {
        getPinNumbers().add(pin);
    }

    private void setPrimaryAccountNumber (PrimaryAccountNumber primaryAccountNumber)
    {
        this.primaryAccountNumber = primaryAccountNumber;
    }

    /**
     * @return The PAN for the card
     */
    public PrimaryAccountNumber getPrimaryAccountNumber ()
    {
        return this.primaryAccountNumber;
    }

    private void setCardHolderVerificationValue (String cardHolderVerificationValue)
    {
        this.cardHolderVerificationValue = cardHolderVerificationValue;
    }

    /**
     * @return The CVV value for the card
     */
    public String getCardHolderVerificationValue ()
    {
        return this.cardHolderVerificationValue;
    }

    private void setPinNumbers (ArrayList<Pin> pinNumbers)
    {
        this.pinNumbers = pinNumbers;
    }

    private ArrayList<Pin> getPinNumbers ()
    {
        return this.pinNumbers;
    }

    /**
     * @return The PINs for the card
     */
    public Iterator<Pin> getPins ()
    {
        return getPinNumbers().iterator();
    }

    private void setExpiryDate (ExpiryDate expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    /**
     * @return The {@link ExpiryDate} for the card
     */
    public ExpiryDate getExpiryDate ()
    {
        return this.expiryDate;
    }

    private void setExpiryDateAuthenticationToBeEnforced (boolean expiryDateAuthenticationToBeEnforced)
    {
        this.expiryDateAuthenticationToBeEnforced = expiryDateAuthenticationToBeEnforced;
    }

    /**
     * @return Whether Expiry Date checking is to be enforced along with CVV checking
     */
    public boolean isExpiryDateAuthenticationToBeEnforced ()
    {
        return this.expiryDateAuthenticationToBeEnforced;
    }

    private void setAuthenticationState (AuthenticationState authenticationState)
    {
        this.authenticationState = authenticationState;
    }

    /**
     * @return The current authentication state for the user
     */
    public AuthenticationState getAuthenticationState ()
    {
        return this.authenticationState;
    }

}