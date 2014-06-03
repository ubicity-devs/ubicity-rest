package at.ac.ait.ubicity.jit.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.Thread;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.Shutdown;

import org.apache.log4j.Logger;

import at.ac.ait.ubicity.commons.util.PropertyLoader;
import at.ac.ait.ubicity.jit.JitController;

@PluginImplementation
public class JitControllerImpl implements JitController {
	private static final Logger logger = Logger.getLogger(JitController.class);

	protected ServerSocket listenSocket;

	protected ThreadGroup threadGroup;

	protected Vector<Connection> connections;

	protected Vulture vulture;

	private String name;

	private boolean shutdown;

	@Override
	@Init
	public void init() {
		PropertyLoader config = new PropertyLoader(
				JitController.class.getResource("/jit.cfg"));
		name = config.getString("plugin.jit.name");

		try {
			listenSocket = new ServerSocket(
					config.getInt("plugin.jit.reverse_cac_port"));
		} catch (Exception | Error e) {
			logger.fatal("Could not create server-side socket on ubicity core:"
					+ e);
		}
		threadGroup = new ThreadGroup(
				"ubicity JitIndexingController connections");
		connections = new Vector<Connection>();
		vulture = new Vulture(this);

		logger.info(name + " loaded");
	}

	/**
	 * Loop forever, listening for and accepting connections from clients. For
	 * each connection, create a Connection object to handle communication
	 * through the new Socket. When we create a new connection, add it to the
	 * Vector of connections. The Vulture will dispose of dead connections.
	 */
	@Thread
	public void run() {
		while (!shutdown) {
			try {
				Socket client = listenSocket.accept();
				Connection c = new Connection(client, threadGroup, 3, vulture);
				synchronized (connections) {
					connections.addElement(c);
				}
			} catch (IOException e) {
				logger.fatal("Exception while listening for connections" + e);
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	@Shutdown
	public void shutdown() {
		shutdown = true;

		for (Connection con : connections) {
			try {
				con.client.close();
			} catch (IOException e) {
				logger.warn("Caught Exc while closing connection.", e);
			}
		}

	}
}
