// $Id: PinAgent/src/com/safenetinc/viewpin/common/utils/EntropyPool.java 1.1 2008/09/04 10:47:06IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.utils;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import com.safenetinc.viewpin.agent.ViewPinConstants;

/**
 * Class to handle the supply of entropy to the PINAgent
 * 
 * @author Stuart Horler
 *
 *
 */
public class EntropyPool
{
    private static final int INITIAL_SIZE = 256;
    private static final int INCREMENT_SIZE = 256;
    private static final int BYTE_BYTE_LENGTH = 1;
    private static final int LONG_BYTE_LENGTH = 8;
    
    private static final Logger logger = Logger.getLogger(EntropyPool.class);
    private static ThreadLocal<ByteBuffer> entropyPool = null;
    
    static
    {
        setEntropyPool(new ThreadLocal<ByteBuffer>());    
    }
    
    private EntropyPool()
    {
        super();
    }
        
    /**
     * @return A random byte
     * @throws NoSuchAlgorithmException Thrown by the underlying RNG
     */
    public static byte getByte() throws NoSuchAlgorithmException
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
    
    /**
     * Get an array of randomly chosen bytes
     * @param length The length of the array that will be returned
     * @return The random byte array
     * @throws NoSuchAlgorithmException Thrown by the underlying RNG
     */
    public static byte[] getBytes(int length) throws NoSuchAlgorithmException
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
    
    /**
     * @return A random long value
     * @throws NoSuchAlgorithmException Thrown by the underlying RNG
     */
    public static long getLong() throws NoSuchAlgorithmException
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
    
    /**
     * Initialise the pool
     */
    private static void initPool() throws NoSuchAlgorithmException
    {
        if(getEntropyPool().get() == null)
        {
            fill(INITIAL_SIZE);
        }
    }
    
    private static void fill(int length) throws NoSuchAlgorithmException
    {
        SecureRandom sr;
        byte[] randomData;
        
        sr = null;
        randomData = null;

        getLogger().debug("filling entropy pool with " + length + " bytes");
        
        // Get random number generator instance
        sr = SecureRandom.getInstance(ViewPinConstants.RANDOM_NUMBER_GENERATOR_ALGORITHM_NAME);

        getLogger().debug("random number generator algorithm = " + sr.getAlgorithm() + ", provdider = " + sr.getProvider());
        
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
    
    private static Logger getLogger()
    {
        return EntropyPool.logger;
    }
    
    /**
     * @return The number of bytes remaining in this entropy pool
     */
    public static int getBytesRemaining()
    {
        return getEntropyPool().get().remaining();
    }
}