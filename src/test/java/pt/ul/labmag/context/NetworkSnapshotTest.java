package pt.ul.labmag.context;

import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import pt.ul.labmag.context.experiments.CPModel;
import pt.ul.labmag.context.experiments.stats.posthoc.NetworkSnapshot;
import junit.framework.TestCase;

public class NetworkSnapshotTest extends TestCase {

	@Test
	public void testNetworkStore() {
		CPModel model = new CPModel();
		Configuration cfg = model.getConfiguration();
		cfg.setProperty(CPModel.P_MAX_STEPS, 1);
		cfg.setProperty("cfg.id", 0);
		cfg.setProperty("run", 1);
		model.loadConfiguration(cfg);

		model.run();

		// try to store the networks
		NetworkSnapshot netSnap = new NetworkSnapshot();

		List<Properties> result = netSnap.getFullData(model);

		for (String column : netSnap.getFullDataColumns()) {
			System.out.print(column + ";");
		}

		for (Properties edge : result) {
			// print edge
			for (String column : netSnap.getFullDataColumns()) {
				System.out.print(edge.getProperty(column));
				System.out.print(";");
			}
			System.out.println();
		}
	}
}
