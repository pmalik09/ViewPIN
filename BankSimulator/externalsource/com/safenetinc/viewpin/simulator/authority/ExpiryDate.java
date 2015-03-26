// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/ExpiryDate.java 1.1 2008/09/04 10:38:22IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import com.safenetinc.viewpin.simulator.authority.exceptions.InvalidExpiryDateException;

/**
 * Class to represent the expiry date for a card
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class ExpiryDate
{
    private static final int    MINIMUM_MONTH   = 1;

    private static final int    MAXIMUM_MONTH   = 12;

    private static final int    MINIMUM_YEAR    = 0;

    private static final int    MAXIMUM_YEAR    = 99;

    private static final String FORMATTED_REGEX = "^[0-9]{2}/[0-9]{2}$";

    private int                 month           = 0;

    private int                 year            = 0;

    private String              formatted       = null;

    /**
     * Creates a new instance
     * 
     * @param expiryDate String representing the expiry date in the form mm/yy
     * @throws InvalidExpiryDateException Thrown if an invalid expiry date is specified
     */
    public ExpiryDate(String expiryDate) throws InvalidExpiryDateException
    {
        super();

        String expiryDateMonth;
        String expiryDateYear;

        expiryDateMonth = null;
        expiryDateYear = null;

        // Ensure expiry date is not null
        if (expiryDate == null)
        {
            throw new InvalidExpiryDateException("expiry date is null");
        }

        // Ensure expiry date is formatted correctly
        if (expiryDate.matches(FORMATTED_REGEX) != true)
        {
            throw new InvalidExpiryDateException("invalid format");
        }

        // Split expiry date into month and year components
        expiryDateMonth = expiryDate.substring(0, 2);
        expiryDateYear = expiryDate.substring(3, 5);

        // Initialise expiry date
        initExpiryDate(expiryDateMonth, expiryDateYear);
    }

    /**
     * Creates a new instance
     * 
     * @param month String containing the expiry month in the form MM
     * @param year String containing the expiry year in the form YY
     * @throws InvalidExpiryDateException Thrown if an invalid expiry date is specified
     */
    public ExpiryDate(String month, String year) throws InvalidExpiryDateException
    {
        initExpiryDate(month, year);
    }

    private void initExpiryDate (String monthToSet, String yearToSet) throws InvalidExpiryDateException
    {
        int m;
        int y;

        m = 0;
        y = 0;

        if (monthToSet == null)
        {
            throw new InvalidExpiryDateException("month is null");
        }

        if (yearToSet == null)
        {
            throw new InvalidExpiryDateException("year is null");
        }

        try
        {
            // Parse month
            m = Integer.parseInt(monthToSet);
        }
        catch (NumberFormatException nfe)
        {
            throw new InvalidExpiryDateException("month must be a valid number");
        }

        try
        {
            // Parse year
            y = Integer.parseInt(yearToSet);
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

        format();
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

    private void format ()
    {
        String expiryDate;
        String monthToFormat;
        String yearToFormat;

        expiryDate = null;
        monthToFormat = null;
        yearToFormat = null;

        if (getMonth() < 10)
        {
            monthToFormat = "0" + getMonth();
        }
        else
        {
            monthToFormat = "" + getMonth();
        }

        if (getYear() < 10)
        {
            yearToFormat = "0" + getYear();
        }
        else
        {
            yearToFormat = "" + getYear();
        }

        expiryDate = monthToFormat + "/" + yearToFormat;

        setFormatted(expiryDate);
    }

    /**
     * Returns the formatted expiry date
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString ()
    {
        return getFormatted();
    }

    private void setFormatted (String formatted)
    {
        this.formatted = formatted;
    }

    /**
     * @return The formatted expiry date
     */
    public String getFormatted ()
    {
        return this.formatted;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals (Object o)
    {
        boolean equal;
        ExpiryDate expiryDate;

        equal = false;
        expiryDate = null;

        if (o == null)
        {
            equal = false;

            return equal;
        }

        if (o instanceof ExpiryDate == false)
        {
            equal = false;

            return equal;
        }

        expiryDate = (ExpiryDate) o;

        if (expiryDate.getMonth() == getMonth() && expiryDate.getYear() == getYear())
        {
            equal = true;
        }
        else
        {
            equal = false;
        }

        return equal;
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