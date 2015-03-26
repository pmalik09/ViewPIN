// $Id: PinAuthoritySimulator/src/com/safenetinc/viewpin/simulator/authority/CreateKeyStore.java 1.1 2008/09/04 10:48:55IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.x509.X509V3CertificateGenerator;

/**
 * Class to create a key store for use with the PinAuthority simulator
 * 
 * @author Stuart Horler
 */
public class CreateKeyStore
{
    private static final String AUTHORITY_KEYSTORE_NAME = "authority.ks";

    private static final String DEFAULT_PASSWORD        = "password";

    /**
     * Default constructor
     */
    public CreateKeyStore()
    {
        super();
    }

    /**
     * Creates a key store with name authority.ks containing the RSA key pairs required by a PINAuthority
     * 
     * @throws Exception Thrown if an error occurs during key store creation
     */
    private void createKeyStore () throws Exception
    {
        System.out.println("Creating PINAuthority Simulator key store");
        KeyStore ks;

        ks = null;

        ks = KeyStore.getInstance("JKS");
        ks.load(null, DEFAULT_PASSWORD.toCharArray());

        System.out.println("Generating the PINAuthority Signing Certificate");
        String signingCertificateSubjectKeyIdentifier = generateCertificate("authority signing", 1024, ks, "Signing");
        System.out.println("signing certificate subject key identifier = " + signingCertificateSubjectKeyIdentifier);

        System.out.println("Generating the PINAuthority Wrapping Certificate");
        String wrappingCertificateSubjectKeyIdentifier = generateCertificate("authority wrapping", 1024, ks, "Wrapping");
        System.out.println("wrapping certificate subject key identifier = " + wrappingCertificateSubjectKeyIdentifier);

        // Store the certificates in a new java key store
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(AUTHORITY_KEYSTORE_NAME);
            ks.store(fos, DEFAULT_PASSWORD.toCharArray());
        }
        finally
        {
            if (fos != null)
                fos.close();
        }
        System.out.println("Certificates generated successfully.");
        System.out.println("Certificates stored in keystore " + AUTHORITY_KEYSTORE_NAME + " please amend"); 
        System.out.println("the BankSimulatorConfig file to point to this keystore");
        System.out.println("The certificates have also been exported to the local directory as DER");
        System.out.println("encoded .cer extension files");
        System.out.println("import these .cer files into your PINAgent in order to interact with ");
        System.out.println("this PINAuthority Simulator.");
    }

    private String generateCertificate (String commonName, int keyLength, KeyStore keyStore, String filenamePrefix) throws Exception
    {
        KeyPair kp = generateRsaKeyPair(keyLength, RSAKeyGenParameterSpec.F4);

        Hashtable<DERObjectIdentifier, String> attributes = new Hashtable<DERObjectIdentifier, String>();
 
        attributes.put(X509Name.C, "c");
        attributes.put(X509Name.ST, "st");
        attributes.put(X509Name.L, "l");
        attributes.put(X509Name.O, "o");
        attributes.put(X509Name.OU, "ou");
        attributes.put(X509Name.CN, commonName);

        Vector<DERObjectIdentifier> order = new Vector<DERObjectIdentifier>();
        order.addElement(X509Name.C);
        order.addElement(X509Name.ST);
        order.addElement(X509Name.L);
        order.addElement(X509Name.O);
        order.addElement(X509Name.OU);
        order.addElement(X509Name.CN);

        X509V3CertificateGenerator versionThreeCertificateGenerator = new X509V3CertificateGenerator();
        versionThreeCertificateGenerator.reset();

        versionThreeCertificateGenerator.setSerialNumber(BigInteger.valueOf(10));
        versionThreeCertificateGenerator.setIssuerDN(new X509Principal(order, attributes));
        versionThreeCertificateGenerator.setSubjectDN(new X509Principal(order, attributes));
        versionThreeCertificateGenerator.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
        versionThreeCertificateGenerator.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)));
        versionThreeCertificateGenerator.setPublicKey(kp.getPublic());
        versionThreeCertificateGenerator.setSignatureAlgorithm("SHA1withRSA");

        SubjectKeyIdentifier ski = null;
        org.bouncycastle.asn1.ASN1InputStream asn1InputStream = null;
        try
        {
            asn1InputStream = new org.bouncycastle.asn1.ASN1InputStream(kp.getPublic().getEncoded());
            org.bouncycastle.asn1.ASN1Sequence sequence = (org.bouncycastle.asn1.ASN1Sequence) asn1InputStream.readObject();
            SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo(sequence);
            ski = new SubjectKeyIdentifier(spki);
        }
        finally
        {
            if (asn1InputStream != null)
                asn1InputStream.close();
        }

        versionThreeCertificateGenerator.addExtension(X509Extensions.SubjectKeyIdentifier, false, ski);

        X509Certificate cert = versionThreeCertificateGenerator.generate(kp.getPrivate());

        Certificate[] certChain = new Certificate[1];

        certChain[0] = cert;

        String keyEntryAlias = new String(Hex.encode(ski.getKeyIdentifier()));

        keyStore.setKeyEntry(keyEntryAlias, kp.getPrivate(), DEFAULT_PASSWORD.toCharArray(), certChain);

        String hexEncodedSubjectKeyIdentifier = new String(Hex.encode(ski.getKeyIdentifier()));

        exportCertificate(hexEncodedSubjectKeyIdentifier, cert, filenamePrefix);

        return hexEncodedSubjectKeyIdentifier;
    }

    private void exportCertificate (String subjectKeyIdentifier, X509Certificate certificate, String filenamePrefix) throws Exception
    {
        File f;
        FileOutputStream fos;

        f = null;
        fos = null;

        f = new File(filenamePrefix + "-" + subjectKeyIdentifier + ".cer");

        try
        {
            fos = new FileOutputStream(f);
            fos.write(certificate.getEncoded());
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }
        }
    }

    /**
     * Generates an RSA key pair
     * 
     * @param keySize The size of the keys
     * @param publicExponent Exponent for the keys
     * @return A newly created {@link KeyPair}
     * @throws NoSuchAlgorithmException Thrown if the RSA algorithn cannot be found by the underlying JCE
     *         provider
     * @throws NoSuchProviderException Thrown if the provider configured for the system can't be found
     * @throws InvalidAlgorithmParameterException Thrown if the keySize or publicExponent were invalid
     */
    public static KeyPair generateRsaKeyPair (int keySize, BigInteger publicExponent) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
    {
        KeyPair kp;
        KeyPairGenerator kpg;
        RSAKeyGenParameterSpec kgps;

        kp = null;
        kpg = null;
        kgps = null;

        kpg = KeyPairGenerator.getInstance("RSA");
        kgps = new RSAKeyGenParameterSpec(keySize, publicExponent);
        kpg.initialize(kgps);
        kp = kpg.generateKeyPair();

        return kp;
    }

    /**
     * Main method used to invoke the application
     * 
     * @param args The command line arguments, not used
     * @throws Exception Thrown if an error occurs during key store creation
     */
    public static void main (String[] args) throws Exception
    {
        new CreateKeyStore().createKeyStore();
    }
}