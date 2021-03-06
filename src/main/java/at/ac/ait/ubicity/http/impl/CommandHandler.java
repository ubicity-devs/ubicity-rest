package at.ac.ait.ubicity.http.impl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import at.ac.ait.ubicity.commons.jit.Action;
import at.ac.ait.ubicity.commons.jit.Answer;

/**
 * Global HTTP Handler for receiving and forwarding Commands.
 *
 */
@Path("command")
public class CommandHandler extends AbstractHandler {

	@Path("{plugin}")
	@GET
	public String getEndpoint(@PathParam("plugin") String plugin, @QueryParam("cmd") String cmd, @QueryParam("data") String data) {
		Action act = new Action(plugin, cmd, data);
		Answer answer = process(act);
		return answer.toJson();
	}

	@POST
	public String postEndpoint(String jsonString) {
		Action act = new Action(jsonString);
		Answer answer = process(act);
		return answer.toJson();
	}

	@Override
	protected boolean isResponsible(Action act) {
		return true;
	}
}
