package com.latour.tftp;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * ByteBufferUtil handles null terminated strings
 * @author David Latour
 *
 */
public class ByteBufferUtil
{
	private static final Charset asciCharset = Charset.forName("US-ASCII");
	public static String readString(ByteBuffer receiveBuffer)
	{
		int startPos = receiveBuffer.position();
		byte[] array = receiveBuffer.array();
		int stringLen = 0;
		for (int i = startPos; i < array.length && array[i] != 0; stringLen++, i++)
		{
		}
		String str = new String(array, startPos, stringLen, asciCharset);
		for (int i = 0; i <= stringLen; i++)
		{
			receiveBuffer.get();
		}
		return str;
	}

	public static void putString(ByteBuffer buffer, String inString)
	{
		byte[] stringBytes = inString.getBytes(asciCharset);
		buffer.put(stringBytes);
		buffer.put((byte)0);
	}
}
