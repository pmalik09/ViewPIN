// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/PrimaryAccountNumber.java 1.2 2008/09/05 16:13:45IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures;

import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidPrimaryAccountNumberException;

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
            throw new InvalidPrimaryAccountNumberException("PAN is null");
        }

        // Ensure primary account number is in the correct format
        if (primaryAccountNumberToValidate.matches(PRIMARY_ACCOUNT_NUMBER_REGEX) == false)
        {
            throw new InvalidPrimaryAccountNumberException("PAN format invalid");
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
}