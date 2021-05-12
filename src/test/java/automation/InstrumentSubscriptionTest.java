package automation;

import static org.junit.Assert.assertTrue;

import org.testng.annotations.Test;

import com.intuit.karate.http.WebSocketClient;
import com.intuit.karate.http.WebSocketOptions;

public class InstrumentSubscriptionTest extends BaseTest {

	@Test
	public void testInstrument01Subscription1() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		client.send("sub 1 { \"type\": \"instrument\", \"id\": \"DE000BASF111\" }");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("\"active\":true"));
		assertTrue(result.contains("\"typeId\":\"stock\""));
		assertTrue(result.contains("\"isin\":\"DE000BASF111\""));

	}

	@Test
	public void testInstrument02Subscription13() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		client.send("sub 13 { \"type\": \"ticker\", \"id\": \"DE000BASF111.LSX\" }");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("13 A"));
		assertTrue(result.contains("\"price\":67.91"));
		assertTrue(result.contains("\"size\":2"));

		client.send("unsub 13");

	}

	@Test
	public void testInstrument03ValidSubscriptions() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		for (int i = 12; i <= 60; i++) {
			client.send("sub " + i + " { \"type\": \"ticker\", \"id\": \"DE000BASF111.LSX\" }");

			synchronized (this) {
				wait();
			}
			assertTrue(result.contains("" + i + " A"));
			assertTrue(result.contains("\"price\":67.91"));
			assertTrue(result.contains("\"size\":2"));
			client.send("unsub " + i + "");

		}
	}

	@Test
	public void testInstrument04Subscription13EError() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		client.send("sub 13 { \"type\": \"ticker\", \"id\": \"DE000BASF1.LSX\" }");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("13 E"));
		assertTrue(result.contains("\"errorCode\":\"JSON_PARSE_ERROR\""));
		assertTrue(result.contains("\"errorMessage\":\"Subscription payload"));

	}

	@Test
	public void testInstrument05SubscriptionBadSubscriptionType() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		client.send("sub 13 { \"type\": \"tickerA\", \"id\": \"DE000BASF111.LSX\" }");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("13 E"));
		assertTrue(result.contains("\"errorCode\":\"BAD_SUBSCRIPTION_TYPE\""));

	}

	@Test
	public void testInstrument06SubscriptionUnkownTopicType() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		client.send("sub 13 { \"type\": \"tickerA\", \"id\": \"\" }");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("13 E"));
		assertTrue(result.contains("\"errorCode\":\"BAD_SUBSCRIPTION_TYPE\""));
		assertTrue(result.contains("\"errorMessage\":\"Unknown topic type\""));

	}

	@Test
	public void testInstrument07SubscriptionDecodeError() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		client.send("sub 13 { \"type\": \"tickerA\" ");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("13 E"));
		assertTrue(result.contains("\"errorCode\":\"JSON_PARSE_ERROR\""));
		assertTrue(result.contains("\"errorMessage\":\"Could not decode payload\""));

	}

	@Test
	public void testInstrument08SubscriptionDummyError() throws Exception {
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});

		client.send("sub 13 { \"type\": \"instrument\", \"id\": \"DE000BASF111\",\"dummyField\": \"DE000BASF111\" }");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("13 E"));
		assertTrue(result.contains("\"errorCode\":\"JSON_PARSE_ERROR\""));
		assertTrue(result.contains("\"errorMessage\":\"Could not decode payload\""));

	}

	@Test
	public void testInstrument99SubscriptionFail() throws Exception {
		WebSocketOptions options = new WebSocketOptions("wss://api.staging.neontrading.com");
		options.setTextConsumer(text -> {
			logger.debug("websocket listener text: {}", text);
			synchronized (this) {
				result = text;
				notify();
			}
		});
		client = new WebSocketClient(options, logger);
		client.send("sub 1 { \"type\": \"instrument\", \"id\": \"DE000BASF111\" }");
		synchronized (this) {
			wait();
		}
		assertTrue(result.contains("\"errorCode\":\"VERSION_NOT_SPECIFIED\""));
		assertTrue(result.contains("\"errorMessage\":\"protocol version not specified\""));

	}
}
