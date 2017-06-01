package com.latour.tftp;

import java.net.SocketAddress;
import java.util.HashMap;

/**
 * SessionCollection holds all the current sessions.
 * 
 * @see Session
 * 
 * @author David
 *
 */

public class SessionCollection
{
	private final HashMap<SocketAddress, Session> sessions;

	public SessionCollection()
	{
		sessions = new HashMap<SocketAddress, Session>();
	}

	public void add(Session session)
	{
		sessions.put(session.getOriginalRequest().getSenderAddress(), session);
	}

	public void addData(DataMessage request)
	{
		Session session = sessions.get(request.getSenderAddress());
		if (session != null)
		{
			session.addData(request);
			if (session.isFinished())
			{
				sessions.remove(request.getSenderAddress());
			}
		}
	}

	public void checkIfFinished(Session session)
	{
		if (session.isFinished())
		{
			sessions.remove(session.getSenderAddress());
		}
	}

	public DataMessage addAck(AckMessage request)
	{
		DataMessage message = null;
		Session session = sessions.get(request.getSenderAddress());
		if (session != null)
		{
			message = session.addAck(request);
			if (session.isFinished())
			{
				sessions.remove(request.getSenderAddress());
			}
		}
		return message;
	}
}