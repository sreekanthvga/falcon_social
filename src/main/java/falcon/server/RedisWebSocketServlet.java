package falcon.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import redis.clients.jedis.JedisPool;

public class RedisWebSocketServlet extends WebSocketServlet {
	//private static final long serialVersionUID = -9152808383213487609L;
	private JedisPool pool;
	
	public RedisWebSocketServlet(JedisPool pool) {
		this.pool = pool;
	}
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(new WebSocketFactory(pool));
	}
	
};