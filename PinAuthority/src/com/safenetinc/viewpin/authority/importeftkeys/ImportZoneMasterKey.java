package com.safenetinc.viewpin.authority.importeftkeys;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import iaik.pkcs.pkcs11.wrapper.CK_ATTRIBUTE;
import iaik.pkcs.pkcs11.wrapper.CK_MECHANISM;
import iaik.pkcs.pkcs11.wrapper.CK_RSA_PKCS_OAEP_PARAMS;
import iaik.pkcs.pkcs11.wrapper.PKCS11;
import iaik.pkcs.pkcs11.wrapper.PKCS11Connector;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;

public class ImportZoneMasterKey 
{
	private static String CRYPTOKI_LIBRARY_LOCATION = "/usr/lib/libCryptoki2.so";
	private static final long CRYPTOKI_SESSION_FLAGS = PKCS11Constants.CKF_SERIAL_SESSION | PKCS11Constants.CKF_RW_SESSION;
	private static final int LUNA_SP_PARTITION_SLOT_ID = 1;
	private static final String APPLICATION_NAME = "UnwrapZoneMasterKey";
	
	private String zoneMasterKeyLabel = null;
	private String wrappingKeyLabel = null;
	private String wrappedZoneMasterKeyFilename = null;
	
	public ImportZoneMasterKey(String[] args)
	{
		super();
		
		try
		{
			processCommandLine(args);
			
			unwrapZoneMasterKey();
		}
	    catch(ParseException pe)
	    {
	        //No action required
	    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void processCommandLine(String[] args) throws ParseException
    {
        Option zoneMasterKeyLabelOption;
        Option wrappingKeyLabelOption;
        Option wrappedZoneMasterKeyFilenameOption;
        Options commandLineOptions;
        CommandLineParser clp;
        CommandLine cl;
        
        zoneMasterKeyLabelOption = null;
        wrappingKeyLabelOption = null;
        wrappedZoneMasterKeyFilenameOption = null;
        commandLineOptions = null;
        clp = null;
        cl = null;
        
        zoneMasterKeyLabelOption = new Option("z", "zmklabel", true, "zone master key label");
        zoneMasterKeyLabelOption.setRequired(true);
        zoneMasterKeyLabelOption.setArgs(1);
        zoneMasterKeyLabelOption.setOptionalArg(false);
        zoneMasterKeyLabelOption.setArgName("zmklabel");
        
        wrappingKeyLabelOption = new Option("w", "wraplabel", true, "wrapping key label");
        wrappingKeyLabelOption.setRequired(true);
        wrappingKeyLabelOption.setArgs(1);
        wrappingKeyLabelOption.setOptionalArg(false);
        wrappingKeyLabelOption.setArgName("wraplabel");

        wrappedZoneMasterKeyFilenameOption = new Option("f", "wrapfile", true, "wrapped zone master key filename");
        wrappedZoneMasterKeyFilenameOption.setRequired(true);
        wrappedZoneMasterKeyFilenameOption.setArgs(1);
        wrappedZoneMasterKeyFilenameOption.setOptionalArg(false);
        wrappedZoneMasterKeyFilenameOption.setArgName("wrapfile");
        
        commandLineOptions = new Options();
        commandLineOptions.addOption(zoneMasterKeyLabelOption);
        commandLineOptions.addOption(wrappingKeyLabelOption);
        commandLineOptions.addOption(wrappedZoneMasterKeyFilenameOption);
        
        clp = new PosixParser();

        try
        {
            cl = clp.parse(commandLineOptions, args);
            
            setZoneMasterKeyLabel(cl.getOptionValue('z'));
            setWrappingKeyLabel(cl.getOptionValue('w'));
            setWrappedZoneMasterKeyFilename(cl.getOptionValue('f'));
        }
        catch (ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();

            formatter.printHelp(APPLICATION_NAME, commandLineOptions, true);

            throw pe;
        }
    }
	
	private void unwrapZoneMasterKey() throws IOException, DecoderException, PKCS11Exception, MultipleKeysException
	{
		File wrappedZoneMasterKeyFilename = new File("/usr-files/" + getWrappedZoneMasterKeyFilename());
		
		// Ensure we can read file containing wrapped zone master key
		if(wrappedZoneMasterKeyFilename.canRead() == false)
		{
			System.out.println("failed to read file " + getWrappedZoneMasterKeyFilename() + " containing wrapped zone master key");
			
			return;
		}
		
		FileReader fr = null;
		BufferedReader br =  null;
		String encodedWrappedZoneMasterKey = null;
		
		try
		{
			fr = new FileReader(wrappedZoneMasterKeyFilename);
		
			br = new BufferedReader(fr);
		
			// Get encoded wrapped zone master key from file
			encodedWrappedZoneMasterKey = br.readLine();
		}
		finally
		{
			if(br != null)
			{
				br.close();
			}
			
			if(fr != null)
			{
				fr.close();
			}
		}
		
		// Decode wrapped zone master key
		byte[] wrappedZoneMasterKey = Hex.decodeHex(encodedWrappedZoneMasterKey.toCharArray());
	
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
		            
		        // Check if zone master key with the same label already exists
		        long[] zoneMasterKeyHandles = findKeys(module, sessionHandle, PKCS11Constants.CKO_SECRET_KEY, getZoneMasterKeyLabel(), PKCS11Constants.CKK_DES2);
		        
		        long nextZonePinKeyHandle = 0L;

		        // Ensure all zone master keys with the same label are destroyed
	        	for(int i = 0; i < zoneMasterKeyHandles.length; i++)
	        	{
	        		nextZonePinKeyHandle = zoneMasterKeyHandles[i];
	        		
		        	// zone master key with the same label already exists, destroy it
		        	module.C_DestroyObject(sessionHandle, nextZonePinKeyHandle);
		        	
		        	System.out.println("existing zone master key " + getZoneMasterKeyLabel() + " destroyed");
	        	}
		        
		        // Get handle to wrapping key
		        long wrappingKeyHandle = findKey(module, sessionHandle, PKCS11Constants.CKO_PRIVATE_KEY, getWrappingKeyLabel(), PKCS11Constants.CKK_RSA);

		        // Did we find handle to wrapping key successfully?
		        if(wrappingKeyHandle != 0L)
		        {
		        	// Unwrap zone master key using wrapping key
		        	long zoneMasterKeyHandle = unwrapKey(module, sessionHandle, wrappedZoneMasterKey, wrappingKeyHandle, getZoneMasterKeyLabel(), PKCS11Constants.CKK_DES2);
		        	
		        	String zoneMasterKeyKeyCheckValue = calculateDoubleDesKeyCheckValue(module, sessionHandle, zoneMasterKeyHandle);
		        	System.out.println("zone master key KCV = " + zoneMasterKeyKeyCheckValue);
		        }
		        else
		        {
		        	System.out.println("failed to find wrapped key " + getWrappingKeyLabel());
		        	
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
	
	private long unwrapKey(PKCS11 module, long sessionHandle, byte[] wrappedKey, long unwrappingKeyHandle, String keyLabel, long wrappedKeyType) throws PKCS11Exception
	{
		CK_RSA_PKCS_OAEP_PARAMS unwrappingMechanismParameters = new CK_RSA_PKCS_OAEP_PARAMS();
		unwrappingMechanismParameters.hashAlg = PKCS11Constants.CKM_SHA_1;
		unwrappingMechanismParameters.mgf = PKCS11Constants.CKG_MGF1_SHA1;
		unwrappingMechanismParameters.pSourceData = null;
		unwrappingMechanismParameters.source = PKCS11Constants.CKZ_DATA_SPECIFIED;
		
		CK_MECHANISM unwrappingMechanism = new CK_MECHANISM();
		unwrappingMechanism.mechanism = PKCS11Constants.CKM_RSA_PKCS_OAEP;
		unwrappingMechanism.pParameter = unwrappingMechanismParameters;
		
		// Define unwrapped key attributes
		CryptoKiAttributes unwrappedKeyAttributes = new CryptoKiAttributes();
		unwrappedKeyAttributes.add(new LongCryptoKiAttribute(PKCS11Constants.CKA_CLASS, PKCS11Constants.CKO_SECRET_KEY));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_TOKEN, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_PRIVATE, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_MODIFIABLE, false));
		unwrappedKeyAttributes.add(new LongCryptoKiAttribute(PKCS11Constants.CKA_KEY_TYPE, wrappedKeyType));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_DERIVE, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_SENSITIVE, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_ENCRYPT, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_DECRYPT, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_SIGN, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_VERIFY, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_WRAP, false));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_UNWRAP, true));
		unwrappedKeyAttributes.add(new BooleanCryptoKiAttribute(PKCS11Constants.CKA_EXTRACTABLE, false));
		unwrappedKeyAttributes.add(new CharArrayCryptoKiAttribute(PKCS11Constants.CKA_LABEL, keyLabel.toCharArray()));
				
		// Create template to hold unwrapped key attributes
		CK_ATTRIBUTE[] unwrappedKeyTemplate = unwrappedKeyAttributes.toArray();

		long unwrappedKeyHandle = module.C_UnwrapKey(sessionHandle, unwrappingMechanism, unwrappingKeyHandle, wrappedKey, unwrappedKeyTemplate);
	
		return unwrappedKeyHandle;
	}
	
	private String calculateDoubleDesKeyCheckValue(PKCS11 module, long sessionHandle, long keyComponentHandle) throws PKCS11Exception
	{
		CK_MECHANISM encryptionMechanism = new CK_MECHANISM();
		encryptionMechanism.mechanism = PKCS11Constants.CKM_DES3_ECB;
		
		module.C_EncryptInit(sessionHandle, encryptionMechanism, keyComponentHandle);
		
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
	
	private void setZoneMasterKeyLabel(String zoneMasterKeyLabel) 
	{
		this.zoneMasterKeyLabel = zoneMasterKeyLabel;
	}
	
	private String getZoneMasterKeyLabel() 
	{
		return this.zoneMasterKeyLabel;
	}
	
	private void setWrappingKeyLabel(String wrappingKeyLabel) 
	{
		this.wrappingKeyLabel = wrappingKeyLabel;
	}
	
	private String getWrappingKeyLabel() 
	{
		return this.wrappingKeyLabel;
	}
	
	private void setWrappedZoneMasterKeyFilename(String wrappedZoneMasterKeyFilename) 
	{
		this.wrappedZoneMasterKeyFilename = wrappedZoneMasterKeyFilename;
	}
	
	private String getWrappedZoneMasterKeyFilename() 
	{
		return this.wrappedZoneMasterKeyFilename;
	}

	public static void main(String[] args) 
	{
		new ImportZoneMasterKey(args);
	}
}
