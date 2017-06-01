package com.latour.tftp;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class DataSerializerTest
{
	@Test
	public void testSerialize()
	{
		byte[] expected = {0x00, 0x03, 0x02, (byte)0x57, 0x01, 0x02, 0x03, 0x04};
		byte[] data = {0x01, 0x02, 0x03, 0x04};
		short block = 599;
		DataSerializer serializer = new DataSerializer();
		DataMessage message = new DataMessage(block, data, null);
		ByteBuffer serialized = serializer.serialize(message);
		byte[] actual = new byte[serialized.remaining()];
		serialized.get(actual);
		Assert.assertArrayEquals(expected, actual);
	}
}
