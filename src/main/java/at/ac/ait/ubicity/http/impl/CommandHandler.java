package at.ac.ait.ubicity.http.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import at.ac.ait.ubicity.commons.jit.Action;
import at.ac.ait.ubicity.commons.jit.Answer;
import at.ac.ait.ubicity.core.UbicityCore;

/**
 * Global HTTP Handler for receiving and forwarding Commands.
 *
 */
@Path("command")
public class CommandHandler extends ResourceConfig {
	private static final Logger logger = Logger.getLogger(CommandHandler.class);

	@Path("{plugin}")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String process(@PathParam("plugin") String plugin, @QueryParam("cmd") String cmd, @QueryParam("data") String data) {

		Action act = new Action(plugin, cmd, data);
		logger.info("Received: " + act.toJson());
		Answer answer = UbicityCore.forward(act);

		return answer.toJson();
	}

	/*
	 * @POST
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Answer process(Action act) {
	 * 
	 * logger.info("Received: " + act.toString());
	 * 
	 * // Answer answer = UbicityCore.forward(act);
	 * 
	 * Answer answer = new Answer(act, Status.COMMAND_NOT_RECOGNIZED); logger.info("Received answer: " + answer.toString());
	 * 
	 * return answer; }
	 */
}
