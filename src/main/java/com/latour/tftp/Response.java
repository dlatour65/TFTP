package com.latour.tftp;

import java.net.SocketAddress;

/**
 * Response is an interface that encapsulates the responses that are sent from
 * the server.
 *
 * @see DataMessage
 * @see WriteAcknowledge
 * 
 * @author David Latour
 *
 */
public interface Response
{
	SocketAddress getSenderAddress();

	Serializer getSerializer();

	OpCode getOpCode();
}
