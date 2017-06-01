package com.latour.tftp;

import java.net.SocketAddress;

/**
 * WriteAcknowlede holds data needed for a WRQ.
 * 
 * 
 * @author David
 *
 */
public class WriteAcknowlege implements Response
{
	private final static Serializer serializer = new WriteAckSerializer();
	private final SocketAddress senderAddress;
	private final short blockNumber;
	
	public WriteAcknowlege(SocketAddress senderAddress, short blockNumber)
	{
		this.senderAddress = senderAddress;
		this.blockNumber = blockNumber;
	}

	@Override
	public Serializer getSerializer()
	{
		return serializer;
	}

	@Override
	public SocketAddress getSenderAddress()
	{
		return senderAddress;
	}

	public short getBlockNumber()
	{
		return blockNumber;
	}

	@Override
	public OpCode getOpCode()
	{
		return OpCode.ACK;
	}
}
