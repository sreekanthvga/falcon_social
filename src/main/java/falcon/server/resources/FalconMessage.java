package falcon.server.resources;

import java.util.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;
import javax.ws.rs.Path;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisDataException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import be.tomcools.dropwizard.websocket.WebsocketBundle;
//import falcon.server.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Path(value = "/")
public class FalconMessage {
	JedisPool pool;
	public FalconMessage(JedisPool jedisPool) {
    	pool = jedisPool; 
    }
	
	private String getDateTime() {
		System.out.println("Request -> HIT 2" );
		 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		 Date date = new Date();
		 return dateFormat.format(date);
	}
	
	@Path(value = "message")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public JsonNode create(JsonNode json) {
		System.out.println("Request -> HIT 1" + json.toString());
    	return createUpdate(json, UUID.randomUUID().toString());
    }
	
	private JsonNode createUpdate(JsonNode json, String id) {
    	((ObjectNode)json).put("UNIQUE_ID", UUID.randomUUID().toString());
    	((ObjectNode)json).put("TIMESTAMP", getDateTime());
    	System.out.println("Request -> " + json.toString());
    	try (Jedis jedis = pool.getResource()) {
    		jedis.publish("BROADCAST", json.toString());
    		System.out.println("Active: " + pool.getNumActive());
    	}
    	//push to websocket
    	//sendMessageOverSocket(json.toString());
    	return json;
	}
	
	@Path(value = "messages")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<JsonNode> FetchAll() {
		
		List<JsonNode> results = new LinkedList<JsonNode>();
		try (Jedis jedis = pool.getResource()) {
			ObjectMapper m = new ObjectMapper();
			ScanResult<String> scanResult=jedis.scan("0");
			String cursor=scanResult.getStringCursor();
			List<String> keys=scanResult.getResult();
			do {
			    for (String key : keys) {
			    	String value = null;
					try {
						value = jedis.get(key);
						System.out.println("Redis: " + key + "=" + value);
						results.add(m.readTree(value));
					} catch (JedisDataException e) {
						System.out.println("Failed to parse key " + key + " or " + value + " " + e.toString());
					} catch (JsonProcessingException e) {
						System.out.println("Failed to json Process key " + key + " or " + value + " " +  e.toString());
					} catch (IOException e) {
						System.out.println("IOException key " + key + " or " + value + " " +  e.toString());
					}
			    }
			    scanResult=jedis.scan(cursor);
			    cursor=scanResult.getStringCursor();
			    keys=scanResult.getResult();
			  } while (cursor != null && !cursor.equals("0") /*&& keys != null && keys.size() > 0 */);
			
		}
		
		return results;
	}

}