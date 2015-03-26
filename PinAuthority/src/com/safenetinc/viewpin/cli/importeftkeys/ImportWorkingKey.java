package com.safenetinc.viewpin.cli.importeftkeys;
import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import iaik.pkcs.pkcs11.wrapper.CK_ATTRIBUTE;
import iaik.pkcs.pkcs11.wrapper.CK_MECHANISM;
import iaik.pkcs.pkcs11.wrapper.PKCS11;
import iaik.pkcs.pkcs11.wrapper.PKCS11Connector;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;

public class ImportWorkingKey 
{
	private static String CRYPTOKI_LIBRARY_LOCATION = "/usr/lib/libCryptoki2.so";
	private static final long CRYPTOKI_SESSION_FLAGS = PKCS11Constants.CKF_SERIAL_SESSION | PKCS11Constants.CKF_RW_SESSION;
	private static final int LUNA_SP_PARTITION_SLOT_ID = 1;
	
	private ImportWorkingKey()
	{
		super();
	}
	
	public static void unwrapZonePinkey(String zoneMasterKeyLabel, String zonePinKeyLabel, String encodedWrappedZonePinKey) throws PKCS11Exception, DecoderException, IOException, MultipleKeysException
	{
		unwrapWorkingKey(zoneMasterKeyLabel, zonePinKeyLabel, PKCS11Constants.CKK_DES2, encodedWrappedZonePinKey, PKCS11Constants.CKM_DES3_ECB, true, true);	
	}
	
	public static void unwrapCardVerificationKeyAlpha(String zoneMasterKeyLabel, String cardVerificationKeyLabel, String encodedWrappedCardVerificationKey) throws PKCS11Exception, DecoderException, IOException, MultipleKeysException
	{
		unwrapWorkingKey(zoneMasterKeyLabel, cardVerificationKeyLabel, PKCS11Constants.CKK_DES, encodedWrappedCardVerificationKey, PKCS11Constants.CKM_DES_ECB, true, false);	
	}
	
	public static void unwrapCardVerificationKeyBravo(String zoneMasterKeyLabel, String cardVerificationKeyLabel, String encodedWrappedCardVerificationKey) throws PKCS11Exception, DecoderException, IOException, MultipleKeysException
	{
		unwrapWorkingKey(zoneMasterKeyLabel, cardVerificationKeyLabel, PKCS11Constants.CKK_DES, encodedWrappedCardVerificationKey, PKCS11Constants.CKM_DES_ECB, true, true);	
	}
	
	private static void unwrapWorkingKey(String zoneMasterKeyLabel, String workingKeyLabel, long workingKeyType, String encodedWrappedWorkingKey,
		long keyCheckValueMechanism, boolean encrypt, boolean decrypt) throws DecoderException, IOException, PKCS11Exception, MultipleKeysException
	{
		// Decode wrapped working key
		byte[] wrappedWorkingKey = Hex.decodeHex(encodedWrappedWorkingKey.toCharArray());

		// Connect to PKCS#11 module
		PKCS11 module = PKCS11Connector.connectToPKCS11Module(CRYPTOKI_LIBRARY_LOCATION);
		
		try
		{
			// Initialise CryptoKi library
			module.C_Initialize(null);
	        
	        long sessionHandle = 0L;
	        
	        try
	        {
	        	// Open session to token
		        sessionHandle = module.C_OpenSession(LUNA_SP_PARTITION_SLOT_ID, CRYPTOKI_SESSION_FLAGS, null, null);
			        
		        // Get handle to existing working key
		        long[] workingKeyHandles = findKeys(module, sessionHandle, PKCS11Constants.CKO_SECRET_KEY, workingKeyLabel, workingKeyType);
		        
		        // Did we find one or more keys with the same label?
		        if(workingKeyHandles != null)
		        {
		        	long nextWorkingKeyHandle = 0L;
		        	
		        	// Destroy all keys found with the same label
		        	for(int i = 0; i < workingKeyHandles.length; i++)
		        	{
		        		nextWorkingKeyHandle = workingKeyHandles[i];
		        		
			        	// Working key already exists, destroy it
			        	module.C_DestroyObject(sessionHandle, nextWorkingKeyHandle);
			        	
			        	System.out.println("existing working key " + workingKeyLabel + " destroyed");
		        	}
		        }
		        
		        // Find Zone Master Key
		        long zoneMasterKeyHandle = findKey(module, sessionHandle, PKCS11Constants.CKO_SECRET_KEY, zoneMasterKeyLabel, PKCS11Constants.CKK_DES2);
		        
		        // Did we find Zone Master Key?
		        if(zoneMasterKeyHandle != 0L)
		        {
		        	// Unwrap working key with Zone Master Key
		        	long workingKeyHandle = unwrapSecretKey(module, sessionHandle, wrappedWorkingKey, zoneMasterKeyHandle, workingKeyLabel, workingKeyType, encrypt, decrypt);
		        	
		        	String workingKeyKeyCheckValue = calculateKeyCheckValue(module, sessionHandle, workingKeyHandle, keyCheckValueMechanism);
		        	
		        	System.out.println(workingKeyLabel + " KCV = " + workingKeyKeyCheckValue);
		        }
		        else
		        {
		        	// Failed to find zone master key
		        	System.out.println("failed to find zone master key " + zoneMasterKeyLabel);
		        	
		        	System.out.println("ensure partition is logged in");
		        }
	        }
	        finally
	        {
	        	if(sessionHandle != 0L)
	        	{
	        		// Close session to token
	        		module.C_CloseSession(sessionHandle);
	        	}
	        }
		}
		finally
		{
			// Finalize CryptoKi library
			module.C_Finalize(null);
		}
	}
	
	private static long unwrapSecretKey(PKCS11 module, long sessionHandle, byte[] wrappedKey, long unwrappingKeyHandle, String keyLabel, long wrappedKeyType, boolean encrypt, boolean decrypt) throws PKCS11Exception
	{
		CK_MECHANISM unwrappingMechanism = new CK_MECHANISM();
		unwrappingMechanism.mechanism = PKCS11Constants.CKM_DES3_ECB;
		
		// Define unwrapped key attributes
		CryptoKiAttributes unwrappedKeyAttributes = new CryptoKiAttributes();
		unwrappedKeyAttributes.add(new LongCryptoKiAttribute(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_SECRET_KEY));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_TOKEN, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_PRIVATE, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_MODIFIABLE, false));
		unwrappedKeyAttributes.add(new LongCryptoKiAttribute(PKCS11Constants.CKA_KEY_TYPE, wrappedKeyType));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_DERIVE, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_SENSITIVE, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_ENCRYPT, encrypt));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_DECRYPT, decrypt));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_SIGN, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_VERIFY, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_WRAP, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_UNWRAP, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_EXTRACTABLE, false));
		unwrappedKeyAttributes.add(new CharArrayCryptoKiAttribute(PKCS11Constants.CKA_LABEL, keyLabel.toCharArray()));
				
		// Create template to hold unwrapped key attributes
		CK_ATTRIBUTE[] unwrappedKeyTemplate = unwrappedKeyAttributes.toArray();

		long unwrappedKeyHandle = module.C_UnwrapKey(sessionHandle, unwrappingMechanism, unwrappingKeyHandle, wrappedKey, unwrappedKeyTemplate);
	
		return unwrappedKeyHandle;
	}
	
	private static String calculateKeyCheckValue(PKCS11 module, long sessionHandle, long keyHandle, long encryptionMechanism) throws PKCS11Exception
	{
		CK_MECHANISM mechanism = new CK_MECHANISM();
		mechanism.mechanism = encryptionMechanism;
		
		module.C_EncryptInit(sessionHandle, mechanism, keyHandle);
		
		byte[] keyCheckValue = module.C_Encrypt(sessionHandle, new byte[8]);
		
		byte[] truncatedKeyCheckValue = new byte[3];
		
		System.arraycopy(keyCheckValue, 0, truncatedKeyCheckValue, 0, truncatedKeyCheckValue.length);
		
		String encodedKeyCheckValue = Hex.encodeHexString(truncatedKeyCheckValue).toUpperCase();
		
		return encodedKeyCheckValue;
	}
	
	private static long findKey(PKCS11 module, long sessionHandle, long objectClass, String label, long keyType) throws PKCS11Exception, MultipleKeysException
	{
		long keyHandle = 0L;
		
		long[] keys = findKeys(module, sessionHandle, objectClass, label, keyType);
		
		if(keys == null)
		{
			return keyHandle;
		}
		
		if(keys.length == 0)
		{
			return keyHandle;
		}
		
		if(keys.length != 1)
		{
			throw new MultipleKeysException();
		}
		
		keyHandle = keys[0];
		
		return keyHandle;
	}
	
	private static long[] findKeys(PKCS11 module, long sessionHandle, long objectClass, String label, long keyType) throws PKCS11Exception
	{
		CK_ATTRIBUTE[] attributeTemplate = new CK_ATTRIBUTE[3];
	
		attributeTemplate[0] = new CK_ATTRIBUTE();
		attributeTemplate[0].type = PKCS11Constants.CKA_CLASS;
		attributeTemplate[0].pValue = new Long(objectClass);
	
		attributeTemplate[1] = new CK_ATTRIBUTE();
		attributeTemplate[1].type = PKCS11Constants.CKA_KEY_TYPE;
		attributeTemplate[1].pValue = keyType;
	
		attributeTemplate[2] = new CK_ATTRIBUTE();
		attributeTemplate[2].type = PKCS11Constants.CKA_LABEL;
		attributeTemplate[2].pValue = label;
	
		module.C_FindObjectsInit(sessionHandle, attributeTemplate);
	
		long[] allKeys = module.C_FindObjects(sessionHandle, 20000);
		
		module.C_FindObjectsFinal(sessionHandle);
		
		return allKeys;
	}
}
