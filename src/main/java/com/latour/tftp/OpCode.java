package com.latour.tftp;

/** 
 * OpCode is an enum to identify the different types of messages that can be sent.
 * 
 * @author David Latour
 *
 */
public enum OpCode
{
	RRQ((short)1), // Read request
	WRQ((short)2), // Write request
	DATA((short)3), // Data
	ACK((short)4), // Acknowledge
	ERROR((short)5); // Error

	private final short opCode;

	private OpCode(short opCode)
	{
		this.opCode = opCode;
	}

	public short getValue()
	{
		return opCode;
	}

	public static OpCode valueOf(short inValue)
	{
		OpCode returnValue = ERROR;
		for (OpCode code : OpCode.values())
		{
			if (code.opCode == inValue)
			{
				returnValue = code;
				break;
			}
		}
		return returnValue;
	}
}
