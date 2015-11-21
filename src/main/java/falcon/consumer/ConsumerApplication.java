package falcon.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ConsumerApplication {

    public static final String CHANNEL_NAME = "BROADCAST";
    private static Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args) throws Exception {

    	final JedisPoolConfig poolConfig = new JedisPoolConfig();
        final JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 0);
        final Jedis subscriberJedis = jedisPool.getResource();
        final RedisSubscriber subscriber = new RedisSubscriber(jedisPool);
        
        try {
            logger.info("Subscribing to \"Broadcast\"");
            subscriberJedis.subscribe(subscriber, CHANNEL_NAME);
            logger.info("Subscription Done.");
        } catch (Exception e) {
            logger.error("Subscribing failed.", e);
        }finally {
        	subscriber.unsubscribe();
            jedisPool.returnResource(subscriberJedis);
        }

    }
};