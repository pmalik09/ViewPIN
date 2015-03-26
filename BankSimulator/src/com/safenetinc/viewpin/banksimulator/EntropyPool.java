package com.safenetinc.viewpin.banksimulator;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class EntropyPool
{
    private static final int INITIAL_SIZE = 256;
    private static final int INCREMENT_SIZE = 256;
    private static final int BYTE_BYTE_LENGTH = 1;
    private static final int LONG_BYTE_LENGTH = 8;
    
    private static ThreadLocal<ByteBuffer> entropyPool = null;
    
    static
    {
        setEntropyPool(new ThreadLocal<ByteBuffer>());    
    }
    
    private EntropyPool()
    {
        super();
    }
        
    public static byte getByte() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        byte b;
        
        b = 0;
        
        initPool();
        
        if(getEntropyPool().get().remaining() < BYTE_BYTE_LENGTH)
        {
            fill(INCREMENT_SIZE);
        }
    
        b = getEntropyPool().get().get();
        
        return b;
    }
    
    public static byte[] getBytes(int length) throws NoSuchAlgorithmException, NoSuchProviderException
    {
        byte[] bytes;
        
        bytes = null;
        
        initPool();
        
        if(getEntropyPool().get().remaining() < length)
        {
            fill(INCREMENT_SIZE);
        }
        
        bytes = new byte[length];
        
        getEntropyPool().get().get(bytes);
        
        return bytes;
    }
    
    public static long getLong() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        long l;
        
        l = 0L;
     
        initPool();
        
        if(getEntropyPool().get().remaining() < LONG_BYTE_LENGTH)
        {
            fill(INCREMENT_SIZE);
        }
        
        l = getEntropyPool().get().getLong();
        
        return l;
    }
    
    private static void initPool() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        if(getEntropyPool().get() == null)
        {
            fill(INITIAL_SIZE);
        }
    }
    
    private static void fill(int length) throws NoSuchAlgorithmException, NoSuchProviderException
    {
        SecureRandom sr;
        byte[] randomData;
        
        sr = null;
        randomData = null;

        // Get random number generator instance
        sr = SecureRandom.getInstance("LunaRNG", "LunaJCAProvider");
        
        // Create buffer to hold random data
        randomData = new byte[length];
        
        // Fill buffer with random data
        sr.nextBytes(randomData);
        
        // Wrap random data in a ByteBuffer
        getEntropyPool().set(ByteBuffer.wrap(randomData));
    }

    private static ThreadLocal<ByteBuffer> getEntropyPool()
    {
        return entropyPool;
    }

    private static void setEntropyPool(ThreadLocal<ByteBuffer> entropyPool)
    {
        EntropyPool.entropyPool = entropyPool;
    }
    
    public static int getBytesRemaining()
    {
        return getEntropyPool().get().remaining();
    }
}