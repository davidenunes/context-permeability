package pt.ul.labmag.context.experiments.stats;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.AbstractStatistics;
import org.bhave.experiment.data.Statistics;

import pt.ul.labmag.context.experiments.ContextModel;

public class OpinionCount extends AbstractStatistics {

	@Override
	public Properties measure(Model model) {
		ContextModel m = (ContextModel) model;
		Properties opinions = new Properties();

		int[] opCount = m.opinionCount();
		opinions.setProperty(P_OP0, Integer.toString(opCount[0]));
		opinions.setProperty(P_OP1, Integer.toString(opCount[1]));

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

	private static final String P_OP0 = "num-opinion-0";
	private static final String P_OP1 = "num-opinion-1";

	@Override
	public List<String> getColumnNames() {
		List<String> cols = new LinkedList<String>();
		cols.add(P_OP0);
		cols.add(P_OP1);
		return cols;
	}

	@Override
	protected Statistics createPrototype() {
		return new OpinionCount();
	}

}
