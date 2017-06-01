package com.latour.tftp;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class ByteBufferUtilTest
{
	@Test
	public void testPutString()
	{
		String testString = "foobar";
		ByteBuffer buffer = ByteBuffer.allocate(testString.length() + 1);
		ByteBufferUtil.putString(buffer, testString);
		buffer.flip();
		String returnString = ByteBufferUtil.readString(buffer);
		Assert.assertTrue(testString.equals(returnString));
	}
}
