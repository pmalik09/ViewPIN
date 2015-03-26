package com.safenetinc.syslog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Sender 
{
	private static final int MAXIMUM_PACKET_LENGTH = 1024;
	
	public Sender()
	{
		super();
	}
	
	public void send(Packet packet, Socket remoteHostSocket) throws IOException
	{
		byte[] serialisedPacket;
		int serialisedPacketLength;
		
		serialisedPacket = null;
		serialisedPacketLength = 0;
		
		serialisedPacket = serialise(packet);
		
		if(serialisedPacket != null)
			serialisedPacketLength = serialisedPacket.length;
		
		if(serialisedPacketLength > MAXIMUM_PACKET_LENGTH)
		{
		    serialisedPacketLength =  MAXIMUM_PACKET_LENGTH;
		}
		
		remoteHostSocket.getOutputStream().write(serialisedPacket, 0, serialisedPacketLength);
		remoteHostSocket.getOutputStream().write("\n".getBytes());
	}
	
	public void send(Packet packet, InetSocketAddress remoteHostAddress) throws IOException
	{
		byte[] serialisedPacket;
		int serialisedPacketLength;
		DatagramSocket ds;
        DatagramPacket dp;
		
		serialisedPacket = null;
		serialisedPacketLength = 0;
		ds = null;
		dp = null;
		
		serialisedPacket = serialise(packet);
		if(serialisedPacket != null)
			serialisedPacketLength = serialisedPacket.length;
		
		if(serialisedPacketLength > MAXIMUM_PACKET_LENGTH)
		{
		    serialisedPacketLength =  MAXIMUM_PACKET_LENGTH;
		}
		
		try
		{
			ds = new DatagramSocket();
			
			// dead code no use.
			//if(dp != null)
			//ds.send(dp);
			
			dp = new DatagramPacket(serialisedPacket, serialisedPacketLength);
			
			ds.send(dp);
		}
		finally
		{
			if(ds != null)
			{
				ds.close();
			}
		}
	}
	
	private byte[] serialise(Packet packet) throws IOException
	{
	    ByteArrayOutputStream serialisedPacket;
		
		serialisedPacket = null;
	
		try
		{
			serialisedPacket = new ByteArrayOutputStream();
			serialisedPacket.write(packet.toByteArray());
		}
		finally
		{
			if(serialisedPacket != null)
			{
				serialisedPacket.close();
			}
		}
		if(serialisedPacket != null)
			return serialisedPacket.toByteArray();
		else return null;
	}
}