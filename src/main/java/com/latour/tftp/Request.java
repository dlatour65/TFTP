package com.latour.tftp;

import java.net.SocketAddress;

/**
 * Request is an interface that encapsulates the requests that are sent to the server.
 * 
 * @see ReadRequest
 * @see WriteRequest
 * @see DataMessage
 * @see AckMessage
 * 
 * @author David Latour
 *
 */

public interface Request
{
	SocketAddress getSenderAddress();
	OpCode getOpCode();
}
