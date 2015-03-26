package com.safenetinc.viewpin.authority.sjh.common;

public class NibbleBuffer 
{
	private byte[] buffer = null;
	private int currentByteIndex = 0;
	private boolean highNibbleOccupied = false;
	private int totalNibbles = 0;
	
	public NibbleBuffer(int size)
	{
		super();
		
		setBuffer(new byte[size]);
	}
	
	public NibbleBuffer(byte[] buffer)
	{
		setBuffer(buffer);
		this.highNibbleOccupied = true;
		setTotalNibbles(buffer.length * 2);
	}
	
	public void appendNibble(byte nibble) // Nibble to be appended must be in low order nibble of byte passed in
	{
		// Ignore high nibble if it was set
		nibble &= 0x0000000F; 
		
		// Is the high order nibble of the current byte already occupied?
		if(this.highNibbleOccupied == false)
		{
			// Place nibble in high order nibble of current byte
			this.buffer[currentByteIndex] = (byte)(nibble << 4);

			// Indicate that high order nibble of current byte is occupied
			this.highNibbleOccupied = true;
		}
		else
		{
			// Place nibble in low order nibble of current byte
			this.buffer[currentByteIndex] |= nibble;
			
			// Move onto next byte
			this.currentByteIndex++;
			
			// Since we are moving onto next byte then high order nibble will be empty and therefore not occupied
			this.highNibbleOccupied = false;
		}
		
		incrementTotalNibbles();
	}
	
	public byte getNextNibble()
	{
		byte nextNibble = 0;
		
		if(this.highNibbleOccupied == true)
		{
			nextNibble = (byte)((this.buffer[currentByteIndex] >> 4) & 0x0000000F);
			this.highNibbleOccupied = false;
		}
		else
		{
			nextNibble = (byte)((this.buffer[currentByteIndex]) & 0x0000000F);
			this.highNibbleOccupied = true;
			this.currentByteIndex++;
		}
		
		return nextNibble;
	}
	
	public void append(String s)
	{
		byte[] bytes = s.getBytes();
	
		// Append each character from string as a nibble to the nibble buffer
		for(int i = 0; i < bytes.length; i++)
		{
			this.appendNibble(bytes[i]);
		}
	}
	
	private void setBuffer(byte[] buffer)
	{
		this.buffer = buffer;
	}
	
	public byte[] getBuffer()
	{
		return this.buffer;
	}

	public int getTotalNibbles() 
	{
		return this.totalNibbles;
	}

	private void setTotalNibbles(int totalNibbles)
	{
		this.totalNibbles = totalNibbles;
	}
	
	private void incrementTotalNibbles()
	{
		this.totalNibbles++;
	}
	
	public void reset()
	{
		this.currentByteIndex = 0;
		this.highNibbleOccupied = true;
	}
}
