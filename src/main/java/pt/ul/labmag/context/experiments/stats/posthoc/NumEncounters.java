package pt.ul.labmag.context.experiments.stats.posthoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.posthoc.AbstractPostHocStatistics;
import org.bhave.experiment.data.posthoc.PostHocStatistics;

import pt.ul.labmag.context.experiments.ContextModel;

/**
 * Used to export the number of encounters that occurred during a simulation
 * 
 * @author Davide Nunes
 * 
 */
public class NumEncounters extends AbstractPostHocStatistics {

	@Override
	public String getName() {
		return "Total Number of Encounters";
	}

	@Override
	public Class<? extends Model> getTargetModelClass() {
		return ContextModel.class;
	}

	/**
	 * Seems overkill just to register the number of encounters but it fits the
	 * rest of the framework and records other information such as the current
	 * step (number of steps with which the model teminated) the current run,
	 * and the current configuration id which will map this value to a specific
	 * experiment configuration.
	 */
	@Override
	public List<Properties> measure(Model model) {
		ContextModel m = (ContextModel) model;

		List<Properties> stats = new ArrayList<>(1);
		Properties numEncounters = new Properties();

		numEncounters.setProperty(P_TOTAL_ENCOUTNERS,
				Integer.toString(m.getTotalNumEncounters()));

		stats.add(numEncounters);
		return stats;
	}

	private static final String P_TOTAL_ENCOUTNERS = "total-encounters";

	@Override
	public List<String> getDataColumns() {
		List<String> columns = new ArrayList<>(1);
		columns.add(P_TOTAL_ENCOUTNERS);
		return columns;
	}

	@Override
	protected PostHocStatistics createPrototype() {
		return new NumEncounters();
	}

}
