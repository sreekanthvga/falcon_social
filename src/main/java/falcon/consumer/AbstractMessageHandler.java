package falcon.consumer;

import java.io.IOException;

public interface AbstractMessageHandler {

	void onMessage(String channel, String message) throws IOException;
	
}