package com.safenetinc.viewpin.banksimulator;

public class Utils 
{
	private Utils()
	{
		super();
	}
	
	public static byte[] xor(byte[] a, byte[] b)
    {
        byte[] c = new byte[a.length];

        for(int i = 0; i < a.length; i++)
        {
            c[i] = (byte)((int)a[i] ^ (int)b[i]);
        }

        return c;
    }
}
