package org.cometd.hello;

import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.hello.db.MongoDB;

public class MessageListener implements ServerChannel.MessageListener {
	
	private BroadCaster bc;
	private MongoDB mongo;
	
	static final MainLogger _logger = new MainLogger(MessageListener.class.getName());
	
	public MessageListener(BroadCaster bc) {
		this.bc = bc;
		this.mongo = MongoDB.getInstance();
		
	}
	
	private void brodacast(ServerSession session, ServerMessage message) {
		String formatedMsg = null;
		try {
			formatedMsg = MessageTools.formatMessageFromFE(message, session);
		} catch (Exception e) {
			_logger.error("error sending message", e);
		}
		
		if (formatedMsg != null) {
			_logger.info("Sending message to broadcast: " + formatedMsg + " from: " + session.getId());
			bc.sendMessage(formatedMsg);
			
			if ( Settings.getMongoReadOnly() == false ) {
				writeOnDataBase(formatedMsg);
			}
		}
	}
	
	private void writeOnDataBase(String message) {
		_logger.info("Writting message into the database: " + message);
		try {
    		mongo.insertJSON(message, Settings.getMongoCollection());
    	} catch(Exception e) {
    		_logger.warn("Failed write message on Mongo: " + e.getMessage(), e);
    	}
		
    }

	public boolean onMessage(ServerSession session, ServerChannel channel, Mutable message) {
		if ( message.getId() == null ) {
			return true;
		}
		if ( message.getId().isEmpty() == false || message.get("id") != null ) {
			_logger.debug("Message sent from client: " + message.getJSON());
			brodacast(session, message);
			return false;
		}
		return true;
		
	}
}
