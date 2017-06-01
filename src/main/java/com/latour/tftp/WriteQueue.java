package com.latour.tftp;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * WriteQueue queues WritePackets and sends them to the UDP channel
 * 
 * @see WritePacket
 * 
 * 
 * @author David Latour
 *
 */

public class WriteQueue extends Thread
{
	public static WriteQueue createQueue(DatagramChannel udpChannel)
	{
		WriteQueue queue = new WriteQueue(udpChannel);
		queue.start();
		return queue;		
	}
	
	private final DatagramChannel udpChannel;
	private final BlockingQueue<WritePacket> queue;
	private boolean running;
	
	private WriteQueue(DatagramChannel udpChannel)
	{
		super("WriteQueue");
		this.udpChannel = udpChannel;
		queue = new LinkedBlockingQueue<WritePacket>();
		running = true;
	}

	public void add(WritePacket writePacket)
	{
		try
		{
			queue.put(writePacket);
		} catch (InterruptedException e)
		{
			// Do nothing. Will only be interrupted when we stop the thread
		}
	}
	
	@Override
	public void run()
	{
		while( running  )
		{
			try
			{
				WritePacket writePacket = queue.take();
				udpChannel.send(writePacket.getData(), writePacket.getSenderAddress());
			} catch (InterruptedException e)
			{
				// Do nothing. Will only be interrupted when we stop the thread
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

	public void stopWriting()
	{
		running = false;
		this.interrupt();
	}
}
