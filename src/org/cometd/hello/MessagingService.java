package org.cometd.hello;

import java.util.List;

import javax.inject.Inject;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.LocalSession;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.hello.BroadCaster;

@Service()
public class MessagingService implements BroadCaster.Listener {

    @Inject
    private BayeuxServer bayeuxServer;
    @Session
    private LocalSession sender;
    
    private static final MainLogger _logger = new MainLogger(MessagingService.class.getName());
    
    private BroadCaster bc;
    private MessageListener ml;
  
    public MessagingService() {
    	_logger.startUpLogger();
    	_logger.info("Initializing HelloChatEngine - FrontEnd");
    	try {
			bc = (BroadCaster) Class.forName(Settings.getBroadcastClass()).newInstance();
			this.ml = new MessageListener(bc);
		} catch (InstantiationException e) {
			_logger.error("error initializing HelloChatEngine", e);
		} catch (IllegalAccessException e) {
			_logger.error("error initializing HelloChatEngine", e);
		} catch (ClassNotFoundException e) {
			_logger.error("error initializing HelloChatEngine", e);
		}
	}
    
    @Configure("/*")
    protected void configureMessaging(ConfigurableServerChannel channel) {
    	this.ml = new MessageListener(bc);
    	channel.addListener(ml);
    }
    
    public void onUpdates(List<BroadCaster.Update> updates) {
        for (BroadCaster.Update update : updates) {        
        	broadcastMessage(update);
        }
    }
    
    private void broadcastMessage(BroadCaster.Update update) {
    	
    	String channelName = update.getChannel();
    	
    	//Publish to all subscribe  
    	ServerChannel channel = bayeuxServer.getChannel(channelName);
    	
    	ServerMessage message = MessageTools.formatMessageFromBE(update.getMessage());
		_logger.info("Sending message to listeners: " + message);
		try {
			channel.publish(sender, message);
		} catch (NullPointerException n) {
			_logger.warn("No clients available to deliver message");
		} catch (Exception e) {
			_logger.error("Error while try deliver the message: " + e.getMessage(), e);
		}
    }
    
    public synchronized BroadCaster getBC() {
		return bc;
	}

}