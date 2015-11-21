package falcon.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisPersistHandler implements AbstractMessageHandler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private JedisPool pool;
	
	public RedisPersistHandler(JedisPool pool) {
		this.pool = pool;
	}

	@Override
	public void onMessage(String channel, String message) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(message);
			try (Jedis jedis = pool.getResource()) {
				// Store message
				logger.debug("Redis store message " +  message);
				jedis.set(rootNode.path("UUID").asText(), message);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to persist message: " + message, ex);
		}
	};
	
};