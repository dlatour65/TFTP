package com.latour.tftp;

import java.net.SocketAddress;

/**
 * AckMessage holds the data needed for sending or retrieving an ACK message
 * @author David Latour
 *
 */

public class AckMessage implements Request
{
	private final short blockNumber;
	private final SocketAddress senderAddress;

	public AckMessage(short blockNumber, SocketAddress senderAddress)
	{
		this.blockNumber = blockNumber;
		this.senderAddress = senderAddress;
	}

	@Override
	public SocketAddress getSenderAddress()
	{
		return senderAddress;
	}

	@Override
	public OpCode getOpCode()
	{
		return OpCode.ACK;
	}

	public short getBlockNumber()
	{
		return blockNumber;
	}
}
