package org.cometd.hello;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Settings {
	
	private static Properties properties = new Properties();
	private static boolean propertiesOK = false;
	
	private static final String BROADCAST_CLASS = "broadcastBeckendClass";

	private static final String JGROUPS_PROTO = "jGroupsProtocolFile";
	private static final String JGROUPS_CHANNEL = "jGroupsChannelName";
	
	private static final String REDIS_HOST = "redisHost";
	private static final String REDIS_PORT = "redisPort";
	private static final String REDIS_CHANNEL = "redisChannelNane";
	
	private static final String MONGO_HOST = "mongoHost";
	private static final String MONGO_PORT = "mongoPort";
	private static final String MONGO_DB = "mongoDB";
	private static final String MONGO_COLLECTION = "mongoCollection";
	private static final String MONGO_READONLY = "mongoReadOnly";
	
	private static final String JETTY_ADDR = "jettyAddr";
	private static final String JETTY_PORT = "jettyPort";
	
	private static final String COMETD_INIT = "cometdInitParam.";

	private static final MainLogger _logger = new MainLogger(Settings.class.getName());
	
	static {
		String[] paths = new String[] { Main.getParamConfig(), Main.getParamEnv() + ".properties", "config.properties" };
		
		for (String string : paths) {
			propertiesOK = tryLoadProperties(string);
			if (propertiesOK) {
				
				if ( getProperty(BROADCAST_CLASS).isEmpty() ) {
					_logger.error("No broadcast class set.");
					break;
				}
			
				_logger.info("Properties file loaded: " + string);
				_logger.info("BROADCAST_CLASS: " + getProperty(BROADCAST_CLASS));
		
				break;
			}
		}
		if (propertiesOK == false) {
			_logger.error("No properties file found.");
		}
	}
	
	private static boolean tryLoadProperties(String path) {
		try {
			//load a properties file
			properties.load(new FileInputStream(path));			
			return true;
		} catch (FileNotFoundException e) {
			_logger.warn("The " + path + " file was not found!");
		} catch (IOException e) {
			_logger.warn("The " + path + " file was not accessible! Check if it have read permissions.");
		}
		
		return false;
	}
	
	private static String getProperty(String property) {
		return properties.getProperty(property);
	}
	
	private static String getProperty(String property, String defaultVal) {
		return properties.getProperty(property, defaultVal);
	}

	public static synchronized String getJgroupsProto() {
		return getProperty(JGROUPS_PROTO);
	}

	public static synchronized String getJgroupsChannel() {
		return getProperty(JGROUPS_CHANNEL);
	}

	public static synchronized String getRedisHost() {
		return getProperty(REDIS_HOST);
	}

	public static synchronized int getRedisPort() {
		return Integer.parseInt(getProperty(REDIS_PORT, "6379"));
	}

	public static synchronized String getRedisChannel() {
		return getProperty(REDIS_CHANNEL);
	}

	public static synchronized String getMongoHost() {
		return getProperty(MONGO_HOST);
	}

	public static synchronized int getMongoPort() {
		return Integer.parseInt(getProperty(MONGO_PORT, "27017"));
	}

	public static synchronized String getMongoDb() {
		return getProperty(MONGO_DB);
	}

	public static synchronized String getMongoCollection() {
		return getProperty(MONGO_COLLECTION);
	}
	
	public static synchronized boolean getMongoReadOnly() {
		return Boolean.getBoolean(getProperty(MONGO_READONLY, "false"));
	}
	
	public static synchronized String getBroadcastClass() {
		return getProperty(BROADCAST_CLASS, "org.cometd.hello.modules.RedisBroadcaster");
	}
	
	public static synchronized String getJettyAddr() {
		return getProperty(JETTY_ADDR, "localhost");
	}
	
	public static synchronized int getJettyPort() {
		return Integer.parseInt(getProperty(JETTY_PORT, "8080"));
	}
	
	public static synchronized String getBroadcastClassRealName() {
		String[] className = getBroadcastClass().split("\\.");
		return className[className.length-1];
	}
	
	public static synchronized Map<String,String> getCometdInitParams() {
		Map<String, String> map = new HashMap<String, String>();

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String theKey = (String) keys.nextElement();
			if (theKey.startsWith(COMETD_INIT)) {
				map.put(theKey.replaceFirst(COMETD_INIT, ""), getProperty(theKey));
				_logger.debug("added to map {" + theKey.replaceFirst(COMETD_INIT, "") + ","+ getProperty(theKey) + "}");
			}
		}
				
		return map;
	}
	

}