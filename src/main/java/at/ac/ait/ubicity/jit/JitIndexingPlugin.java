/**
    Copyright (C) 2014  AIT / Austrian Institute of Technology
    http://www.ait.ac.at

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/agpl-3.0.html
 */
package at.ac.ait.ubicity.jit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.Thread;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.Shutdown;

import org.apache.log4j.Logger;

import at.ac.ait.ubicity.commons.interfaces.UbicityPlugin;
import at.ac.ait.ubicity.commons.util.PropertyLoader;

@PluginImplementation
public final class JitIndexingPlugin implements UbicityPlugin {

	private static final Logger logger = Logger
			.getLogger(JitIndexingPlugin.class);

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
				JitIndexingPlugin.class.getResource("/jit.cfg"));
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
