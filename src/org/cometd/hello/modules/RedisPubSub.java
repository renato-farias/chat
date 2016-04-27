package org.cometd.hello.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.cometd.hello.MainLogger;
import org.cometd.hello.BroadCaster.Listener;
import org.cometd.hello.BroadCaster.Update;

import redis.clients.jedis.JedisPubSub;

public class RedisPubSub extends JedisPubSub  {
	
	static final MainLogger _logger = new MainLogger(RedisPubSub.class.getName());
	private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
 
	public synchronized void setListeners(List<Listener> listeners) {
		this.listeners = listeners;
	}

	@Override
    public void onMessage(String channel, String message) {
		_logger.info("Received message from broadcast: " + message);
		List<Update> updates = new ArrayList<Update>();
    	updates.add(new Update(message));
    	for (Listener listener : this.listeners){
            listener.onUpdates(updates);
        }
    }
 
    @Override
    public void onPMessage(String pattern, String channel, String message) {
 
    }
 
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
 
    }
 
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
 
    }
 
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
 
    }
 
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
 
    }
}