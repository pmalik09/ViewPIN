package com.safenetinc.syslog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Message 
{
	private Tag tag = null;
	private String content = null;

	private Message() 
	{
		super();
	}

	private Message(Tag tag, String content) 
	{
		super();

		setTag(tag);
		setContent(content);
	}

	public static Message getInstance(Tag tag, String content) throws InvalidTagException, InvalidContentException
	{
		Message m;

		m = null;

		// Ensure content content is valid
		validateContent(content);

		m = new Message(tag, content);

		return m;
	}

	private static void validateContent(String content) throws InvalidContentException
	{
		// Ensure content is not null
		if(content == null)
		{
			throw new InvalidContentException("is null");
		}

		// Ensure content is at least one character
		if(content.length() < 1)
		{
			throw new InvalidContentException("too small");
		}

		// Ensure content start character is valid
		if(isContentStartCharacterValid(content) == false)
		{
			throw new InvalidContentException("invalid start character");
		}

		// Ensure content characters are printable
		for(int i = 0; i < content.length(); i++)
		{
			if(Utils.isPrintableCharacter(content.charAt(i)) == false)
			{
				throw new InvalidContentException("illegal character");
			}
		}
	}

	private static boolean isContentStartCharacterValid(String content)
	{
		boolean rc;

		rc = false;

		switch(content.charAt(0))
		{
			case '[' :

			case ':' :

			case ' ' :

				rc = true;

				break;

		    default :

		    	rc = false;
		}

		return rc;
	}

	private void setTag(Tag tag)
	{
		this.tag = tag;
	}

	public Tag getTag()
	{
		return this.tag;
	}

	private void setContent(String content)
	{
		this.content = content;
	}

	public String getContent()
	{
		return this.content;
	}

	public byte[] toByteArray() throws IOException
	{
		ByteArrayOutputStream baos;

		baos = null;

		try
		{
			baos = new ByteArrayOutputStream();

			baos.write(getTag().getTag().getBytes());
			baos.write(getContent().getBytes());
		}
		finally
		{
			if(baos != null)
			{
				baos.close();
			}
		}
		if(baos!= null)
			return baos.toByteArray();
		else return null;
	}
}