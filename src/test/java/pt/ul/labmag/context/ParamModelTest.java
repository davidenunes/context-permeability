package pt.ul.labmag.context;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.bhave.experiment.Experiment;
import org.bhave.experiment.Model;
import org.bhave.sweeper.CombinedParameterSweep;

import junit.framework.TestCase;

public class ParamModelTest extends TestCase {

	private static final String TEST_CFG = "test_config.cfg";

	public void testPrototype() {
		String filename = TEST_CFG;
		File file = new File(Thread.currentThread().getContextClassLoader()
				.getResource(filename).getPath().toString());

		assertTrue(file.exists());

		Experiment experiment = null;
		try {
			experiment = Experiment.fromFile(file);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Experiment Creation Failled");
		}

		assertNotNull(experiment);

		// verify if the experiment has all the components it should
		Model model = experiment.getModel();

		for (Configuration cfg : experiment.getParameterSpace()) {

			assertTrue(cfg.containsKey(CombinedParameterSweep.CFG_ID_PARAM));

			model.loadConfiguration(cfg);

			Configuration cfgFromModel = model.getConfiguration();

			assertTrue(cfgFromModel
					.containsKey(CombinedParameterSweep.CFG_ID_PARAM));
		}
	}
}
