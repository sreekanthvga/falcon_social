package falcon.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import redis.clients.jedis.JedisPool;

public class RedisWebSocketServlet extends WebSocketServlet {
	private JedisPool pool;
	public RedisWebSocketServlet(JedisPool pool) {
		this.pool = pool;
	}
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(new WebSocketFactory(pool));
	}
	
};
