

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

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
    
    public static int getInt() throws NoSuchAlgorithmException
    {
        int i;
        
        i = 0;
        
        initPool();
        
        if(getEntropyPool().get().remaining() < 4)
        {
            fill(INCREMENT_SIZE);
        }
        
        i = getEntropyPool().get().getInt();
        
        return i;
    }
    
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
        sr = SecureRandom.getInstance("LunaRNG");

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
    
    public static int getBytesRemaining()
    {
        return getEntropyPool().get().remaining();
    }
}