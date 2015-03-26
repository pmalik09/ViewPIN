// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/PrimaryAccountNumber.java 1.1 2008/09/04 10:38:32IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import com.safenetinc.viewpin.simulator.authority.exceptions.InvalidPrimaryAccountNumberException;

/**
 * Class to represent a card's primary account number
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class PrimaryAccountNumber
{
    private static final String PRIMARY_ACCOUNT_NUMBER_REGEX = "^[0-9]{16}$";

    private String              primaryAccountNumber         = null;

    /**
     * Constructor
     * 
     * @param primaryAccountNumber String containing the PAN this class is to store
     * @throws InvalidPrimaryAccountNumberException Thrown if the supplied PAN is invalid
     */
    public PrimaryAccountNumber(String primaryAccountNumber) throws InvalidPrimaryAccountNumberException
    {
        super();

        // Ensure primary account number is valid
        validate(primaryAccountNumber);

        setPrimaryAccountNumber(primaryAccountNumber);
    }

    private void setPrimaryAccountNumber (String primaryAccountNumber)
    {
        this.primaryAccountNumber = primaryAccountNumber;
    }

    /**
     * @return The PAN held by this class
     */
    public String getPrimaryAccountNumber ()
    {
        return this.primaryAccountNumber;
    }

    private void validate (String primaryAccountNumberToValidate) throws InvalidPrimaryAccountNumberException
    {
        // Ensure primary account number is not null
        if (primaryAccountNumberToValidate == null)
        {
            // Primary account number is null
            throw new InvalidPrimaryAccountNumberException("is null");
        }

        // Ensure primary account number is in the correct format
        if (primaryAccountNumberToValidate.matches(PRIMARY_ACCOUNT_NUMBER_REGEX) == false)
        {
            throw new InvalidPrimaryAccountNumberException("invalid format");
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString ()
    {
        return getPrimaryAccountNumber();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals (Object object)
    {
        boolean equals;
        PrimaryAccountNumber primaryAccountNumberToCompareWith;

        equals = false;
        primaryAccountNumberToCompareWith = null;

        if (object == null)
        {
            return equals;
        }

        if (object instanceof PrimaryAccountNumber == false)
        {
            return equals;
        }

        primaryAccountNumberToCompareWith = (PrimaryAccountNumber) object;

        if (primaryAccountNumberToCompareWith.getPrimaryAccountNumber().equalsIgnoreCase(getPrimaryAccountNumber()) == true)
        {
            equals = true;
        }

        return equals;
    }

    /**
     * Not Implemented, returns the constant 42
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode ()
    {
        return 42;
    }
}