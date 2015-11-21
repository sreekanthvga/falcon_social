package falcon.server;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;

import falcon.server.config.ExampleServiceConfiguration;
import falcon.server.resources.FalconMessage;
import falcon.server.resources.HelloResource;

public class MessageHandler extends Application<ExampleServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new MessageHandler().run(args);
    }

    @Override
    public void initialize(Bootstrap<ExampleServiceConfiguration> bootstrap) {
    }

    @Override
    public void run(ExampleServiceConfiguration conf, Environment env) throws Exception {
    	 final HelloResource resource = new HelloResource(conf.getMessages());
         env.jersey().register(resource);
        
         final JedisPoolConfig poolConfig = new JedisPoolConfig();
         final JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 0);
         env.jersey().register(new FalconMessage(jedisPool));
    
    }

}