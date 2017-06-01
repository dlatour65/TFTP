package com.latour.tftp;

import java.net.SocketAddress;

/**
 * DataMessage holds the information needed to send or read a DATA message.
 * 
 * @author David Latour
 *
 */

public class DataMessage implements Request, Response
{
	private final static Serializer serializer = new DataSerializer();

	private final SocketAddress senderAddress;
	private final short blockNumber;
	private final byte[] data;
	
	public DataMessage(short blockNumber, byte[] data, SocketAddress senderAddress)
	{
		this.blockNumber = blockNumber;
		this.data = data;
		this.senderAddress = senderAddress;
	}

	public short getBlockNumber()
	{
		return blockNumber;
	}

	public byte[] getData()
	{
		return data;
	}

	public SocketAddress getSenderAddress()
	{
		return senderAddress;
	}
	
	public OpCode getOpCode()
	{
		return OpCode.DATA;
	}

	@Override
	public Serializer getSerializer()
	{
		return serializer;
	}
}
