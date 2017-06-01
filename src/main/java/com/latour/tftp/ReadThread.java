package com.latour.tftp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * ReadThread is used by the Server to read any messages on the UDP port.
 * A request message is created from any message and sent to the MessageHandler.
 * 
 * @author David Latour
 * @see MessageHander
 */

public class ReadThread extends Thread
{
	private final DatagramChannel channel;
	private final MessageHandler messageHandler;
	private boolean running;
	private final ByteBuffer receiveBuffer;

	public ReadThread(DatagramChannel channel, MessageHandler messageHandler)
	{
		super("ReadThread");
		this.channel = channel;
		this.messageHandler = messageHandler;
		running = true;
		receiveBuffer = ByteBuffer.allocate(1024);
	}

	@Override
	public void run()
	{
		while (running)
		{
			try
			{
				receiveBuffer.clear();
				SocketAddress senderAddress = channel.receive(receiveBuffer);
				if (senderAddress != null)
				{
					receiveBuffer.flip();
					messageHandler.handleMessage(readRequest(senderAddress));
				}

			} catch (IOException e)
			{
				sendError(e);
			}
		}
	}

	private void sendError(IOException e)
	{
		// TODO Auto-generated method stub

	}

	private Request readRequest(SocketAddress senderAddress)
	{
		Request message = null;
		short opCodeShort = receiveBuffer.getShort();
		OpCode opCode = OpCode.valueOf(opCodeShort);
		switch (opCode)
		{
			case ACK:
				message = doAck(senderAddress);
				break;
			case DATA:
				message = doDataRequest(senderAddress);
				break;
			case ERROR:
				message = doError(senderAddress);
				break;
			case RRQ:
				message = doReadRequest(senderAddress);
				break;
			case WRQ:
				message = doWriteRequest(senderAddress);
				break;
			default:
				break;
		}
		return message;
	}

	private Request doAck(SocketAddress senderAddress)
	{
		short blockNumber = receiveBuffer.getShort();
		AckMessage message = new AckMessage(blockNumber, senderAddress);
		return message;
	}

	private Request doDataRequest(SocketAddress senderAddress)
	{
		short blockNumber = receiveBuffer.getShort();
		byte[] data = new byte[receiveBuffer.remaining()];
		receiveBuffer.get(data);
		DataMessage message = new DataMessage(blockNumber, data, senderAddress);
		return message;
	}

	private Request doError(SocketAddress senderAddress)
	{
		return null;
	}

	private Request doReadRequest(SocketAddress senderAddress)
	{
		String fileName = ByteBufferUtil.readString(receiveBuffer);
		String mode = ByteBufferUtil.readString(receiveBuffer);
		ReadRequest message = new ReadRequest(fileName, mode, senderAddress);
		return message;
	}

	private WriteRequest doWriteRequest(SocketAddress senderAddress)
	{
		String fileName = ByteBufferUtil.readString(receiveBuffer);
		String mode = ByteBufferUtil.readString(receiveBuffer);
		WriteRequest message = new WriteRequest(fileName, mode, senderAddress);
		return message;
	}

	public void stopReading()
	{
		running = false;
		this.interrupt();
	}
}
