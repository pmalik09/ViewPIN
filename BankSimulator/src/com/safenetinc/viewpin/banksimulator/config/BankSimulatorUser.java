/*
 * Created on Aug 2, 2005
 * 
 * 
 */

package com.safenetinc.viewpin.banksimulator.config;

import com.safenetinc.viewpin.authority.PrimaryAccountNumber;
import com.safenetinc.viewpin.authority.CardHolderDetails;

/**
 * Class to represent a user/customer within the bank simulator system. In a real banking organisation this
 * class would be a relational table
 * 
 * @author Paul Hampton
 */
public class BankSimulatorUser
{
    private String firstname;
    private String id;
    private String password;
    private String surname;
   // private CardHolderDetails cardHolderDetails = null;
    private PrimaryAccountNumber primaryAccountNumber = null;
  

    /**
     * Creates a {@link BankSimulatorUser} object with a single associated PIN
     * 
     * @param id The ID of the user
     * @param firstname The user's firstname
     * @param surname The user's surname
     * @param password The user's password
     * @param primaryAccountNumber The user's card number
     */
   // public BankSimulatorUser(String id, String firstname, String surname, String password, PrimaryAccountNumber primaryAccountNumber,CardHolderDetails cardHolderDetails)
    public BankSimulatorUser(String id, String firstname, String surname, String password, PrimaryAccountNumber primaryAccountNumber)
    {
        super();
    
        setId(id);
        setFirstname(firstname);
        setSurname(surname);
        setPassword(password);
      //  setCardHolderDetails(cardHolderDetails);
        setPrimaryAccountNumber(primaryAccountNumber);
     
    }

     /**
     * @return Returns the firstname.
     */
    public String getFirstname ()
    {
        return this.firstname;
    }

    /**
     * @return Returns the id.
     */
    public String getId ()
    {
        return this.id;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword ()
    {
        return this.password;
    }
    
    /**
     * @return Returns the surname.
     */
    public String getSurname ()
    {
        return this.surname;
    }

    /**
     * @param firstname The firstname to set.
     */
    private void setFirstname (String firstname)
    {
        this.firstname = firstname;
    }

    /**
     * @param id The id to set.
     */
    private void setId (String id)
    {
        this.id = id;
    }

    /**
     * @param password The password to set.
     */
    private void setPassword (String password)
    {
        this.password = password;
    }

    /**
     * @param surname The surname to set.
     */
    private void setSurname (String surname)
    {
        this.surname = surname;
    }

    private void setPrimaryAccountNumber(PrimaryAccountNumber primaryAccountNumber) 
	{
		this.primaryAccountNumber = primaryAccountNumber;
	}
	
	/**
     * 
	 * @return The Primary Account number for the user
	 */
	public PrimaryAccountNumber getPrimaryAccountNumber() 
	{
		return this.primaryAccountNumber;
	}
	/*
	private void setCardHolderDetails(CardHolderDetails cardHolderDetails) 
	{
		this.cardHolderDetails = cardHolderDetails;
	}
	*/
	/**
     * 
	 * @return The CardHolderDetails for the user
	 */
	 /*
	public CardHolderDetails getCardHolderDetails() 
	{
		return this.cardHolderDetails;
	}
    */
    
}
