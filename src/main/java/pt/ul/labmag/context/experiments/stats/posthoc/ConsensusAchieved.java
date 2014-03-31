package pt.ul.labmag.context.experiments.stats.posthoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.posthoc.AbstractPostHocStatistics;
import org.bhave.experiment.data.posthoc.PostHocStatistics;

import pt.ul.labmag.context.experiments.CPModel;

/**
 * Returns 1 if the consensus was achieved according to the consensus ration
 * used to terminate the simulation
 * 
 * @author Davide Nunes
 * 
 */
public class ConsensusAchieved extends AbstractPostHocStatistics {

	@Override
	public String getName() {
		return "Consensus Achieved?";
	}

	@Override
	public Class<? extends Model> getTargetModelClass() {
		return CPModel.class;
	}

	@Override
	public List<Properties> measure(Model model) {
		CPModel m = (CPModel) model;

		int achieved = m.consensusAchieved() ? 1 : 0;
		Properties achievedP = new Properties();
		achievedP.setProperty(P_CONSENSUS_ACHIEVED, Integer.toString(achieved));

		List<Properties> props = new ArrayList<>(1);
		props.add(achievedP);

		return props;
	}

	private static final String P_CONSENSUS_ACHIEVED = "consensus-achieved";

	@Override
	public List<String> getDataColumns() {
		List<String> columns = new ArrayList<>(1);
		columns.add(P_CONSENSUS_ACHIEVED);
		return columns;
	}

	@Override
	protected PostHocStatistics createPrototype() {
		return new ConsensusAchieved();
	}

}
