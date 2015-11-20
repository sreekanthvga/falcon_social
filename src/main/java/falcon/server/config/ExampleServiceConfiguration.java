package falcon.server.config;

import javax.validation.Valid;
import io.dropwizard.Configuration;
import com.bendb.dropwizard.redis.JedisFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Context;

public class ExampleServiceConfiguration extends Configuration {

    @Valid
    private MessagesConfiguration messages;
    
    @NotNull
    @Context
   /*private JedisFactory jedisFactory = new JedisFactory();

    @JsonProperty("redis")
    public JedisFactory getJedisFactory() {
        return jedisFactory;
    }

    @JsonProperty("redis")
    public void setRedisFactory(JedisFactory factory) {
        jedisFactory = factory;
    }*/
    

    public MessagesConfiguration getMessages() {
        return messages;
    }

    public void setMessages(MessagesConfiguration messages) {
        this.messages = messages;
    }
}