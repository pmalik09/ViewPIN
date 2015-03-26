package com.safenetinc.viewpin.banksimulator;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.safenetinc.viewpin.banksimulator.PrimaryAccountNumber;
import com.safenetinc.viewpin.banksimulator.EntropyPool;
import com.safenetinc.viewpin.banksimulator.NibbleBuffer;
import com.safenetinc.viewpin.banksimulator.InvalidPinBlockFormatException;
import com.safenetinc.viewpin.banksimulator.InvalidPinException;

public class IsoPinBlockFactory 
{
	private static final int ISO_PIN_BLOCK_FORMAT_ZERO  = 0;
	private static final int ISO_PIN_BLOCK_FORMAT_ONE   = 1;
	private static final int ISO_PIN_BLOCK_FORMAT_TWO   = 2;
	private static final int ISO_PIN_BLOCK_FORMAT_THREE = 3;
	private static final int PIN_BLOCK_LENGTH = 8;
	private static final int PAN_LENGTH = 8;
	private static final int TOTAL_PAN_DIGITS = 16;
	private static final byte ISO_PIN_BLOCK_STATIC_PADDING_CHARACTER = 0x0F;
	
	private KeyStore zonePinKeyStore = null;
	private String zonePinKeyCipherProvider = null;
	
	public IsoPinBlockFactory(KeyStore zonePinKeyStore, String zonePinKeyCipherProvider)
    {
    	super();
    	
    	setZonePinKeyStore(zonePinKeyStore);
    	setZonePinKeyCipherProvider(zonePinKeyCipherProvider);
    }
        
    public byte[] generateIsoFormatZeroPinBlock(IsoPin pin, String zonePinKeyAlias, String zonePinKeyPassword, 
    	PrimaryAccountNumber primaryAccountNumber) throws NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, KeyStoreException, IllegalBlockSizeException, BadPaddingException
    {
    	return buildIsoPinBlock(pin, ISO_PIN_BLOCK_FORMAT_ZERO, zonePinKeyAlias, zonePinKeyPassword, primaryAccountNumber);
    }
    
    public byte[] generateIsoFormatOnePinBlock(IsoPin pin, String zonePinKeyAlias, String zonePinKeyPassword) throws NoSuchAlgorithmException, InvalidPinBlockFormatException, UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, KeyStoreException, IllegalBlockSizeException, BadPaddingException
    {
    	return buildIsoPinBlock(pin, ISO_PIN_BLOCK_FORMAT_ONE, zonePinKeyAlias, zonePinKeyPassword);
    }
    
    public byte[] generateIsoFormatTwoPinBlock(IsoPin pin, String zonePinKeyAlias, String zonePinKeyPassword) throws NoSuchAlgorithmException, InvalidPinBlockFormatException, UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, KeyStoreException, IllegalBlockSizeException, BadPaddingException
    {
    	return buildIsoPinBlock(pin, ISO_PIN_BLOCK_FORMAT_TWO, zonePinKeyAlias, zonePinKeyPassword);
    }
    
    public byte[] generateIsoFormatThreePinBlock(IsoPin pin, String zonePinKeyAlias, String zonePinKeyPassword, PrimaryAccountNumber primaryAccountNumber) throws NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, KeyStoreException, IllegalBlockSizeException, BadPaddingException
    {
    	return buildIsoPinBlock(pin, ISO_PIN_BLOCK_FORMAT_THREE, zonePinKeyAlias, zonePinKeyPassword, primaryAccountNumber);
    }
    
    public IsoPin parseIsoPinBlock(byte[] pinBlock, String zonePinKeyAlias, String zonePinKeyPassword) throws InvalidPinBlockFormatException, InvalidPinException, UnrecoverableKeyException, InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
    {
    	byte[] decryptedPinBlock = decryptPinBlock(pinBlock, zonePinKeyAlias, zonePinKeyPassword);
    	
    	// Validate pin block
    	validatePinBlock(decryptedPinBlock);
    	
    	// Extract pin from pin block
    	IsoPin pin = parsePin(decryptedPinBlock);
    	
    	return pin;
    }
    
    public IsoPin parseIsoPinBlock(byte[] pinBlock, String zonePinKeyAlias, String zonePinKeyPassword, PrimaryAccountNumber primaryAccountNumber) throws InvalidPinBlockFormatException, InvalidPinException, UnrecoverableKeyException, InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
    {
    	byte[] decryptedPinBlock = decryptPinBlock(pinBlock, zonePinKeyAlias, zonePinKeyPassword);
    	
    	// Validate pin block
    	validatePinBlock(decryptedPinBlock);
    	
    	// Extract pin block format from pin block
    	int pinBlockFormat = extractPinBlockFormat(decryptedPinBlock);
    	
    	// Have we been passed primary account number unnecessarily?
    	if(pinBlockFormat != ISO_PIN_BLOCK_FORMAT_ZERO && pinBlockFormat != ISO_PIN_BLOCK_FORMAT_THREE)
    	{
    		throw new InvalidPinBlockFormatException("primary account number is superflous");
    	}
    	
    	// Build primary account number mask
        byte[] primaryAccountNumberMask = buildPrimaryAccountNumberMask(primaryAccountNumber);
		
        // XOR pin block with primary account number mask
        decryptedPinBlock = xor(decryptedPinBlock, primaryAccountNumberMask);
		
		// Extract pin from pin block
		IsoPin pin = parsePin(decryptedPinBlock);
		
		return pin;
    }
    
    private byte[] buildIsoPinBlock(IsoPin pin, int pinBlockFormat, String zonePinKeyAlias, String zonePinKeyPassword) throws NoSuchAlgorithmException,
        UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, KeyStoreException,
        IllegalBlockSizeException, BadPaddingException
    {
    	byte[] pinBlock = buildBasicPinBlock(pin, pinBlockFormat);
    	
    	// Encrypt pin block
    	byte[] encryptedPinBlock = encryptPinBlock(pinBlock, zonePinKeyAlias, zonePinKeyPassword);
    	
    	return encryptedPinBlock;
    }
    
    private byte[] buildIsoPinBlock(IsoPin pin, int pinBlockFormat, 
        String zonePinKeyAlias, String zonePinKeyPassword, PrimaryAccountNumber primaryAccountNumber) throws NoSuchAlgorithmException,
        UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException,
        KeyStoreException, IllegalBlockSizeException, BadPaddingException
    {
		byte[] pinBlock = buildBasicPinBlock(pin, pinBlockFormat);
		
		byte[] primaryAccountNumberMask = buildPrimaryAccountNumberMask(primaryAccountNumber);
		
		// XOR pin block with primary account number mask
		pinBlock = xor(pinBlock, primaryAccountNumberMask);
		
		// Encrypt pin block
		byte[] encryptedPinBlock = encryptPinBlock(pinBlock, zonePinKeyAlias, zonePinKeyPassword);
		
    	return encryptedPinBlock;
    }
    
    private void validatePinBlock(byte[] pinBlock) throws InvalidPinBlockFormatException, InvalidPinException
    {
    	// Ensure pin block passed in is not null
    	if(pinBlock == null)
		{
			throw new InvalidPinBlockFormatException("is null");
		}
    	
    	// Ensure length of pin block passed in is valid
    	if(pinBlock.length != PIN_BLOCK_LENGTH)
		{
			throw new InvalidPinBlockFormatException("invalid length");
		}
    	
    	// Extract pin block format from pin block
    	int pinBlockFormat = extractPinBlockFormat(pinBlock);
    	
    	// Validate pin block format
    	validatePinBlockFormat(pinBlockFormat);
    	
    	// Validate pin block length
    	validatePinLength(pinBlock);
    }
    
    private void validatePinBlockFormat(int pinBlockFormat) throws InvalidPinBlockFormatException
	{
		switch(pinBlockFormat)
		{
			case ISO_PIN_BLOCK_FORMAT_ZERO  :
			case ISO_PIN_BLOCK_FORMAT_ONE   :
			case ISO_PIN_BLOCK_FORMAT_TWO   :
			case ISO_PIN_BLOCK_FORMAT_THREE :
				
				break;
				
			default:
				
				throw new InvalidPinBlockFormatException("invalid ISO PIN block format");
		}
	}
    
    private void validatePinLength(byte[] pinBlock) throws InvalidPinBlockFormatException
	{
     	int pinLength = extractPinLengthFromPinBlock(pinBlock);
        
		if(pinLength < IsoPin.MINIMUM_ISO_PIN_LENGTH || pinLength > IsoPin.MAXIMUM_ISO_PIN_LENGTH)
		{
			throw new InvalidPinBlockFormatException("invalid pin length");
		}
	}
    
    private byte[] buildBasicPinBlock(IsoPin pin, int pinBlockFormat) throws NoSuchAlgorithmException, NoSuchProviderException
	{
		NibbleBuffer pinBlock = new NibbleBuffer(PIN_BLOCK_LENGTH); 
		
		// Place pin block format into first nibble position
		pinBlock.appendNibble((byte)pinBlockFormat);
		
		// Get length of pin
		byte pinLength = (byte)pin.getPin().length();
		
		// Place pin length in second nibble position
		pinBlock.appendNibble(pinLength);
	
	    byte nextPinDigit = 0x00;
		
		// Place pin beginning at third nibble position
		for(int i = 0; i < pinLength; i++)
		{
			nextPinDigit = (byte)(pin.getPin().charAt(i));
			pinBlock.appendNibble(nextPinDigit);
		}
		
		// Calculate total number of padding characters required
		int totalRequiredPaddingCharacters = calculateTotalPaddingCharactersRequired(pin);
	
		// Apply padding based on pin block format
		switch(pinBlockFormat)
		{
			case ISO_PIN_BLOCK_FORMAT_ZERO :
			case ISO_PIN_BLOCK_FORMAT_TWO :
				
				// Pad remaining positions of pin block with F padding character
				for(int i = 0; i < totalRequiredPaddingCharacters; i++)
				{
					pinBlock.appendNibble(ISO_PIN_BLOCK_STATIC_PADDING_CHARACTER);
				}
				
				break;
				
			case ISO_PIN_BLOCK_FORMAT_ONE :
				
				// Use random padding for ISO pin block format one
				for(int i = 0; i < totalRequiredPaddingCharacters; i++)
				{
					// Generate a random padding character whose value is between 0 and F
					pinBlock.appendNibble(generateRandomPaddingCharacter());
				}
				
				break;
				
			case ISO_PIN_BLOCK_FORMAT_THREE :
				
				// Use random padding for ISO pin block format three
				byte nextPaddingCharacter = 0;
				
				for(int i = 0; i < totalRequiredPaddingCharacters; i++)
				{
					// Have we reached the last two positions to be padded?
					if(i < 8)
					{
						// All but the last two padding characters should be set to F
						nextPaddingCharacter = ISO_PIN_BLOCK_STATIC_PADDING_CHARACTER;	
					}
					else
					{
						// Generate a random padding character whose value is between 0x0A and 0x0F for the last two padding characters
						nextPaddingCharacter = generateRandomPaddingCharacterGreaterThanNine();
					}
					
					pinBlock.appendNibble(nextPaddingCharacter);
				}
				
				break;
				
			default :
				
				// We will never reach here as pin block form has already been validated
				
				break;
		}
		
		return pinBlock.getBuffer();
	}
    
    private byte[] buildPrimaryAccountNumberMask(PrimaryAccountNumber primaryAccountNumber)
	{
		// Calculate total length of primary account number
		int totalPrimaryAccountNumberLength = primaryAccountNumber.getPrimaryAccountNumber().length();

		// Calculate start and end index of the twelve right most digits of primary account number excluding check digit
		int panStartIndex = (totalPrimaryAccountNumberLength - 1) - 12;
		int panEndIndex = totalPrimaryAccountNumberLength - 1; // Exclude check digit
		
		// Extract right most twelve digits of primary account number excluding check digit
		String rightMostTwelveDigitsOfPanMinusCheckDigit = primaryAccountNumber.getPrimaryAccountNumber().substring(panStartIndex, panEndIndex);
		
		// Build primary account number mask
		NibbleBuffer primaryAccountNumberMask = new NibbleBuffer(PAN_LENGTH);
		
		// Zero fill the first four positions
		primaryAccountNumberMask.appendNibble((byte)0x00);
		primaryAccountNumberMask.appendNibble((byte)0x00);
		primaryAccountNumberMask.appendNibble((byte)0x00);
		primaryAccountNumberMask.appendNibble((byte)0x00);
		
		// Append right most twelve digits of primary account number excluding the check digit to primary account number mask
		primaryAccountNumberMask.append(rightMostTwelveDigitsOfPanMinusCheckDigit);
		
		return primaryAccountNumberMask.getBuffer();
	}
    
    private IsoPin parsePin(byte[] pinBlock) throws InvalidPinBlockFormatException, InvalidPinException
	{
        NibbleBuffer nb = new NibbleBuffer(pinBlock);
		
		// Move passed pin block format and pin length nibbles
		nb.getNextNibble(); // pin block format nibble
		nb.getNextNibble(); // pin length nibble
		
		StringBuffer pin = new StringBuffer();
		
		char nextPinDigit = '\0';
		
		int pinLength = extractPinLengthFromPinBlock(pinBlock);
	    
		// Get each digit of pin
		for(int i = 0; i < pinLength; i++)
		{
			nextPinDigit = (char)nb.getNextNibble();
			nextPinDigit += 0x30;
			
			pin.append(nextPinDigit);
		}
		
		// Calculate total padding characters
		int totalPaddingCharacters = calculateTotalPaddingCharactersRequired(pinLength);
		
		// Extract pin block format from pin block
		int pinBlockFormat = extractPinBlockFormat(pinBlock);
	    		
		// Verify each padding character of an ISO format zero or two pin block format is F
		if(pinBlockFormat == ISO_PIN_BLOCK_FORMAT_ZERO || pinBlockFormat == ISO_PIN_BLOCK_FORMAT_TWO)
		{
			byte nextPaddingCharacter = 0;
			
			for(int i = 0; i < totalPaddingCharacters; i++)
			{
				nextPaddingCharacter = nb.getNextNibble();
				
				if(nextPaddingCharacter != ISO_PIN_BLOCK_STATIC_PADDING_CHARACTER)
				{
					throw new InvalidPinBlockFormatException("bad padding");	
				}
			}
		}
		
		return new IsoPin(pin.toString());
	}
    
    private int calculateTotalPaddingCharactersRequired(int pinLength)
    {
    	// Calculate total number of padding characters required
    	int totalRequiredPaddingCharacters = TOTAL_PAN_DIGITS;
    	totalRequiredPaddingCharacters -= 1; // Length of pin block format
    	totalRequiredPaddingCharacters -= 1; // Length of pin length
    	totalRequiredPaddingCharacters -= pinLength; // Pin length
    	
    	return totalRequiredPaddingCharacters;
    }
    
    private int calculateTotalPaddingCharactersRequired(IsoPin pin)
    {
    	return calculateTotalPaddingCharactersRequired(pin.getPin().length());
    }
    
    private byte generateRandomPaddingCharacter() throws NoSuchAlgorithmException, NoSuchProviderException
    {
    	byte randomPaddingCharacter = 0;
    	
    	byte randomByte = EntropyPool.getByte();
			
		// Isolate low nibble of random byte
		byte lowRandomNibble = (byte)(randomByte & 0x0000000F);
		
		randomPaddingCharacter = lowRandomNibble;
						
		return randomPaddingCharacter;
	}
    
    private byte generateRandomPaddingCharacterGreaterThanNine() throws NoSuchAlgorithmException, NoSuchProviderException
    {
    	byte randomPaddingCharacter = 0;
        
    	// Keep going until we have generated a random padding character that is between 0x0A and 0x0F
		while(true)
		{
			// Generate a random byte of entropy
			byte nextRandomByte = EntropyPool.getByte();
			
			// Isolate low nibble of random byte
			byte nextLowRandomNibble = (byte)(nextRandomByte & 0x0000000F);
			
			// Is this random nibble between 0x0A and 0x0F?
			if(nextLowRandomNibble >= 0x0A)
			{
				// This random nibble is between 0x0A and 0x0F
				randomPaddingCharacter = nextLowRandomNibble;
				
				break;
			}
			
			// Isolate high nibble of random byte
			byte nextHighRandomNibble = (byte)((nextRandomByte >>> 4) & 0x0000000F);
			
			// Is this random nibble between 0x0A and 0x0F?
			if(nextHighRandomNibble >= 0x0A)
			{
				// This random nibble is between 0x0A and 0x0F
				randomPaddingCharacter = nextHighRandomNibble;
				
				break;
			}
		}

		return randomPaddingCharacter;
    }
    
    private int extractPinBlockFormat(byte[] pinBlock)
    {
    	int pinBlockFormat = (pinBlock[0] >>> 4) & 0x0000000F;
    	
    	return pinBlockFormat;
    }
    
    private int extractPinLengthFromPinBlock(byte[] pinBlock)
    {
    	int pinLength = pinBlock[0] & 0x0000000F;
    	
    	return pinLength;
    }
    
    private byte[] xor(byte[] a, byte[] b)
    {
        byte[] c = new byte[a.length];

        for(int i = 0; i < a.length; i++)
        {
            c[i] = (byte)((int)a[i] ^ (int)b[i]);
        }

        return c;
    }
    
    private KeyStore getZonePinKeyStore()
    {
		return this.zonePinKeyStore;
	}

	private void setZonePinKeyStore(KeyStore zonePinKeyStore)
	{
		this.zonePinKeyStore = zonePinKeyStore;
	}
	
	private SecretKey getZonePinKey(String zonePinKeyAlias, String zonePinKeyPassword) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException
	{
		// Ensure zone pin key is present in key store
		if(getZonePinKeyStore().isKeyEntry(zonePinKeyAlias) == false)
		{
			// Zone pin key required to decrypt pin block is not present in key store
			throw new KeyStoreException("zone pin key " + zonePinKeyAlias + " not found");
		}
		
	
		
		 // Get zone pin key from key store
		SecretKey encryptionKey = (SecretKey)getZonePinKeyStore().getKey(zonePinKeyAlias, zonePinKeyPassword.toCharArray()); 
		//convert the DES2 to DES3 
		byte[] keyBytes = new byte[24];
		
                	  
		keyBytes = encryptionKey.getEncoded();
		final SecretKey zonePinKey = new SecretKeySpec(keyBytes, "DESede");
		
	            		              		
       
		
		
		
		return zonePinKey;
		
	}
	
	private byte[] encryptPinBlock(byte[] pinBlock, String zonePinKeyAlias, String zonePinKeyPassword) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, KeyStoreException, UnrecoverableKeyException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		// Get zone pin key from key store
		SecretKey zonePinKey = getZonePinKey(zonePinKeyAlias, zonePinKeyPassword);
		
		// Instantiate Cipher object for encryption
		//Cipher c = Cipher.getInstance("DESede/ECB/NoPadding", getZonePinKeyCipherProvider());
		Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
		c.init(Cipher.ENCRYPT_MODE, zonePinKey);
		
		// Encrypt pin block
		byte[] encryptedPinBlock = c.doFinal(pinBlock);
		
		return encryptedPinBlock;
	}
	
	private byte[] decryptPinBlock(byte[] encryptedPinBlock, String zonePinKeyAlias, String zonePinKeyPassword) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		// Get zone pin key from key store
		SecretKey zonePinKey = getZonePinKey(zonePinKeyAlias, zonePinKeyPassword);
				
		// Instantiate Cipher object for decryption
	//	Cipher c = Cipher.getInstance("DESede/ECB/NoPadding", getZonePinKeyCipherProvider());
		Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
		c.init(Cipher.DECRYPT_MODE, zonePinKey);
		
		// Decrypt pin block
		byte[] decryptedPinBlock = c.doFinal(encryptedPinBlock);
		
		return decryptedPinBlock;
	}

	private void setZonePinKeyCipherProvider(String zonePinKeyCipherProvider) 
	{
		this.zonePinKeyCipherProvider = zonePinKeyCipherProvider;
	}
	
	private String getZonePinKeyCipherProvider()
	{
		return this.zonePinKeyCipherProvider;
	}

	public static void main(String[] args)
	{
		try 
		{
			KeyStore zonePinKeyStore = KeyStore.getInstance("JKS", "SUN");
			
			IsoPinBlockFactory isoPinBlockFactory = new IsoPinBlockFactory(zonePinKeyStore, "SUN");
			
			String zonePinKeyAlias = "zpk";
			String zonePinKeyPassword = "password";
			
			//IsoPin pin = IsoPinBlockParser.parse(Hex.decodeHex("0592789fffedcba9".toCharArray()), new PrimaryAccountNumber("4000001234562"));
			//System.out.println(pin.getPin());
			
			//byte[] pinBlock = generateIsoFormatZeroPinBlock(new IsoPin("92389"), new PrimaryAccountNumber("4000001234562"));
			byte[] pinBlock = isoPinBlockFactory.generateIsoFormatZeroPinBlock(new IsoPin("123456"),
				zonePinKeyAlias, zonePinKeyPassword, new PrimaryAccountNumber("1112223334445559")); // 06121675CCBBBAAA 
			
			IsoPin pin = isoPinBlockFactory.parseIsoPinBlock(pinBlock, zonePinKeyAlias, zonePinKeyPassword,
					new PrimaryAccountNumber("1112223334445559"));
			
			System.out.println(pin);
			//generateIsoPinBlockFormatOne(new IsoPin("92389"));
			
			//generateIsoPinBlockFormatTwo(new IsoPin("123456789012"));
			
			/*
			byte[] pinBlock = generateIsoPinBlockFormatThree(new IsoPin("92389"), new PrimaryAccountNumber("4000001234562"));
		    // | 0 | 5 | 9 | 2 | 7 | 8 | 9 | F | F | F | E | D | C | B | A | 9 |
		     
		     */
			//System.out.println(Hex.encodeHexString(pinBlock));
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
}
