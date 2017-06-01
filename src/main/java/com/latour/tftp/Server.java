package com.latour.tftp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Server is the main class to start the TFTP server.
 * There are no program arguments, but there is a properties file.
 * @see Configuration
 * 
 * @author David Latour
 *
 */

public class Server
{
	private final Configuration configuration;
	private final MessageHandler messageHandler;
	private WriteQueue writeQueue;
	private ReadThread readThread;

	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
		server.startListening();
	}

	public Server()
	{
		this.configuration = Configuration.getInstance();
		this.messageHandler = new MessageHandler(this);
	}

	public void startListening() throws IOException
	{
		DatagramChannel udpChannel = createChannel();
		readThread = new ReadThread(udpChannel, messageHandler);
		readThread.start();
		writeQueue = WriteQueue.createQueue(udpChannel);
		System.out.println("TFTP Server listening on port " + configuration.getListeningPort());
	}

	private DatagramChannel createChannel() throws IOException
	{
		DatagramChannel channel = DatagramChannel.open();
		channel.socket().bind(new InetSocketAddress(configuration.getListeningPort()));
		channel.configureBlocking(true);
		return channel;
	}

	public void stopListening()
	{
		readThread.stopReading();
		writeQueue.stopWriting();
	}

	public void sendResponse(Response response)
	{
		Serializer serializer = response.getSerializer();
		ByteBuffer serializedData = serializer.serialize(response);
		writeQueue.add(new WritePacket(serializedData, response.getSenderAddress(), response.getOpCode()));
	}
}
