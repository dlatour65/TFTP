package com.latour.tftp;

import java.nio.ByteBuffer;

/**
 * DataSerializer will serialize the DataMessage
 * 
 * @author David Latour
 *
 */

public class DataSerializer implements Serializer
{
	@Override
	public ByteBuffer serialize(Response response)
	{
		DataMessage message = (DataMessage)response;
		ByteBuffer buffer = ByteBuffer.allocate(message.getData().length + 4); // opcode and block #
		buffer.putShort(OpCode.DATA.getValue());
		buffer.putShort(message.getBlockNumber());
		buffer.put(message.getData());
		buffer.flip();
		return buffer;
	}
}
