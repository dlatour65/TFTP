package com.latour.tftp;

import java.net.SocketAddress;

/**
 * WriteRequest contains the information for sending a WRQ.
 * 
 * @author David Latour
 *
 */

public class WriteRequest implements Request
{
	private final String filename;
	private final String mode;
	private final SocketAddress senderAddress;

	public WriteRequest(String filename, String mode, SocketAddress senderAddress)
	{
		this.filename = filename;
		this.mode = mode;
		this.senderAddress = senderAddress;
	}

	public String getFilename()
	{
		return filename;
	}

	public String getMode()
	{
		return mode;
	}

	public SocketAddress getSenderAddress()
	{
		return senderAddress;
	}

	public OpCode getOpCode()
	{
		return OpCode.WRQ;
	}
}
