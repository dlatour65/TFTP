package com.latour.tftp;

/**
 * MessageHandler will handle incoming messages.
 * It will usually send it to the SessionCollection to find the Session (state) 
 * of the file transfer
 * 
 * @author David Latour
 *
 */

public class MessageHandler
{
	private final Server tftpServer;
	private final SessionCollection sessionCollection;

	public MessageHandler(Server tftpServer)
	{
		this.tftpServer = tftpServer;
		sessionCollection = new SessionCollection();
	}

	private WriteAcknowlege createWriteAck(Request message, short blockNumber)
	{
		WriteAcknowlege response = new WriteAcknowlege(message.getSenderAddress(), blockNumber);
		return response;
	}

	public void handleMessage(Request request)
	{
		switch (request.getOpCode())
		{
			case ACK:
				handleAckMessage((AckMessage) request);
				break;
			case DATA:
				handleDataMessage((DataMessage) request);
				break;
			case ERROR:
				break;
			case RRQ:
				handleReadMessage((ReadRequest) request);
				break;
			case WRQ:
				handleWriteMessage((WriteRequest) request);
				break;
			default:
				break;
		}
	}

	private void handleAckMessage(AckMessage request)
	{
		DataMessage message = sessionCollection.addAck(request);
		if (message != null)
		{
			tftpServer.sendResponse(message);
		}
	}

	private void handleReadMessage(ReadRequest request)
	{
		Session session = new Session(request);
		sessionCollection.add(session);
		DataMessage message = session.createDataMessage();
		sessionCollection.checkIfFinished(session);
		tftpServer.sendResponse(message);
	}

	private void handleWriteMessage(WriteRequest request)
	{
		sessionCollection.add(new Session(request));
		WriteAcknowlege ackMessage = createWriteAck(request, (short) 0);
		tftpServer.sendResponse(ackMessage);
	}

	private void handleDataMessage(DataMessage request)
	{
		sessionCollection.addData(request);
		WriteAcknowlege ackMessage = createWriteAck(request, request.getBlockNumber());
		tftpServer.sendResponse(ackMessage);
	}
}
