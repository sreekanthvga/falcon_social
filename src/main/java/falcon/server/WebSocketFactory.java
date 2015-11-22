package falcon.server;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import redis.clients.jedis.JedisPool;

public class WebSocketFactory implements WebSocketCreator {
	WebSocketMediator endpoint;
	
	public WebSocketFactory(JedisPool pool) {
		endpoint = new WebSocketMediator(pool);
	}
	
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        for (String subprotocol : req.getSubProtocols()) {
            if ("binary".equals(subprotocol)) {
                return null;
            }
        }
        return endpoint;
    }

	public Object createWebSocket(UpgradeRequest arg0, UpgradeResponse arg1) {
		// TODO Auto-generated method stub
		return endpoint;
	}
}