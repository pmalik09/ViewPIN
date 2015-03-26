import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import com.safenetinc.viewpin.simulator.authority.PinAuthoritySimulator;

public class StressTest
{
    public StressTest()
    {
        super();
        
        Provider lunaJcaProvider = Security.getProvider("LunaJCAProvider");
        
        if(lunaJcaProvider != null)
        {
            lunaJcaProvider.clear();
        }
        else
        {
            System.out.println("LunaJCAProvider not found");
            
            return;
        }
        
        Provider lunaJceProvider = Security.getProvider("LunaJCEProvider");
        
        if(lunaJceProvider != null)
        {
            lunaJceProvider.clear();
        }
        else
        {
            System.out.println("LunaJCEProvider not found");
            
            return;
        }
        
        System.out.println("Luna providers cleared");
        
        lunaJcaProvider.put("SecureRandom.LunaRNG", "com.chrysalisits.crypto.LunaRandom");
        lunaJcaProvider.put("KeyStore.Luna", "com.chrysalisits.crypto.LunaKeyStore");
        lunaJcaProvider.put("Signature.MD5withRSA", "com.chrysalisits.crypto.LunaSignatureMD5withRSA");
        lunaJcaProvider.put("Signature.SHA1withRSA", "com.chrysalisits.crypto.LunaSignatureSHA1withRSA");
        lunaJcaProvider.put("Signature.SHA256withRSA", "com.chrysalisits.crypto.LunaSignatureSHA256withRSA");
        lunaJcaProvider.put("Signature.SHA384withRSA", "com.chrysalisits.crypto.LunaSignatureSHA384withRSA");
        lunaJcaProvider.put("Signature.SHA512withRSA", "com.chrysalisits.crypto.LunaSignatureSHA512withRSA");
        lunaJcaProvider.put("KeyStore.LunaMP", "com.chrysalisits.crypto.LunaKeyStoreMP");
        lunaJcaProvider.put("AlgorithmParameters.OAEP", "com.chrysalisits.crypto.LunaParametersOAEP");
        lunaJcaProvider.put("MessageDigest.SHA", "com.chrysalisits.crypto.SHA");
        lunaJcaProvider.put("Alg.Alias.MessageDigest.SHA1", "SHA");
        lunaJcaProvider.put("Alg.Alias.MessageDigest.SHA-11", "SHA");
        lunaJcaProvider.put("AlgorithmParameters.IV", "com.chrysalisits.crypto.LunaParametersIv");
        lunaJceProvider.put("Alg.Alias.Cipher.RSA", "RSA//NoPadding");
        lunaJceProvider.put("Cipher.RSA//PKCS1v1_5", "com.chrysalisits.cryptox.LunaCipherRSAPKCS");
        lunaJceProvider.put("Alg.Alias.Cipher.RSA//PKCS1Padding", "RSA//PKCS1v1_5");
        lunaJceProvider.put("Cipher.RSA//OAEPWithSHA1AndMGF1Padding", "com.chrysalisits.cryptox.LunaCipherRSAOAEP");
        lunaJceProvider.put("KeyGenerator.LunaDESede", "com.chrysalisits.cryptox.LunaKeyGeneratorDes3");
        lunaJceProvider.put("KeyGenerator.LunaAES", "com.chrysalisits.cryptox.LunaKeyGeneratorAes");
        lunaJceProvider.put("Cipher.LunaDESede/CBC/ISO10126Padding", "com.chrysalisits.cryptox.LunaCipherDES3CbcISO10126Pad");
        lunaJceProvider.put("Cipher.LunaAES/CBC/ISO10126Padding", "com.chrysalisits.cryptox.LunaCipherAEScbcISO10126Pad");
        lunaJceProvider.put("Cipher.LunaAES/CBC/NoPadding", "com.chrysalisits.cryptox.LunaCipherAESCbc");
    }
    
    private void startStressTest(int totalThreads, int totalRequests) throws Exception
    {
        ThreadGroup threadGroup;
        ArrayList<PinRetrievalRequestGenerator> generators;
        PinRetrievalRequestGenerator nextGenerator;
        
        threadGroup = null;
        generators = null;
        nextGenerator = null;
        
        SecureRandom sr = SecureRandom.getInstance("LunaRNG");
        //sr.setSeed(new byte[20]);
        //sr.nextBytes(new byte[20]);
        
        //KeyStore agentKeyStore = initKeyStore(new File("agent.ks"), "password".toCharArray());
        //KeyStore authorityKeyStore = initKeyStore(new File("authority.ks"), "password".toCharArray());
        
        KeyStore agentKeyStore = KeyStore.getInstance("Luna");
        agentKeyStore.load(null, null);
        KeyStore authorityKeyStore = KeyStore.getInstance("Luna");
        authorityKeyStore.load(null, null);
        
        
        //PinAuthoritySimulator pinAuthoritySimulator = new PinAuthoritySimulator("bcaa8af10935712df2a7be810deaecff0ac207e1", "/usr-xfiles", sr, agentKeyStore, authorityKeyStore);
        PinAuthoritySimulator pinAuthoritySimulator = new PinAuthoritySimulator("44e7f83fed8f8540ddefba876b293f71a15eb422", "/usr-xfiles", sr, agentKeyStore, authorityKeyStore);
        //SSLContext sc = null;
        //sc = z(sr);
        //y(sc);
       
        long start = System.currentTimeMillis();
        
        generators = new ArrayList<PinRetrievalRequestGenerator>();
        
        threadGroup = new ThreadGroup("anonymous");
        
        ArrayList<Thread> threads = new ArrayList<Thread>();
        Thread nextThread = null;
        
        for(int i = 0; i < totalThreads; i++)
        {
            nextGenerator = new PinRetrievalRequestGenerator(totalRequests, 
                pinAuthoritySimulator, "lunasp2.lab.local", "vp.viewmypin.co.uk", 8445);
                
            generators.add(nextGenerator);
            nextThread = new Thread(threadGroup, nextGenerator);
            threads.add(nextThread);
            nextThread.start();
        }
        
        // Wait until all threads have completed
        for(int i = 0; i < threads.size(); i++)
        {
            nextThread = threads.get(i);
  
            System.out.println("joining thread " + nextThread.toString());
            nextThread.join();
            System.out.println("joined thread " + nextThread.toString());
        }
        
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("elasped = " + elapsed);
        
        double average = (double)elapsed / (double)(totalThreads * totalRequests);
        
        System.out.println(average + "(ms)");
        System.out.println(1000.0D / average + "(s)");
        
        System.out.println("total errors = " + calculateTotalErrors(generators));
    }
    
    
    private int calculateTotalErrors(ArrayList<PinRetrievalRequestGenerator> generators)
    {
        int totalErrors;
        PinRetrievalRequestGenerator nextGenerator;
        
        totalErrors = 0;
        nextGenerator = null;
        
        for(int i = 0; i < generators.size(); i++)
        {
            nextGenerator = generators.get(i);
            
            totalErrors += nextGenerator.getTotalErrors();
        }
        
        return totalErrors;
    }
    
    public static KeyStore initKeyStore(File keyStoreFile, char[] keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        KeyStore ks;
        FileInputStream fis;
        
        ks = null;
        fis = null;
        
        try
        {
            fis = new FileInputStream(keyStoreFile);
            
            ks = KeyStore.getInstance("JKS");
            ks.load(fis, keyStorePassword);
        }
        finally
        {
            if(fis != null)
            {
                fis.close();
            }
        }
        
        return ks;
    }
   
    public static void main(String[] args)
    {
        int totalThreads = 1;
        int totalRequests = 10;
        
        if(args.length == 1)
        {
            totalThreads = Integer.parseInt(args[0]);
        }
        
        if(args.length == 2)
        {
            totalThreads = Integer.parseInt(args[0]);
            totalRequests = Integer.parseInt(args[1]);
        }
        
        try
        {
            new StressTest().startStressTest(totalThreads, totalRequests);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}