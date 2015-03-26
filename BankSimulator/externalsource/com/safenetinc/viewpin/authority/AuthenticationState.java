// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/authority/AuthenticationState.java 1.1 2008/09/15 13:44:25IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority;

/**
 * Class to hold the authentication state for a BankSimulator user/customer
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class AuthenticationState
{
    private int     failedAuthentications = 0;

    private boolean locked                = false;

    protected AuthenticationState()
    {
        super();
    }

    /**
     * Increment the lockout counter for the user
     */
    public synchronized void registerFailedAuthencation ()
    {
        incrementFailedAuthentications();
    }

    /**
     * Reset the lockout counter for the user
     */
    public synchronized void registerSuccessfulAuthentication ()
    {
        resetFailedAuthentications();
    }

    /**
     * Sets the locked status of the account
     * 
     * @param locked The locked status to set
     */
    public synchronized void setLocked (boolean locked)
    {
        this.locked = locked;
    }

    /**
     * @return Whether or not the account is locked
     */
    public synchronized boolean isLocked ()
    {
        return this.locked;
    }

    /**
     * @return The number of failed authentications
     */
    public synchronized int getFailedAuthentications ()
    {
        return this.failedAuthentications;
    }

    private void incrementFailedAuthentications ()
    {
        setFailedAuthentications(getFailedAuthentications() + 1);
    }

    private void resetFailedAuthentications ()
    {
        setFailedAuthentications(0);
    }

    private void setFailedAuthentications (int failedAuthentications)
    {
        this.failedAuthentications = failedAuthentications;
    }
}