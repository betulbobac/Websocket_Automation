package automation;

import static org.junit.Assert.assertEquals;

import org.testng.annotations.BeforeSuite;

import com.intuit.karate.Logger;
import com.intuit.karate.http.WebSocketClient;
import com.intuit.karate.http.WebSocketOptions;

public class BaseTest {
	protected static final Logger logger = new Logger();

	protected WebSocketClient client;
	protected String result;
	WebSocketOptions options;

	@BeforeSuite
	public void testWebSocketClient() throws Exception {
		options = new WebSocketOptions("wss://api.staging.neontrading.com");
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});
		client = new WebSocketClient(options, logger);
		client.send(
				"connect 21 {     \"device\": \"<deviceId>\",     \"clientId\": \"cta\",     \"clientVersion\": \"1.0.1\",     \"platformId\": \"windows\",     \"platformVersion\": \"10.2\",     \"locale\": \"de\" }");
		synchronized (this) {
			wait();
		}
		assertEquals("connected", result);
	}

}
