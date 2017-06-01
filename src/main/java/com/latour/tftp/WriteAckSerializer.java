package com.latour.tftp;

import java.nio.ByteBuffer;

/**
 * WriteAckSerializer will serialize the WriteAcknowledge message.
 * 
 * @see WriteAcknowledge
 * 
 * @author David
 *
 */

public class WriteAckSerializer implements Serializer
{
	private static final int RESPONSE_SIZE = 4;

	@Override
	public ByteBuffer serialize(Response response)
	{
		ByteBuffer buffer = ByteBuffer.allocate(RESPONSE_SIZE);
		WriteAcknowlege writeAck = (WriteAcknowlege)response;
		buffer.putShort(OpCode.ACK.getValue());
		buffer.putShort(writeAck.getBlockNumber());
		buffer.flip();
		return buffer;
	}
}
