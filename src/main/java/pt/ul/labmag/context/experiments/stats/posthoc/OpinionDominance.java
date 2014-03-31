package pt.ul.labmag.context.experiments.stats.posthoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.posthoc.AbstractPostHocStatistics;

import pt.ul.labmag.context.experiments.CPModel;
import pt.ul.labmag.context.model.ContextPermability.OpinionMonitor;

/**
 * This stat reporter is used to export the network structures used on each
 * experiment with this we can later read the network files and analyse the
 * networks with a specialized network analysis package.
 * 
 * @author Davide Nunes
 * 
 */
public class OpinionDominance extends AbstractPostHocStatistics {

	@Override
	public String getName() {
		return "Agent Opinions";
	}

	@Override
	public Class<? extends Model> getTargetModelClass() {
		return CPModel.class;
	}

	private static final String C_DOMINANT_OPINION = "dominant-opinion";
	// number of opinion 0 occurrences seen
	private static final String C_NUM_DOMINAT_CHANGES = "num-dom-changes";
	// a proportion [0,1] of maximum opinion difference
	private static final String C_MAX_OP_DIFF_PROP = "max-op-diff";

	@Override
	public List<String> getDataColumns() {

		List<String> columns = new ArrayList<>();
		columns.add(C_DOMINANT_OPINION);
		columns.add(C_NUM_DOMINAT_CHANGES);
		columns.add(C_MAX_OP_DIFF_PROP);
		return columns;

	}

	@Override
	protected OpinionDominance createPrototype() {
		return new OpinionDominance();
	}

	@Override
	public List<Properties> measure(Model model) {

		List<Properties> snapshot = new ArrayList<>();
		CPModel contextModel = (CPModel) model;

		OpinionMonitor om = contextModel.getOpinionMonitor();
		Properties currAgent = new Properties();

		currAgent.setProperty(C_DOMINANT_OPINION,
				Integer.toString(om.getDominantOpinion()));
		currAgent.setProperty(C_NUM_DOMINAT_CHANGES,
				Integer.toString(om.getOpinionSwitchCount()));
		currAgent.setProperty(C_MAX_OP_DIFF_PROP,
				Double.toString(om.getMaxOpinionDiffProp()));

		snapshot.add(currAgent);

		return snapshot;
	}
}
