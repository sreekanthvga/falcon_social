package falcon.server;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.ServletRegistration;
import javax.websocket.server.ServerEndpointConfig;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;

import be.tomcools.dropwizard.websocket.WebsocketBundle;
import falcon.server.config.ExampleServiceConfiguration;
import falcon.server.resources.FalconMessage;
import falcon.server.resources.HelloResource;

public class MessageHandler extends Application<ExampleServiceConfiguration> {
	private WebsocketBundle websocket = new WebsocketBundle();
    public static void main(String[] args) throws Exception {
        new MessageHandler().run(args);
    }

    @Override
    public void initialize(Bootstrap<ExampleServiceConfiguration> bootstrap) {
    	super.initialize(bootstrap);
        bootstrap.addBundle(websocket);
        //bootstrap.addBundle(new AssetsBundle("/app", "/", "index.html", "static"));
        //bootstrap.addBundle(new AssetsBundle("/static", "/"));
        //bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
        bootstrap.addBundle(new AssetsBundle("/assets", "/static"));
    }

    @Override
    public void run(ExampleServiceConfiguration conf, Environment env) throws Exception {
    	 final HelloResource resource = new HelloResource(conf.getMessages());
         env.jersey().register(resource);
        
         final JedisPoolConfig poolConfig = new JedisPoolConfig();
         final JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 0);
         env.jersey().register(new FalconMessage(jedisPool));
         //env.jersey().setUrlPattern("/application/*");
         websocket.addEndpoint(WebSocketMediator.class);
         final ServletRegistration.Dynamic websocket = env.servlets().addServlet(
                 "listenwebsocket",
                 new RedisWebSocketServlet(jedisPool));
         websocket.setAsyncSupported(true);
         websocket.addMapping("/listenwebsocket/");
    }

}