package at.ac.ait.ubicity.http;

import java.io.IOException;

import org.junit.Test;

import at.ac.ait.ubicity.http.impl.HttpControllerImpl;

public class HttpHandlerTest {

	@Test
	public void testEndpoint() throws IOException {

		HttpControllerImpl impl = new HttpControllerImpl();
		impl.init();

		System.in.read();

		impl.shutdown();
	}
}
