package pt.ul.labmag.context.experiments.stats;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.AbstractStatistics;
import org.bhave.experiment.data.Statistics;

import pt.ul.labmag.context.experiments.ContextModel;
import pt.ul.labmag.context.model.ContextPermeability.OpinionMonitor;

/**
 * Used to record data about opinion dominance througout the simulation
 * 
 * 
 */
public class OpinionDominance extends AbstractStatistics {

	@Override
	public Properties measure(Model model) {
		ContextModel m = (ContextModel) model;
		Properties opinions = new Properties();

		OpinionMonitor opMonitor = m.getOpinionMonitor();

		double diff = opMonitor.getMaxOpinionDiffProp();
		int domOp = opMonitor.getDominantOpinion();
		int numChanges = opMonitor.getOpinionSwitchCount();

		opinions.setProperty(P_OP_DIFF, Double.toString(diff));
		opinions.setProperty(P_OP_DOMINANT, Integer.toString(domOp));
		opinions.setProperty(P_OP_NUM_CHANGES, Integer.toString(numChanges));

		return opinions;
	}

	@Override
	public String getName() {
		return "Opinion Count";
	}

	@Override
	public Class<? extends Model> getTargetModelClass() {
		return ContextModel.class;
	}

	// current opinion difference proportion
	private static final String P_OP_DIFF = "op-diff-prop";
	// current dominant opinion
	private static final String P_OP_DOMINANT = "op-dominant";
	// number of changes in dominance
	private static final String P_OP_NUM_CHANGES = "num-changes";

	@Override
	public List<String> getColumnNames() {
		List<String> cols = new LinkedList<String>();
		cols.add(P_OP_DIFF);
		cols.add(P_OP_DOMINANT);
		cols.add(P_OP_NUM_CHANGES);
		return cols;
	}

	@Override
	protected Statistics createPrototype() {
		return new OpinionDominance();
	}

}
