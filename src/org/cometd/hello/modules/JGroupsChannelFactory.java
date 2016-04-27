package org.cometd.hello.modules;

import org.cometd.hello.MainLogger;
import org.cometd.hello.Settings;
import org.jgroups.JChannel;

public class JGroupsChannelFactory {
	
	private static final MainLogger _logger = new MainLogger(JGroupsBroadcaster.class.getName());
	
	private static JChannel channel;

    public static String DEFAULT_JGROUPS_XML = Settings.getJgroupsProto();
    public static String DEFAULT_CHANNEL_NAME = Settings.getJgroupsChannel();

    JGroupsChannelFactory() {
    }

    public static synchronized JChannel getInstance() {
        return getInstance(DEFAULT_JGROUPS_XML, DEFAULT_CHANNEL_NAME);
    }

    public static synchronized JChannel getInstance(String jGroupsFilterLocation) {
        return getInstance(jGroupsFilterLocation, DEFAULT_CHANNEL_NAME);
    }

    public static synchronized JChannel getInstance(String jGroupsFilterLocation, String channelName) {
        if (channel == null) {
            try {
                channel = new JChannel(jGroupsFilterLocation);
            } catch (Exception e) {
            	_logger.error("error creating JGroupsChannel", e);
                throw new RuntimeException("Failed to create JGroupsChannel", e);
            }
        }

        return channel;
    }
    
    public static synchronized String getCluster() {
        return DEFAULT_CHANNEL_NAME;
    }    

}
