package com.latour.tftp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * WritePacket holds the raw data to be sent through UDP
 * The WriteQueue receives and sends WritePackets on its own thread.
 * 
 * @see WriteQueue
 * 
 * @author David Latour
 *
 */

public class WritePacket
{
	private final ByteBuffer data;
	private final SocketAddress senderAddress;
	private final OpCode opCode;
	
	public WritePacket(ByteBuffer data, SocketAddress senderAddress, OpCode opCode)
	{
		this.data = data;
		this.senderAddress = senderAddress;
		this.opCode = opCode;
	}
	public ByteBuffer getData()
	{
		return data;
	}
	public SocketAddress getSenderAddress()
	{
		return senderAddress;
	}
	public OpCode getOpCode()
	{
		return opCode;
	}
}
