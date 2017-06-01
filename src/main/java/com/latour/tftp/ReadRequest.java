package com.latour.tftp;

import java.net.SocketAddress;

/**
 * ReadRequest contains the information for sending a RRQ.
 * 
 * @author David Latour
 *
 */

public class ReadRequest implements Request
{
	private final String filename;
	private final String mode;
	private final SocketAddress senderAddress;
	
	public ReadRequest(String filename, String mode, SocketAddress senderAddress)
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
		return OpCode.RRQ;
	}
}
