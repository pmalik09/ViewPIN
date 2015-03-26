package com.safenetinc.viewpin.authority.sjh.eft.cvv;

import javax.crypto.SecretKey;

public class CardVerificationKeyPair
{
    private SecretKey keyA = null;
    private SecretKey keyB = null;

    public CardVerificationKeyPair(SecretKey keyA, SecretKey keyB)
    {
        super();

        setKeyA(keyA);
        setKeyB(keyB);
    }

    public SecretKey getKeyA()
    {
        return this.keyA;
    }

    private void setKeyA(SecretKey keyA)
    {
        this.keyA = keyA;
    }

    public SecretKey getKeyB()
    {
        return this.keyB;
    }

    private void setKeyB(SecretKey keyB)
    {
        this.keyB = keyB;
    }
}
