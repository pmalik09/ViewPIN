//$Id: 
package com.safenetinc.viewpin.authority;

import java.util.Random;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;
import com.safenetinc.viewpin.authority.exceptions.*;

/**
 * Class to hold constants for PIN block formation
 * 
 * @author Anurag Chaudhary
 * 
 * 
 */
abstract class PINBlockConstants
{
	// Account number field
	public static final int ANF_LENGTH = 12;
	// PIN block string length
	public static final int PB_STR_LENGTH = 16;
	// PIN block length
	public static final int PB_NUM_LENGTH = 8;
	
	// Enable some debug info
	public static final boolean DEBUG = false;
	
	// PIN Block formats
	public static final int ISO_0 = 0;
	public static final int ISO_1 = 1;
	public static final int ISO_2 = 2;
	public static final int ISO_3 = 3;
	
	private PINBlockConstants()
	{
		super();
	}
}

/**
 * Class to create PIN block from PIN and PAN(Optional) 
 * 
 * @author Anurag Chaudhary
 * 
 * 
 */
public class PINBlock
{
	private char[]	anb = null;
	private int		pinLength = 0;
	private char[]	pinString = null;
	private static Logger logger = Logger.getLogger(PINBlock.class);
	private static Random generator = new Random( 19580428 );;

	private static Logger getLogger ()
	{
		return logger;
	}
	  
	static private byte[] hexstringtobyte(String s)
	{
		byte[] txtInByte = Hex.decode(s.getBytes());

		return txtInByte;
	}
	
	/**
	 * 
	 * @param format holds PIN block format to create
	 * @return
	 */
	private byte[] CreatePINBlock(int format)
	{
		byte[] pb = new byte [PINBlockConstants.PB_NUM_LENGTH];
		byte[] pinN = null;
		byte[] anbN = null;
		int i;

		// 1st digit denotes PIN block format
		this.pinString[0] = (Integer.toHexString(format)).charAt(0);
		// 2nd digit denotes PIN length
		this.pinString[1] = (Integer.toHexString(pinLength)).charAt(0);
		
		switch (format) {
		case 0:
			// Padding all 'F'
			for (i = pinLength + 2; i < PINBlockConstants.PB_STR_LENGTH ; i++)
				this.pinString[i] = 'F';
			pinN = hexstringtobyte(String.copyValueOf(pinString));
			anbN = hexstringtobyte(String.copyValueOf(anb));
			for (i = 0; i < PINBlockConstants.PB_NUM_LENGTH; i++) {
				pb[i] = (byte)(pinN[i] ^ anbN[i]);
			}
			break;
		case 1:
			// Padding - 0 to F
			for (i = pinLength + 2; i < PINBlockConstants.PB_STR_LENGTH ; i++) {
				byte padByte =  (byte)(generator.nextInt(16));
				this.pinString[i] = (Integer.toHexString(padByte)).charAt(0);
			}
			pinN = hexstringtobyte(String.copyValueOf(pinString));
			for (i = 0; i < PINBlockConstants.PB_NUM_LENGTH; i++) {
				pb[i] = pinN[i];
			}
			break;
		case 2:
			// Padding all 'F'
			for (i = pinLength + 2; i < PINBlockConstants.PB_STR_LENGTH ; i++)
				this.pinString[i] = 'F';
			pinN = hexstringtobyte(String.copyValueOf(pinString));
			for (i = 0; i < PINBlockConstants.PB_NUM_LENGTH; i++) {
				pb[i] = pinN[i];
			}
			break;
		case 3:
			// Padding - 'A' to 'F'
			for (i = pinLength + 2; i < PINBlockConstants.PB_STR_LENGTH ; i++) {
				byte padByte =  (byte)(generator.nextInt(6) + 10);
				this.pinString[i] = (Integer.toHexString(padByte)).charAt(0);
			}
			pinN = hexstringtobyte(String.copyValueOf(pinString));
			anbN = hexstringtobyte(String.copyValueOf(anb));
			for (i = 0; i < PINBlockConstants.PB_NUM_LENGTH; i++) {
				pb[i] = (byte)(pinN[i] ^ anbN[i]);
			}
			break;
		default:
			break;
		}

		if (PINBlockConstants.DEBUG) {
			dumphex(pinN);
			dumphex(anbN);
			dumphex(pb);
		}
		
		return pb;
	}
	
	private void dumphex(byte[] array)
	{
		if (array != null)
			System.out.println(new String(Hex.encode(array)));
	}
	
	static private char[] FormANBFromPAN(String pan)
	{
		char[] localANB = new char [PINBlockConstants.ANF_LENGTH + 4];
		int i , j;

		for (i = 0; i < PINBlockConstants.PB_STR_LENGTH; i++)
			localANB[i] = '0';

		for (i = (PINBlockConstants.PB_STR_LENGTH), j = pan.length() - 1; i > 4; i--, j--) {
			if (j > 0)
				localANB[i - 1] = pan.charAt(j - 1);
			else
				localANB[i - 1] = '0';

		}
	
		return localANB;
	}
	
    /**
     * Constructor
     * @param pin Object holding PIN
     * @param pan Object holding Primary Account Number
     */
	
	public PINBlock(Pin pin, PrimaryAccountNumber pan)											
	{
		super();
		this.pinString = new char[PINBlockConstants.PB_STR_LENGTH];
		String pinS = pin.getPin();
		String panS = pan.getPrimaryAccountNumber();

		this.pinLength = pinS.length();

		this.anb = FormANBFromPAN(panS);
		pinS.getChars(0, this.pinLength, this.pinString, 2);
	}

	/**
	 * Constructor (if PAN is not available)
	 * @param pin
	 */
	public PINBlock(Pin pin)
	{
		super();
		//initializing pinString
		
		this.pinString = new char[PINBlockConstants.PB_STR_LENGTH];
		String pinS = pin.getPin();
		this.pinLength = pinS.length();
		this.anb = FormANBFromPAN("");

		pinS.getChars(0, this.pinLength, this.pinString, 2);
	}

	/**
	 * @param format Output PIN block format
	 * @return PIN block
	 * @throws InvalidPINBlockFormatException
	 */
	public byte[] getPINBlock(int format) throws InvalidPINBlockFormatException
	{
		byte[] pb = null;
		
		if ((format == PINBlockConstants.ISO_0) || (format == PINBlockConstants.ISO_1) ||
				(format == PINBlockConstants.ISO_2) || (format == PINBlockConstants.ISO_3)) {
			pb = CreatePINBlock(format);
		}
		else
			throw new InvalidPINBlockFormatException();

		return pb;
	}
	
	/**
	 * 
	 * @param pb PIN block
	 * @return PIN
	 */
	static public Pin getPINFromPINBlock(String pb, PrimaryAccountNumber pan)  throws InvalidPINBlockFormatException
	{
		Pin pin = null;
		byte[] pbNumeric = hexstringtobyte(pb);
		byte[] PIN = null;
		byte[] localANB = null;
		int i;
		
		int format = (int)(pbNumeric[0] >> 4);
		int pinLength = (int)(pbNumeric[0] & 0x0F);

		//System.out.println(format);
		//System.out.println(pinLength);
		localANB = hexstringtobyte(String.copyValueOf(FormANBFromPAN(pan.getPrimaryAccountNumber())));
		
		switch(format){
		case 0:
		case 3:
			PIN = new byte[PINBlockConstants.PB_NUM_LENGTH];
			for (i = 0; i < PINBlockConstants.PB_NUM_LENGTH; i++) {
				PIN[i] = (byte)(pbNumeric[i] ^ localANB[i]);
			}
			break;
		case 1:
		case 2:
			PIN = new byte[PINBlockConstants.PB_NUM_LENGTH];
			for (i = 0; i < PINBlockConstants.PB_NUM_LENGTH; i++) {
				PIN[i] = (pbNumeric[i]);
			}
			break;
		default:
			throw new InvalidPINBlockFormatException();
		}

		try {
			pin = new Pin((new String(Hex.encode(PIN))).substring(2, pinLength + 2));
		}
		catch (InvalidPinException e) {
			getLogger().error("Error creating Pin object " + e.getMessage());
		}
		return pin;
	}
}
