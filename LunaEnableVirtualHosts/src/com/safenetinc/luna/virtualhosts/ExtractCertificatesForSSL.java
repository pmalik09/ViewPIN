/*
 * Created on 20 Aug 2012
 */
package com.safenetinc.luna.virtualhosts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

public class ExtractCertificatesForSSL
{

    private static final String CERTIFICATE_FILE_PATH      = "/usr-xfiles/";

    private static final String PRIVATE_KEY_STUB_EXTENSION = ".key";

    private static final String CERTIFICATE_EXTENSION      = ".crt";

    public String createCertificateFile (String certificateSKI) throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException
   // public String createCertificateFile (String certificateSKI) 
    {
	 String path = CERTIFICATE_FILE_PATH + certificateSKI + CERTIFICATE_EXTENSION;
	  
        X509Certificate certificate = getTomcatCertificate(certificateSKI);
        
		
        File file = new File(path);

        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);

		
		
        pw.println("-----BEGIN CERTIFICATE-----");
        pw.print(new String(Base64.encodeBase64(certificate.getEncoded(), true)));
		pw.print("-----END CERTIFICATE-----");
		
		
		
        pw.close();
        fw.close();

			
        file.setReadable(true, false);
        file.setWritable(true, false);

        //System.out.println("Exported certificate " + certificateSKI + " to "+path+" OK");
        
        return path;
		

    }

   

    public String createPrivateKeyStubFile (String certificateSKI) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        String path = CERTIFICATE_FILE_PATH + certificateSKI + PRIVATE_KEY_STUB_EXTENSION;
        
        RSAPrivateKey privateKey = (RSAPrivateKey) getTomcatPrivateKey(certificateSKI);

        if (privateKey == null)
        {
            System.out.println("Private key object not found in partition");

            throw new CertificateException("Private key object not found in partition");
        }

       // System.out.println("located specified private key object in partition ok");

        BigInteger modulus = privateKey.getModulus();
        BigInteger publicExponent = new BigInteger("65537");
        BigInteger privateExponent = new BigInteger("1");
        BigInteger primeP = new BigInteger("13");
        BigInteger primeQ = new BigInteger("12");
        BigInteger primeExponentP = new BigInteger("1");
        BigInteger primeExponentQ = new BigInteger("1");
        BigInteger crtCoefficient = new BigInteger("1");

        RSAPrivateKeyStructure dummyTomcatPrivateKeyStructure = new RSAPrivateKeyStructure(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);

        File file = new File(path);

        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);

        pw.println("-----BEGIN RSA PRIVATE KEY-----");
        pw.print(new String(Base64.encodeBase64(dummyTomcatPrivateKeyStructure.getDEREncoded(), true)));
        pw.print("-----END RSA PRIVATE KEY-----");

        pw.close();
        fw.close();

        file.setReadable(true, false);
        file.setWritable(true, false);

       
        return path;
        
    }

    private Key getTomcatPrivateKey (String certificateSKI) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        KeyStore ks = KeyStore.getInstance("Luna");
		ByteArrayInputStream is1 = new ByteArrayInputStream(("slot:1").getBytes());
		
        ks.load(is1, null);

        Key privateKey = null;

        try
        {
            privateKey = ks.getKey(certificateSKI, "password".toCharArray());
        }
        catch (UnrecoverableKeyException uke)
        {
        }

        return privateKey;
    }

    private X509Certificate getTomcatCertificate (String certificateSKI) throws CertificateException, IOException,  NoSuchAlgorithmException
    {
        
		ByteArrayInputStream is1 = new ByteArrayInputStream(("slot:1").getBytes());
		
        X509Certificate certificate = null;
		try
        {
			KeyStore ks = KeyStore.getInstance("Luna");
			ks.load(is1, null);
			certificate = (X509Certificate) ks.getCertificate(certificateSKI);
			
		}
        catch (KeyStoreException uke)
        {
			uke.printStackTrace();
        }
		catch (CertificateException uke)
        {
			uke.printStackTrace();
        }
        return certificate;
    }

}
