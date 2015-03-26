// $Id: PinAgent/src/com/safenetinc/viewpin/common/utils/UniversallyUniqueIdentifierGenerator.java 1.1 2008/09/04 10:47:08IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.utils;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Class to handle generation of UUIDs
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class UniversallyUniqueIdentifierGenerator
{
    private UniversallyUniqueIdentifierGenerator()
    {
        super();
    }

    /**
     * Generates a version four compliant UUID
     * 
     * @return the {@link UUID}
     * @throws NoSuchAlgorithmException Thrown if a problem occurs with the underlyingn entropy generation
     */
    public static UUID generateVersionFour () throws NoSuchAlgorithmException
    {
        UUID uuid;
        long mostSignificantRandomBits;
        long leastSignificantRandomBits;

        uuid = null;
        mostSignificantRandomBits = 0L;
        leastSignificantRandomBits = 0L;

        // Generate random most significant bits
        mostSignificantRandomBits = EntropyPool.getLong();

        // Generate random least significant bits
        leastSignificantRandomBits = EntropyPool.getLong();

        // Indicate version four UUID
        mostSignificantRandomBits &= 0xFFFFFFFFFFFF0FFFL;
        mostSignificantRandomBits |= 0x0000000000004000L;

        // Fill least significant bits of UUID with random values
        leastSignificantRandomBits &= 0x1FFFFFFFFFFFFFFFL;
        leastSignificantRandomBits |= 0x8000000000000000L;

        uuid = new UUID(mostSignificantRandomBits, leastSignificantRandomBits);

        return uuid;
    }
}