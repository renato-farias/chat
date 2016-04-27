package org.cometd.hello.modules;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.cometd.hello.BroadCaster;
import org.cometd.hello.MainLogger;
import org.cometd.hello.Settings;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisBroadcaster implements BroadCaster  {
	
	static final MainLogger _logger = new MainLogger(RedisBroadcaster.class.getName());
	public static final String CHANNEL_NAME = Settings.getRedisChannel();
	
	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	
	JedisPoolConfig poolConfig = new JedisPoolConfig();
    JedisPool jedisPool = new JedisPool(poolConfig, Settings.getRedisHost(), Settings.getRedisPort());
    final Jedis subscriberJedis = jedisPool.getResource();
    final RedisPubSub subscriber = new RedisPubSub();
    Jedis publisherJedis;
    
    public RedisBroadcaster() {
    	subscriber.setListeners(listeners);
    }
    
    public void start() {
    	
    	_logger.info("Loading RedisBroadcaster");	
		_logger.info("REDIS_HOST: " + Settings.getRedisHost());
		_logger.info("REDIS_PORT: " + Settings.getRedisPort());
		_logger.info("REDIS_CHANNEL: " + Settings.getRedisChannel());
    	
		try {
        	_logger.info("Subscribing to [" + CHANNEL_NAME + "]. This thread will be blocked.");
        	(new Thread() { public void run() {
        		subscriberJedis.subscribe(subscriber, CHANNEL_NAME);
        		_logger.info("Subscription ended.");
        	}}).start();
        } catch (Exception e) {
        	_logger.error("Subscribing failed: " + e, e);
        }
    }
    
 
    public void stop() {
    	subscriber.unsubscribe();
    	jedisPool.returnResource(subscriberJedis);
    	jedisPool.returnResource(publisherJedis);
    }
    
    private void checkConnect() {
    	try {
    		_logger.debug("Pinging Redis");
    		publisherJedis.ping();
    		_logger.debug("PONG: Redis is Okay to publish");
    	} catch (NullPointerException n) {
    		_logger.info("Publisher was not connected, I am connect to Redis first.");
    		publisherJedis = jedisPool.getResource();
    	} catch (Exception e) {
    		_logger.info("Reconnecting to redis...");
    		publisherJedis = jedisPool.getResource();
		}
    }
    
    public void sendMessage(String message) {
    	try {
    		checkConnect();
    		publisherJedis.publish(CHANNEL_NAME, message);
    	} catch(Exception e) {
    		_logger.warn("Failed to publish message to pub/sub: " + e.getMessage(), e);
    	}
    }
    

    public List<Listener> getListeners() {
        return listeners;
    }

	public boolean isOkay() {
		if ( subscriberJedis.isConnected() ) {
			return true;
		}
		return false;
	}
	
}
