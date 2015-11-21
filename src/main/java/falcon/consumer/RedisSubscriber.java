package falcon.consumer;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisSubscriber extends JedisPubSub {
	private Logger logger;
	private RedisPersistHandler pHandler;
	
	public RedisSubscriber(JedisPool pool) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.pHandler = new RedisPersistHandler(pool);
	}
	
	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		logger.debug("onUnsubscribe");
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		logger.debug("onSubscribe");
	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		logger.debug("onPUnsubscribe");
	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		logger.debug("onPSubscribe");
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		logger.debug("onPMessage");
	}

	@Override
    public void onMessage(String channel, String message) {
		try {
			pHandler.onMessage(channel, message);
		} catch (Exception ex) {
			logger.error("Failed to Persist message " + message + " on channel " + channel);
		}
	}

	public void logger(String channel, String message) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.readTree(message);
			logger.debug("Channel " + channel + ": " + message);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to parse JSON: " + message, ex);
		}
	};
	
};