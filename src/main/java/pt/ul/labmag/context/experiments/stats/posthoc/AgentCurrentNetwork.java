package pt.ul.labmag.context.experiments.stats.posthoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.posthoc.AbstractPostHocStatistics;

import pt.ul.labmag.context.experiments.CSModel;
import pt.ul.labmag.context.model.Agent;
import pt.ul.labmag.context.model.CSAgent;

/**
 * This stat reporter is used to export the agent current position in the
 * networks for Context Switching model. One can agent can only be active in one
 * network at a time.
 * 
 * @author Davide Nunes
 * 
 */
public class AgentCurrentNetwork extends AbstractPostHocStatistics {

	@Override
	public String getName() {
		return "Agent Current Network";
	}

	@Override
	public Class<? extends Model> getTargetModelClass() {
		return CSModel.class;
	}

	private static final String C_AGENT_ID = "agent-id";
	// current position / current network layer
	private static final String C_CURRENT_NETWORK = "current-network";

	@Override
	public List<String> getDataColumns() {

		List<String> columns = new ArrayList<>();
		columns.add(C_AGENT_ID);
		columns.add(C_CURRENT_NETWORK);
		return columns;

	}

	@Override
	protected AgentCurrentNetwork createPrototype() {
		return new AgentCurrentNetwork();
	}

	@Override
	public List<Properties> measure(Model model) {

		List<Properties> snapshot = new ArrayList<>();
		CSModel contextModel = (CSModel) model;

		for (Agent agent : contextModel.agents) {
			CSAgent csAgent = (CSAgent) agent;
			Properties currAgent = new Properties();

			currAgent
					.setProperty(C_AGENT_ID, Integer.toString(csAgent.getID()));
			currAgent.setProperty(C_CURRENT_NETWORK,
					Integer.toString(csAgent.getCurrentNetwork()));

			snapshot.add(currAgent);

		}
		return snapshot;
	}
}
