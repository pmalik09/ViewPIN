package com.safenetinc.viewpin.authority;

import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class EncryptDecrypt {

	public static String encrypt(byte[] str, Key key,String transformation) throws Exception 
	{		
		byte[] iv =null;
        IvParameterSpec ivps =null;
		try {
			
			// Extract intialisation vector
        	int IVlen = 0;
        	
        	//set the IVlen = 16 in case of AES...block size in AES=16bytes
        	if(key.getAlgorithm().compareToIgnoreCase("AES") == 0)
        		IVlen = 16;
        	else
        	//set the IVlen = 8 in case of DES/DES2/DES3...block size in DES/DES2/DES3=8bytes	
        		IVlen = 8;
        	
			iv = new byte[IVlen];
			
			for (int i=0;i<IVlen ;i++ )
			{
				iv[i]=0;
			}
		

			ivps = new IvParameterSpec(iv);

			Cipher cipher=Cipher.getInstance(transformation);
			

			if((-1 == transformation.lastIndexOf("ECB")) &&
				(-1 == transformation.lastIndexOf("CBC")))
			
				 cipher.init(Cipher.ENCRYPT_MODE, key);
			
			else
			  cipher.init(Cipher.ENCRYPT_MODE, key, ivps);
			
			
            // Encode the string into bytes using utf-8
	       // byte[] utf8 = str.getBytes("UTF8");
	                   
	        // Encrypt
	        byte[] enc = cipher.doFinal(str);
	        
	        // Encode bytes to base64 to get a string
	        return new String(Base64.encodeBase64(enc));

	    } 
		catch (Exception e ) {
			
			throw e;
		}
				
	}
	
	public static byte[] decrypt(String str,Key key,String transformation) throws Exception
	{
		
	 
		 byte[] iv =null;
		 IvParameterSpec ivps =null;
         try {    	
        
      		 // Decode base64 to get bytes
            byte[] dec = Base64.decodeBase64(str.getBytes());
           
        	Cipher cipher=Cipher.getInstance(transformation);        
        	
			// Extract intialisation vector
        	
        	int IVlen = 0;
        	
        	//set the IVlen = 16 in case of AES...block size in AES=16bytes
        	if(key.getAlgorithm().compareToIgnoreCase("AES") == 0)
        		IVlen = 16;
        	else
        	//set the IVlen = 8 in case of DES/DES2/DES3...block size in DES/DES2/DES3=8bytes	
        		IVlen = 8;
        	
			iv = new byte[IVlen];
			
			for (int i=0;i<IVlen ;i++ )
			{
				iv[i]=0;
			}
		

			ivps = new IvParameterSpec(iv);        
			
			

			//if(transformation)
			if((-1 == transformation.lastIndexOf("ECB")) &&
				(-1 == transformation.lastIndexOf("CBC")))
			
				cipher.init(Cipher.DECRYPT_MODE,key);

			else
				cipher.init(Cipher.DECRYPT_MODE,key, ivps);
	
            
            // Decrypt
            byte[] utf8 =cipher.doFinal(dec);

            // Decode using utf-8
          //  return new String(utf8, "UTF8");
		  return utf8;
        }   
		catch (Exception e ) {
              
			throw e;
        
        }
		
     }

}
