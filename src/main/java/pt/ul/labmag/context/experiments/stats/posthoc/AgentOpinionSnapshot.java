package pt.ul.labmag.context.experiments.stats.posthoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.posthoc.AbstractPostHocStatistics;

import pt.ul.labmag.context.experiments.ContextModel;
import pt.ul.labmag.context.model.Agent;

/**
 * This stat reporter is used to export the agent memory values during the
 * simulation (number of observations for each opinion).
 * 
 * @author Davide Nunes
 * 
 */
public class AgentOpinionSnapshot extends AbstractPostHocStatistics {

	@Override
	public String getName() {
		return "Agent Opinions";
	}

	@Override
	public Class<? extends Model> getTargetModelClass() {
		return ContextModel.class;
	}

	private static final String C_AGENT_ID = "agent-id";
	// number of opinion 0 occurrences seen
	private static final String C_OPINION_0_MEMORY = "opinion-0-memory";
	private static final String C_OPINION_1_MEMORY = "opinion-1-memory";

	@Override
	public List<String> getDataColumns() {

		List<String> columns = new ArrayList<>();
		columns.add(C_AGENT_ID);
		columns.add(C_OPINION_0_MEMORY);
		columns.add(C_OPINION_1_MEMORY);
		return columns;

	}

	@Override
	protected AgentOpinionSnapshot createPrototype() {
		return new AgentOpinionSnapshot();
	}

	@Override
	public List<Properties> measure(Model model) {

		List<Properties> snapshot = new ArrayList<>();
		ContextModel contextModel = (ContextModel) model;

		Agent[] agents = contextModel.getAgents();
		for (Agent agent : agents) {
			Properties currAgent = new Properties();

			int[] memory = agent.getOpinionMemory();
			currAgent.setProperty(C_AGENT_ID, Integer.toString(agent.getID()));
			currAgent.setProperty(C_OPINION_0_MEMORY,
					Integer.toString(memory[0]));
			currAgent.setProperty(C_OPINION_1_MEMORY,
					Integer.toString(memory[1]));

			snapshot.add(currAgent);

		}
		return snapshot;
	}
}
