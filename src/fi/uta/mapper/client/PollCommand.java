package fi.uta.mapper.client;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public class PollCommand implements RepeatingCommand {

	@Override
	public boolean execute() {
		Model.getData();
		return true;
	}

}
