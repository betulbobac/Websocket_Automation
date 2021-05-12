<h1>What is Websocket</h1>
A WebSocket is one of communications protocol,that allow two or more devices to exchange information.
The WebSocket API differs from the standard SOAP or REST API by virtue of the nature of its traffic.
If I was testing a REST API, I would send a request, “wait” for a response and make sure it had the response code, the data, format and response times I was expecting.
With a WebSocket API, we are looking at another kettle of fish altogether because of the persistent nature of the connections. We need a method to maintain the open connection and we need a way to see the messages that arrive in response to the messages that you are sending.

![image](https://user-images.githubusercontent.com/76169905/117990557-f0437480-b345-11eb-94e3-59ae4a12a3c9.png)

<h1>How to Test Websocket</h1>
I prefered to use karate tool enables you to script a sequence of calls to any kind of web-service and assert that the responses are as expected. It makes it really easy to build complex request payloads, traverse data within the responses, and chain data from responses into the next request. 
Since Karate is built on top of Cucumber-JVM, you can run tests and generate reports like any standard Java project
I was given websocket server with public access. So, I don't have to handle server side operation. Only client actions are what I need to send request and validate response.

<h1>Part 1 - Test with Karate WebSocket Client</h1>

First of all, I created BaseTest class that created websocket for the desired options and created WebSocketClient.
In my case, connect request must be sent before each test cases.The `connect` command is always the first request in a websocket session.
This class basically creates client and use within entire suite after it connected successfully


![image](https://user-images.githubusercontent.com/76169905/117992803-ddca3a80-b347-11eb-91f3-e7e9a205e204.png)


Instrument Subscription retrieve details about a single instrument.
Test cases performs response validation after sub command was sent to websocket.
I have to use synchronized block to perform validation after response retrieved from server.
This is how I handle wait until response received then check the result
![image](https://user-images.githubusercontent.com/76169905/117995710-37336900-b34a-11eb-9bcb-c248c7a21799.png)

In order to complete test, failure scenario should be tested as well such as bad subscription type, unkown topic, json parse error etc.
You may find relavant cases inside InstrumentSubscriptionTest.java

<h1>Part 2 - Test with Cucumber Client</h1>

I created echo.feature file to keep scenario related with subscription
If you are familiar with Cucumber / Gherkin, the big difference here is that you don't need to write extra "glue" code or Java "step definitions" with karate!
Karate also has built-in support for websocket that is based on the async capability
These will init a websocket client for the given url and optional subProtocol. If a handler function (returning a boolean) is provided - it will be used to complete the "wait" of socket.listen() if true is returned - where socket is the reference to the websocket client returned by karate.webSocket(). A handler function is needed only if you have to ignore other incoming traffic. If you need custom headers for the websocket handshake, use JSON as the last argument.
![image](https://user-images.githubusercontent.com/76169905/117998614-bcb81880-b34c-11eb-8b34-a2e637b5528e.png)
This is great for testing boundary conditions against a single end-point, with the added bonus that your test becomes even more readable. This approach can certainly enable product-owners or domain-experts who are not programmer-folk, to review, and even collaborate on test-scenarios and scripts.

Note that any websocket instances created will be auto-closed at the end of the Scenario.

To make the assertions, I used the keyword “match.” By doing so, you can check if an expression is evaluated as true by using the built-in JavaScript engine.

In order to run test cases with Cucumber , execute WebSocketRunner.java by junit
Following report is generated regarding my test result
![image](https://user-images.githubusercontent.com/76169905/117999470-8e870880-b34d-11eb-86db-7525fc7c0032.png)



