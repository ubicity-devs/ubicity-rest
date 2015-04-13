package at.ac.ait.ubicity.http.impl;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import at.ac.ait.ubicity.commons.broker.JiTBroker;
import at.ac.ait.ubicity.commons.jit.Action;
import at.ac.ait.ubicity.commons.jit.Answer;
import at.ac.ait.ubicity.commons.jit.Answer.Status;

/**
 * Global HTTP Handler for receiving and forwarding Commands.
 *
 */
public abstract class AbstractHandler extends ResourceConfig {
	private static final Logger logger = Logger.getLogger(AbstractHandler.class);

	/**
	 * Check if Handler is able to handle the action command. <br/>
	 * Otherwise Answer with {@code Status.COMMAND_NOT_RECOGNIZED} is returned.
	 * 
	 * @param act
	 * @return
	 */
	protected abstract boolean isResponsible(Action act);

	protected Answer process(Action act) {
		Answer ans = null;

		if (isResponsible(act)) {
			ans = JiTBroker.process(act);
		} else {
			// Return objects without processing if command is not supported
			ans = new Answer(act, Status.COMMAND_NOT_RECOGNIZED);
		}

		logger.info("Process Status: " + ans.toJson());

		return ans;
	}
}
