package at.ac.ait.ubicity.http.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.Shutdown;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import at.ac.ait.ubicity.commons.util.PropertyLoader;
import at.ac.ait.ubicity.http.HttpController;

import com.sun.net.httpserver.HttpServer;

@PluginImplementation
public class HttpControllerImpl implements HttpController {
	private static final Logger logger = Logger.getLogger(HttpControllerImpl.class);

	private static PropertyLoader config = new PropertyLoader(HttpControllerImpl.class.getResource("/http.cfg"));
	private HttpServer server;
	private String name;

	@Override
	@Init
	public void init() {
		name = config.getString("plugin.http.name");

		try {
			URI baseUri = UriBuilder.fromUri("http://localhost/").port(config.getInt("env.http.endpoint_port")).build();
			ResourceConfig config = new ResourceConfig(getResourceClasses());
			config.property("com.sun.jersey.api.json.POJOMappingFeature", true);

			server = JdkHttpServerFactory.createHttpServer(baseUri, config);

		} catch (Exception | Error e) {
			logger.error("Could not create http server.", e);
		}

		logger.info(name + " loaded");
	}

	private Set<Class<?>> getResourceClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();

		String[] handlers = config.getStringArray("plugin.http.handler");
		String path = HttpControllerImpl.class.getPackage().getName();

		// add all handler to collection
		for (int i = 0; i < handlers.length; i++) {
			String className = path + "." + handlers[i];
			try {
				classes.add(Class.forName(className));
				logger.info("Handler loaded: " + className);
			} catch (ClassNotFoundException e) {
				logger.warn("Handler not found: " + className);
			}
		}
		return classes;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	@Shutdown
	public void shutdown() {
		if (server != null) {
			server.stop(0);
		}
	}
}
