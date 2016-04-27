package org.cometd.hello.modules;

import org.cometd.hello.BroadCaster;
import org.cometd.hello.MainLogger;
import org.cometd.hello.MessageTools;
import org.cometd.hello.Settings;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class JGroupsBroadcaster extends ReceiverAdapter implements BroadCaster {
	
	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	final List<String> state = new LinkedList<String>();
	private static final MainLogger _logger = new MainLogger(JGroupsBroadcaster.class.getName());
	
	private JChannel jchannel;
	private String clusterName;
	
	
	public JGroupsBroadcaster() {
		
		JChannel jchannel = JGroupsChannelFactory.getInstance();
		String clusterName = JGroupsChannelFactory.getCluster();
		
		this.jchannel = jchannel;
		this.clusterName = clusterName;
		
	}

	public synchronized void setJchannel(JChannel jchannel) {
		this.jchannel = jchannel;
	}

	public void viewAccepted(View new_view) {
		_logger.info("Accepted clients: " + new_view );
    }
	
    public void receive(Message msg) {
    	
    	String message = MessageTools.getUTF8(msg.getBuffer());
    	
    	_logger.info("Received message from broadcast: " + msg);
    	_logger.info("Details of received message: " + message);

    	List<Update> updates = new ArrayList<Update>();
    	
        updates.add(new Update(message));
        
        for (Listener listener : listeners){
            listener.onUpdates(updates);
        }
    }
    
    public void stop() {
    	jchannel.close();
    }
    
    
    public void sendMessage(String message) {
    	Message msg = null;
		msg = new Message(null, null, message.getBytes());
    	try {
    		jchannel.send(msg);
    	} catch(Exception e) {
    		_logger.warn("Failed to send message to cluster: " + this.clusterName + " : " + e.getMessage(), e);
    	}
    }
    

    public List<Listener> getListeners() {
        return listeners;
    }
    

    public void start() {
    	
    	_logger.info("Loading JGroupsBroadcaster");	
		_logger.info("JGROUPS_PROTO: " + Settings.getJgroupsProto());
		_logger.info("JGROUPS_CHANNEL: " + Settings.getJgroupsChannel());
		
		_logger.info("Starting JGroups Clustering support with group name " + this.clusterName);
		try {
			this.jchannel.setReceiver(this);
			this.jchannel.connect(clusterName);
		} catch (Exception e) {
			_logger.warn("Failed to connect to cluster: " + this.clusterName + " : " + e.getMessage(), e);
		}
		
		
	}
    
	public boolean isOkay() {
		if ( jchannel.isConnected() ) {
			return true;
		}
		return false;	
	}

}
