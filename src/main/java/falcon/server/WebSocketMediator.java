package falcon.server;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import falcon.server.RedisServerSubscriber;
import redis.clients.jedis.JedisPool;

@WebSocket
@ServerEndpoint("/listenwebsocket")
public class WebSocketMediator implements AbstractMessageHandler {

    private RedisServerSubscriber subscriber;
    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
 
    public WebSocketMediator(JedisPool pool)  {
		this.subscriber = new RedisServerSubscriber(pool, this);
		try {
			subscriber.start();
		} catch (Exception ex) {
			System.out.println("Failed to start subscriber thread!");
			throw new RuntimeException("Failed to start subscriber");
		}
	};

	@OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
		System.out.println("### Websocket Server onConnect hit \n");
		System.out.println("Connect WebSocket " + session.getRemoteAddress());
		peers.add(session);
	}
		
    public void onMessage(String channel, String message) throws IOException 
    {
    	for (Session session : peers) {
    		if(session.isOpen()) {
    			System.out.println("Message: " + message + " on " + session.getRemoteAddress());
    			session.getRemote().sendStringByFuture(message);
    		}
    		else 
    			System.out.println("Session is closed: " + session.getRemoteAddress());
    	}
    }
    
	@OnWebSocketMessage
    public void onText(Session session, String message) throws IOException {
		System.out.println("Got Message on WebSocket: " + message + "(" + session.getRemoteAddress() + ")" + " Endpoint: " + this);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(message);
			((ObjectNode) rootNode).put("timestamp", rootNode.get("TIMESTAMP").toString());
			onMessage("BROADCAST", rootNode.toString());  
		} catch (JsonProcessingException jpe) {
			System.out.println("Failed to JSON parse message: " + message);
		}
	}

	@OnWebSocketClose
    public void onClosed(Session session, int statusCode, String reason) {
	    boolean res = peers.remove(session);
		reason = (reason != null ? reason : "-");
		if (res)
			System.out.println("Closing WebSocket (" + session.getRemoteAddress() +" " + statusCode  + " " + reason );
		else {
			System.out.println("onClose: Unable to find session in map: " + session.getRemoteAddress() + " Status code" + statusCode + " Reason: " + reason);
		}
	}
}