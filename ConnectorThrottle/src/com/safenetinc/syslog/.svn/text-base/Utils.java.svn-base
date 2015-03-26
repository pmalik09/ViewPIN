package com.safenetinc.syslog;

public class Utils 
{
	private Utils() 
	{
		super();
	}

	public static boolean isDigitCharacter(int c)
	{
		boolean rc;

		rc = false;

		if(c >= '0' && c <= '9')
		{
			rc = true;
		}

		return rc;
	}

	public static boolean isLetterCharacter(int c)
	{
		boolean rc;

		rc = false;

		if(c >= 'A' && c <= 'Z')
		{
			rc = true;
		}
		else
		{
			if(c >= 'a' && c <= 'z')
			{
				rc = true;
			}	
			else
			{
				rc = false;
			}
		}

		return rc;
	}

	public static boolean isLetterOrDigitCharacter(int c)
	{
		boolean rc;

		rc = false;

		if( (isLetterCharacter(c) == true) || (isDigitCharacter(c) == true) )
		{
			rc = true;
		}

		return rc;
	}

	public static boolean isPrintableCharacter(int c)
	{
		boolean rc;

		rc = false;

		if( c >= 32 && c <= 126)
		{
			rc = true;
		}

		return rc;
	}
}