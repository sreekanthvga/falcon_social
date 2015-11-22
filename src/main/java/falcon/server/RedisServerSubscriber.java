package falcon.server;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisServerSubscriber extends RedisAbstractSubscriber implements Runnable{
	private Logger logger;
    private AbstractMessageHandler  mHandler;
    private Jedis sub; 
	private boolean running = false;
	private Thread thread = new Thread(this, "RedisServerSubscriberThread");
	
	public RedisServerSubscriber(@Context JedisPool pool, AbstractMessageHandler handler) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.mHandler = handler;
		sub = pool.getResource();
	}
	
	@Override
	public void run() {
		running = true;  
		while (running) {
			try {
				sub.subscribe(this, "BROADCAST");
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception in subscribe: " + ex.getMessage());
			}
		}
		System.out.println("Ending " + thread.getName());
	}

	public void start() throws Exception {
		thread.start();
	}

	public void stop() throws Exception {
		running = false;
		try {
			sub.quit();
		} catch (Exception ex) {
			logger.warn("Caught Exception while shutting down. Ignoring: " + ex.getMessage());
		}
	}
	
	@Override
	public void onMessage(String channel, String message) 
	{
		try {
			mHandler.onMessage(channel, message);
		} catch (Exception ex) {
			logger.error("Failed to handle message " + message + " on channel " + channel);
		}
	}

};
	
