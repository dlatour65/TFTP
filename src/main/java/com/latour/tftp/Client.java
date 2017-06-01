package com.latour.tftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Client is an incomplete implementation of the client side of TFTP specification
 * It is a command line class that takes four arguments.
 * 	1. host - ipaddress of the server
 *  2. GET or PUT - this is the operation that will be made upon the file.  
 *  	PUT will send a file to the server.
 *  	GET will retrieve a file from the server.
 *  3. fileDir - is the directory in which the file to be sent resides 
 *  	or the directory that the file will be created.
 *  4. filename - is the file name of the file to be sent or received.
 * @author David Latour
 *
 */

public class Client
{
	private static final String OCTET_STRING = "octet";
	private static final String GET_STRING = "GET";
	private static final String PUT_STRING = "PUT";
	private static final int MAX_SIZE = 512;

	private final ByteBuffer receiveBuffer;

	public static void main(String[] args) throws IOException
	{
		if (args.length != 4)
		{
			System.out.println("Usage: host [GET | PUT] fileDir filename");
			return;
		}
		Client client = new Client(args[0], args[1].toUpperCase(), args[2], args[3]);
		client.doIt();
	}

	private final String host;
	private final String operation;
	private final String fileDir;
	private final String fileName;
	private DatagramChannel channel;

	public Client(String host, String operation, String fileDir, String fileName)
	{
		this.host = host;
		this.operation = operation;
		this.fileDir = fileDir;
		this.fileName = fileName;
		receiveBuffer = ByteBuffer.allocate(1024);
	}

	private void doIt() throws IOException
	{
		channel = createChannel();
		if (GET_STRING.equals(operation))
		{
			doGetFile();
		} else if (PUT_STRING.equals(operation))
		{
			doPutFile();
		}
	}

	private void doGetFile() throws IOException
	{
		System.out.println("Sending read request");
		ByteBuffer buffer = ByteBuffer.allocate(2 + fileName.length() + 1 + OCTET_STRING.length() + 1);
		buffer.putShort(OpCode.RRQ.getValue());
		ByteBufferUtil.putString(buffer, fileName);
		ByteBufferUtil.putString(buffer, OCTET_STRING);
		buffer.flip();
		channel.write(buffer);
		retrieveFile();
	}

	private void retrieveFile() throws IOException
	{
		File fileParent = new File(fileDir);
		File fileToSave = new File(fileParent, fileName);
		fileToSave.createNewFile();
		DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(fileToSave));
		boolean done = false;
		do
		{
			DataMessage message = getData();
			if( message != null )
			{
				sendAck(message);
				writeToLocalFile(outputStream, message);
				if( message.getData().length < MAX_SIZE )
				{
					done = true;
				}
			}
			else
			{
				done = true;
			}
		} while( done == false );
		outputStream.close();
	}

	private void sendAck(DataMessage message) throws IOException
	{
		System.out.println("Sending ack");
		ByteBuffer buffer = ByteBuffer.allocate(4); // opcode, block number
		buffer.putShort(OpCode.ACK.getValue());
		buffer.putShort(message.getBlockNumber());
		buffer.flip();
		channel.write(buffer);
	}

	private void writeToLocalFile(DataOutputStream outputStream, DataMessage message) throws IOException
	{
		outputStream.write(message.getData(), 0, message.getData().length);
	}

	private DataMessage getData()
	{
		DataMessage message = null;
		SocketAddress address = readResponse();
		short opCodeShort = receiveBuffer.getShort();
		OpCode opCode = OpCode.valueOf(opCodeShort);
		switch (opCode)
		{
			case DATA:
				short blockNum = receiveBuffer.getShort();
				byte[] data = new byte[receiveBuffer.remaining()];
				receiveBuffer.get(data);
				message = new DataMessage(blockNum, data, address);
				break;
			case ERROR:
				handleError();
				break;
			default:
				break;
		}
		return message;
	}

	private void doPutFile() throws IOException
	{
		File fileParent = new File(fileDir);
		File fileToSend = new File(fileParent, fileName);
		sendWriteRequest(fileToSend);
		if (getAck() == false)
		{
			// something bad happened and the error has already been handled.
			return;
		}
		sendFile(fileToSend);
	}

	private boolean getAck()
	{
		boolean ackReceived = false;
		SocketAddress senderAddress = readResponse();
		if (senderAddress != null)
		{
			short opCodeShort = receiveBuffer.getShort();
			OpCode opCode = OpCode.valueOf(opCodeShort);
			switch (opCode)
			{
				case ACK:
					ackReceived = true;
					break;
				case ERROR:
					handleError();
					break;
				default:
					break;
			}
		}
		return ackReceived;
	}

	private DatagramChannel createChannel() throws IOException
	{
		DatagramChannel channel = DatagramChannel.open();
		channel.socket().connect(new InetSocketAddress(host, 69));
		channel.configureBlocking(true);
		return channel;
	}

	private void sendWriteRequest(File file) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(2 + file.getName().length() + 1 + OCTET_STRING.length() + 1);
		buffer.putShort(OpCode.WRQ.getValue());
		ByteBufferUtil.putString(buffer, file.getName());
		ByteBufferUtil.putString(buffer, OCTET_STRING);
		buffer.flip();
		channel.write(buffer);
	}

	private void sendFile(File fileToSend) throws IOException
	{
		byte[] buffer = new byte[MAX_SIZE];
		DataInputStream inputStream = new DataInputStream(new FileInputStream(fileToSend));
		for (short i = 1; inputStream.available() > 0; i++)
		{
			int length = MAX_SIZE;
			if (inputStream.available() < MAX_SIZE)
			{
				length = inputStream.available();
			}
			inputStream.readFully(buffer, 0, length);
			ByteBuffer byteBuffer = ByteBuffer.allocate(length + 4); // opCode and block #
			byteBuffer.putShort(OpCode.DATA.getValue());
			byteBuffer.putShort(i);
			byteBuffer.put(buffer, 0, length);
			byteBuffer.flip();
			sendData(byteBuffer);
			if (getAck() == false)
			{
				// something bad happened and the error has already been
				// handled.
				inputStream.close();
				return;
			}
		}
		inputStream.close();
	}

	private void sendData(ByteBuffer buffer) throws IOException
	{
		channel.write(buffer);
	}

	private SocketAddress readResponse()
	{
		SocketAddress senderAddress = null;
		try
		{
			receiveBuffer.clear();
			senderAddress = channel.receive(receiveBuffer);
			if (senderAddress != null)
			{
				receiveBuffer.flip();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return senderAddress;
	}

	private void handleError()
	{
		short errorCode = receiveBuffer.getShort();
		String message = ByteBufferUtil.readString(receiveBuffer);
		System.out.println("Error: " + errorCode + " Message: " + message);
	}
}
