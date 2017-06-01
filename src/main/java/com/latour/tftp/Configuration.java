package com.latour.tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration is a class that holds the configuration information for the server.
 * It currently holds variables in a property file.  The property file needs to be in 
 * the resource directory from which the server is executing.
 * 	1. port - port the server will listen and send on.  Default is 69.
 * 	2. timeout - not implemented
 * 	3. writeDirectory - is the directory that file will be saved to or retrieved from.
 * 
 * @author David Latour
 *
 */
public class Configuration
{
	private static final String PORT_STRING = "port";
	private static final String TIMOUT_STRING = "timeout";
	private static final String WRITE_DIRECTORY_STRING = "writeDirectory";
	private static final String CONFIG_FILE_NAME = "tftpConfig.properties";

	private static class LazyHolder
	{
		public static Properties configProperties = readConfiguration();
		public static final Configuration INSTANCE = new Configuration(configProperties);
	}

	public static Configuration getInstance()
	{
		return LazyHolder.INSTANCE;
	}

	private final int listeningPort;
	private final int timeOut;
	private final File writeDirectory;

	private Configuration(Properties configProperties)
	{
		listeningPort = readPort(configProperties);
		timeOut = readTimeOut(configProperties);
		writeDirectory = readWriteDirectory(configProperties);
	}

	private static Properties readConfiguration()
	{
		Properties configProperties = null;
		try
		{
			File configFile = new File(CONFIG_FILE_NAME);
			FileInputStream configStream = new FileInputStream(configFile);
			configProperties = new Properties();
			configProperties.load(configStream);
			configStream.close();
		}
		catch(IOException e)
		{
			configProperties = createDefaultProperties();
		}
		return configProperties;
	}

	private static Properties createDefaultProperties()
	{
		Properties properties = new Properties();
		properties.setProperty(PORT_STRING, "69");
		properties.setProperty(TIMOUT_STRING, "100");
		properties.setProperty(WRITE_DIRECTORY_STRING, "DROP_DIR");
		return properties;
	}

	private int readPort(Properties configProperties)
	{
		String propertyString = configProperties.getProperty(PORT_STRING);
		return Integer.parseInt(propertyString);
	}

	private int readTimeOut(Properties configProperties)
	{
		String propertyString = configProperties.getProperty(TIMOUT_STRING);
		return Integer.parseInt(propertyString);
	}

	private File readWriteDirectory(Properties configProperties)
	{
		String propertyString = configProperties.getProperty(WRITE_DIRECTORY_STRING);
		File dir = new File(propertyString);
		if (dir.exists() == false)
		{
			dir.mkdir();
		}
		return dir;
	}

	public int getListeningPort()
	{
		return listeningPort;
	}

	public int getTimeOut()
	{
		return timeOut;
	}

	public File getWriteDirectory()
	{
		return writeDirectory;
	}
}
