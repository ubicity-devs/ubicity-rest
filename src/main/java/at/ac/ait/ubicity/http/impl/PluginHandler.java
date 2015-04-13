package at.ac.ait.ubicity.http.impl;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import at.ac.ait.ubicity.commons.jit.Action;
import at.ac.ait.ubicity.commons.jit.Answer;

/**
 * Global HTTP Handler for loading and unloading plugins.
 *
 */
@Path("admin")
public class PluginHandler extends AbstractHandler {

	private static List<String> COMMANDS = Arrays.asList("list");

	@Path("plugins")
	@GET
	public String getEndpoint(@QueryParam("cmd") String cmd, @QueryParam("data") String data) {
		Action act = new Action("ubicity-core", cmd, data);
		Answer answer = process(act);
		return answer.toJson();
	}

	@POST
	public String postEndpoint(String jsonString) {
		Action act = new Action(jsonString);
		act.setReceiver("ubicity-core");

		Answer answer = process(act);
		return answer.toJson();
	}

	@Override
	protected boolean isResponsible(Action act) {
		return COMMANDS.contains(act.getCommand());
	}
}
