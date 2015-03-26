package com.safenetinc.syslog;

public class Tag 
{
	private static final int MAXIMUM_TAG_LENGTH = 32;

	private String tag = null;

	private Tag()
	{
		super();
	}

	private Tag(String tag)
	{
		super();

		setTag(tag);
	}

	public static Tag getInstance(String tag) throws InvalidTagException
	{
		Tag t;

		t = null;

		// Ensure tag is valid
		validateTag(tag);

		t = new Tag(tag);

		return t;
	}

	private static void validateTag(String tag) throws InvalidTagException
	{
        // Ensure tag is not null
	    if(tag == null)
	    {
	    	throw new InvalidTagException("is null");
	    }

	    // Ensure tag is not empty
	    if(tag.length() < 0)
	    {
	    	throw new InvalidTagException("too short");
	    }
	    
	    // Ensure tag is not too long
	    if(tag.length() > MAXIMUM_TAG_LENGTH)
	    {
	    	throw new InvalidTagException("too long");
	    }

	    // Ensure tag contains only supported characters
	    for(int i = 0; i < tag.length(); i++)
	    {
	    	if(Utils.isLetterCharacter(tag.charAt(i)) == false)
	    	{
	    		if(Utils.isDigitCharacter(tag.charAt(i)) == false)
		    	{
	    			throw new InvalidTagException("invalid character");
		    	}
	    	}
	    }
	}

	private void setTag(String tag)
	{
		this.tag = tag;
	}

	public String getTag()
	{
		return this.tag;
	}
}