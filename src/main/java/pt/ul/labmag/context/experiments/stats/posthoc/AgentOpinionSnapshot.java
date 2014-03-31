package pt.ul.labmag.context.experiments.stats.posthoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.posthoc.AbstractPostHocStatistics;

import pt.ul.labmag.context.experiments.CPModel;
import pt.ul.labmag.context.model.Agent;

/**
 * This stat reporter is used to export the network structures used on each
 * experiment with this we can later read the network files and analyse the
 * networks with a specialized network analysis package.
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
		return CPModel.class;
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
		CPModel contextModel = (CPModel) model;

		for (Agent agent : contextModel.agents) {
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
