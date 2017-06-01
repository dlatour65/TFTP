package com.latour.tftp;

import java.nio.ByteBuffer;

/**
 * Serializer is the interface to serialize messages
 * 
 * @see DataSerializer
 * @see WriteAckSerializer
 * 
 * @author David Latour
 *
 */

public interface Serializer
{
	ByteBuffer serialize(Response response);
}
