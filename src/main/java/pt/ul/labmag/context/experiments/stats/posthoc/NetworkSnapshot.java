package pt.ul.labmag.context.experiments.stats.posthoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bhave.experiment.Model;
import org.bhave.experiment.data.posthoc.AbstractPostHocStatistics;
import org.bhave.network.api.Link;
import org.bhave.network.api.Network;

import pt.ul.labmag.context.experiments.CPModel;

/**
 * This stat reporter is used to export the network structures used on each
 * experiment with this we can later read the network files and analyse the
 * networks with a specialized network analysis package.
 * 
 * @author Davide Nunes
 * 
 */
public class NetworkSnapshot extends AbstractPostHocStatistics {

	@Override
	public String getName() {
		return "Networks Layers";
	}

	@Override
	public Class<? extends Model> getTargetModelClass() {
		return CPModel.class;
	}

	private static final String C_NODE1 = "node1";
	private static final String C_NODE2 = "node2";
	private static final String C_LAYER = "layer";

	@Override
	public List<String> getDataColumns() {

		List<String> columns = new ArrayList<>();
		columns.add(C_NODE1);
		columns.add(C_NODE2);
		columns.add(C_LAYER);
		return columns;

	}

	@Override
	protected NetworkSnapshot createPrototype() {
		return new NetworkSnapshot();
	}

	@Override
	public List<Properties> measure(Model model) {

		List<Properties> snapshot = new ArrayList<>();
		CPModel contextModel = (CPModel) model;

		for (int l = 0; l < contextModel.networks.length; l++) {
			Network network = contextModel.networks[l];
			for (Link link : network.getLinks()) {
				Properties edge = new Properties();

				edge.setProperty(C_NODE1, Integer.toString(link.from().getID()));
				edge.setProperty(C_NODE2, Integer.toString(link.to().getID()));
				edge.setProperty(C_LAYER, Integer.toString(l));

				snapshot.add(edge);
			}
		}
		return snapshot;
	}

}
