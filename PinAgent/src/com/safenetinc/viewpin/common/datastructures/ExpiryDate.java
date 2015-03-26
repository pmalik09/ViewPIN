// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/ExpiryDate.java 1.1 2008/09/04 10:46:49IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures;

import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidExpiryDateException;

/**
 * Class to represent the expiry date for a card
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class ExpiryDate
{
    private static final int MINIMUM_MONTH = 1;

    private static final int MAXIMUM_MONTH = 12;

    private static final int MINIMUM_YEAR  = 0;

    private static final int MAXIMUM_YEAR  = 99;

    private int              month         = 0;

    private int              year          = 0;

    /**
     * Creates a new instance
     * 
     * @param month String representing the month in the form mm
     * @param year String representing the year in the form yy
     * @throws InvalidExpiryDateException Thrown if an invalid expiry date is specified
     */
    public ExpiryDate(String month, String year) throws InvalidExpiryDateException
    {
        super();

        int m;
        int y;

        m = 0;
        y = 0;

        if (month == null)
        {
            throw new InvalidExpiryDateException("month is null");
        }

        if (year == null)
        {
            throw new InvalidExpiryDateException("year is null");
        }

        try
        {
            // Parse month
            m = Integer.parseInt(month);
        }
        catch (NumberFormatException nfe)
        {
            throw new InvalidExpiryDateException("month must be a valid number");
        }

        try
        {
            // Parse year
            y = Integer.parseInt(year);
        }
        catch (NumberFormatException nfe)
        {
            throw new InvalidExpiryDateException("year must be a valid number");
        }

        // Validate month
        if (m < MINIMUM_MONTH || m > MAXIMUM_MONTH)
        {
            throw new InvalidExpiryDateException("invalid month");
        }

        // Validate year
        if (y < MINIMUM_YEAR || y > MAXIMUM_YEAR)
        {
            throw new InvalidExpiryDateException("invalid year");
        }

        setMonth(m);
        setYear(y);
    }

    private void setMonth (int month)
    {
        this.month = month;
    }

    /**
     * @return The expiry month
     */
    public int getMonth ()
    {
        return this.month;
    }

    private void setYear (int year)
    {
        this.year = year;
    }

    /**
     * @return The expiry year
     */
    public int getYear ()
    {
        return this.year;
    }
}