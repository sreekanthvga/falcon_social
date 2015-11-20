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


import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Path(value = "/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FalconMessage {

	private UUID uuid;
	private String header;
	private String payload;
	private String timestamp;

	JedisPool pool; 
	// Must have no-argument constructor
	public FalconMessage(JedisPool jedisPool) {
    	pool = jedisPool; 
    }
	
	public FalconMessage(String header, String body) {
	    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    Date date = new Date();
		this.header = header;
		this.payload = body;
		this.uuid = java.util.UUID.randomUUID();
		this.timestamp = dateFormat.format(date);
	}
	
	@POST
    public JsonNode create(JsonNode json) {
    	return createUpdate(json, UUID.randomUUID().toString());
    }
	
	private JsonNode createUpdate(JsonNode json, String id) {
    	((ObjectNode)json).put("UUID", UUID.randomUUID().toString());
    	((ObjectNode)json).put("timestamp", System.currentTimeMillis());
    	
    	//final Jedis publisherJedis = jedisPool.getResource();

        //new Publisher(publisherJedis, CHANNEL_NAME).start();
    	System.out.println("Request -> " + json.toString());
    	try (Jedis jedis = pool.getResource()) {
    		jedis.publish("BROADCAST", json.toString());
    		System.out.println("Active: " + pool.getNumActive());
    		return json;
    	}
	}

}