package com.safenetinc.viewpin.authority;

import java.util.ArrayList;
import java.util.Iterator;
import com.safenetinc.viewpin.authority.Pin;
import com.safenetinc.viewpin.authority.exceptions.InvalidPINBlockFormatException;
import com.safenetinc.viewpin.authority.exceptions.InvalidPinException;
import com.safenetinc.viewpin.authority.exceptions.InvalidPrimaryAccountNumberException;

/**
*This class holds the detail of cardHolder
* @author Pratibha Malik
*
*/
public class CardHolderDetails {

	 private String  			primaryAccountNumber                 = null;

     private String  			cardHolderVerificationValue          = null;

     private String         	expiryDate                           = null;

	 private boolean            expiryDateAuthenticationToBeEnforced = false;

	 private ArrayList<String>    pinNumbers                         = null;
	 
	 private int inputPINBlockFormat								  = 0;
	 
	 private int outputPINBlockFormat								  = 0;
	
	 public CardHolderDetails()
	 {
		 super();
		 this.cardHolderVerificationValue = null;
		 this.expiryDate = null;
		 this.expiryDateAuthenticationToBeEnforced =  false;
		 this.primaryAccountNumber = null;
		 this.pinNumbers = null;
		 this.expiryDateAuthenticationToBeEnforced = true;
		 this.inputPINBlockFormat = 0;
		 this.outputPINBlockFormat = 0;
		 
	 }

	  /**
     * Constructor
     * 
     * @param primaryAccountNumber The PAN for the customer's card
     * @param cardHolderVerificationValue The CVV for the customer's card
     * @param expiryDate The expiry date for the customer's card
     * @param expiryDateAuthenticationToBeEnforced boolean denoting whether expiry date checking (along with
     *        CVV) is being enforced
     */
	  public CardHolderDetails(String primaryAccountNumber,ArrayList<String> PINList, String cardHolderVerificationValue, String expiryDate,
			 				int inputPINBlockFormat,int outputPINBlockFormat, boolean expiryDateAuthenticationToBeEnforced
			 				)
	  {
		  setPrimaryAccountNumber(primaryAccountNumber);
          setCardHolderVerificationValue(cardHolderVerificationValue);
          setExpiryDate(expiryDate);
          setExpiryDateAuthenticationToBeEnforced(expiryDateAuthenticationToBeEnforced);
          setInputPINBlockFormat(inputPINBlockFormat);
          setOutputPINBlockFormat(outputPINBlockFormat);
          setPinNumbers(PINList);
	  }
	 
	  /**
	   * Adds a PIN to the card
	   * 
	   * @param pin The PIN to add
	   */
	  	public void addPin (Pin pin)
	  	{
	  		getPINNumbers(this.getPinNumbers()).add(pin);
	  	}
	   public void setPrimaryAccountNumber (String primaryAccountNumber)
	    {
	        this.primaryAccountNumber = primaryAccountNumber;
	    }

	    /**
	     * @return The PAN for the card
	     */
	    public String getPrimaryAccountNumber ()
	    {
	        return this.primaryAccountNumber;
	    }

	    public void setCardHolderVerificationValue (String cardHolderVerificationValue)
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

	    public void setExpiryDate (String expiryDate)
	    {
	        this.expiryDate = expiryDate;
	    }

	    /**
	     * @return The {@link ExpiryDate} for the card
	     */
	    public String getExpiryDate ()
	    {
	        return this.expiryDate;
	    }

	    public void setExpiryDateAuthenticationToBeEnforced (boolean expiryDateAuthenticationToBeEnforced)
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
	    
	    public void setPinNumbers (ArrayList<String> pinNumbers)
	    {
	        this.pinNumbers = pinNumbers;
	    }

	    public ArrayList<String> getPinNumbers ()
	    {
	        return this.pinNumbers;
	    }

	    
	    public ArrayList<Pin> getPINNumbers (ArrayList<String> PinNumber)
	    {
	    	ArrayList<Pin> PINList = new ArrayList<Pin>();
	    	
	    	for(int i=0;i<PinNumber.size();i++)
	    	{
	    		Pin pin = null;
				try
				{
							pin = new Pin(PinNumber.get(i));
				}
				catch(InvalidPinException e)
	    		{
	    			return PINList;
	    		}
		
				
	    		PINList.add(pin);
	    	}
	        return PINList;
	    }

	    /**
	     * @return The PINs for the card
	     */
	    /*
	    public Iterator<Pin> getPins ()
	    {
	        return getPinNumbers().iterator();
	    }
	    */
	    public void setInputPINBlockFormat(int PINBlockFormat)
	    {
	        this.inputPINBlockFormat = PINBlockFormat;
	    }

	    public int getInputPINBlockFormat()
	    {
	        return this.inputPINBlockFormat;
	    }
	    
	    public void setOutputPINBlockFormat(int PINBlockFormat)
	    {
	        this.outputPINBlockFormat = PINBlockFormat;
	    }

	    public int getOutputPINBlockFormat()
	    {
	        return this.outputPINBlockFormat;
	    }
	    
	    public boolean checkPINBlockFormat(int PINBlockFormat)
	    {
	    	if(PINBlockFormat<0 || PINBlockFormat>4)
	    		return false;
	    	else
	    		return true;
	    }
}

