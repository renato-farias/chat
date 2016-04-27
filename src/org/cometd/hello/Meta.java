package org.cometd.hello;

import org.cometd.annotation.Listener;
import org.cometd.annotation.Service;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;

@Service
public class Meta {
    
    private static final MainLogger _logger = new MainLogger(Meta.class.getName());
    
    @Listener("/meta/subscribe")
    public void monitorSubscribe(ServerSession session, ServerMessage message) {
    	_logger.info("Monitored Subscribe from "+session+" for "+message.get(Message.SUBSCRIPTION_FIELD));
    }
    
    @Listener("/meta/unsubscribe")
    public void monitorUnsubscribe(ServerSession session, ServerMessage message) {
    	_logger.info("Monitored Unsubscribe from "+session+" for "+message.get(Message.SUBSCRIPTION_FIELD));
    }
    
    @Listener("/meta/handshake")
    public void monitorHandShake(ServerSession session, ServerMessage message) {
    	_logger.debug(message.getJSON());
    }

    @Listener("/meta/*")
    public void monitorMeta(ServerSession session, ServerMessage message) {
    	_logger.debug(message.toString());
    }

}
