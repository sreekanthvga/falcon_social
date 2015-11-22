# falcon_social
learning experiment with dropwizard based webservices and redis,

Webservice based out from dropwizard framework.
The endpoint for accepting json messages is : http://localhost:8080/message 
You may perform a POST operation using any rest browser plugins or curl.
The message may look like below/any valid json content.

{
  "message": {
    "body": "Chrome testing the dropwizard endpoint",
    "status": "89898989",
    "id": "wwwwwwwww",
    "header": "jsjfsjkdfjsd"
  }
}

And the server responds back with a UNIQUE_ID and date stamp along with the original request.
The server publish the message to the redis publish channel "BROADCAST".
The web socket servet also, listens to the redis "BROADCAST" channel and pushes the data across
all the WebSocket Session objects stored in a set.

The endpoint for getting all the persisted messages from the redis store is :  http://localhost:8080/messages
You may perform a GET operation using any browser clients.

The index.html file contains the simple browser based client code for listening to the
websocket endpoint 'ws://localhost:8080/listenwebsocket/' which prints out the  messages pushed to the
websocket endpoint to a table along with the date field extracted from the message itself.
Currently index.html is not served from the web server. One may just open this in any web socket 
supported browser and try the connect button to start listen to the above mentioned endpoint.

To test it, clone the repo to the local drive and execute the command 'mvn compile exec:java'
and it should start the web server running.


