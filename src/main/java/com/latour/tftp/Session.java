package com.latour.tftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * Session maintains the state of the file transfers, and also creates response
 * messages according to the state.
 * 
 * @author David Latour
 *
 */

public class Session
{
	private static final int MAX_SIZE = 512;
	private final Request originalRequest;
	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private boolean finished;
	private short blockNumber;

	public Session(Request request)
	{
		this.originalRequest = request;
		finished = false;
		try
		{
			init();
		} catch (IOException e)
		{
			sendError(e);
		}
	}

	private void init() throws IOException
	{
		switch (originalRequest.getOpCode())
		{
			case RRQ:
				openFile();
				break;
			case WRQ:
				createFile();
				break;
			default:
				// something is wrong
				break;
		}
	}

	private void openFile() throws FileNotFoundException
	{
		File dropDirectory = Configuration.getInstance().getWriteDirectory();
		ReadRequest request = (ReadRequest) originalRequest;
		String fileName = request.getFilename();
		File fileToSend = new File(dropDirectory, fileName);
		if (fileToSend.exists())
		{
			inputStream = new DataInputStream(new FileInputStream(fileToSend));
		} else
		{
			sendError("File not found");
		}
	}

	public Request getOriginalRequest()
	{
		return originalRequest;
	}

	public void addData(DataMessage request)
	{
		if (outputStream != null)
		{
			try
			{
				outputStream.write(request.getData());
				outputStream.flush();
				if (request.getData().length < MAX_SIZE)
				{
					finished = true;
					outputStream.close();
				}
			} catch (IOException e)
			{
				sendError(e);
			}
		}
	}

	private void createFile() throws IOException
	{
		File dropDirectory = Configuration.getInstance().getWriteDirectory();
		WriteRequest request = (WriteRequest) originalRequest;
		String fileName = request.getFilename();
		File file = new File(dropDirectory, fileName);
		if (file.exists())
		{
			file.delete();
		}
		file.createNewFile();
		outputStream = new DataOutputStream(new FileOutputStream(file));
	}

	public DataMessage createDataMessage()
	{
		DataMessage message = null;
		try
		{
			int length = 0;
			if (inputStream != null && inputStream.available() > 0)
			{
				length = MAX_SIZE;
				if (inputStream.available() < MAX_SIZE)
				{
					length = inputStream.available();
					finished = true;
				}
				byte[] bufferArray = new byte[length];
				inputStream.readFully(bufferArray);
				message = new DataMessage(++blockNumber, bufferArray, originalRequest.getSenderAddress());
			} else
			{
				finished = true;
			}
		} catch (IOException e)
		{
			sendError(e);
		}
		return message;
	}

	public boolean isFinished()
	{
		return finished;
	}

	private void sendError(IOException e)
	{
		// TODO Auto-generated method stub
	}

	private void sendError(String string)
	{
		// TODO Auto-generated method stub
	}

	public SocketAddress getSenderAddress()
	{
		return originalRequest.getSenderAddress();
	}

	public DataMessage addAck(AckMessage request)
	{
		return createDataMessage();
	}
}
