package pt.ul.labmag.context.segregation;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.bhave.experiment.Experiment;
import org.bhave.experiment.Model;
import org.bhave.sweeper.CombinedParameterSweep;
import org.junit.Test;

import pt.ul.labmag.context.experiments.CTModel;

/**
 * A test unit for the context segregation model
 * 
 * @author Davide Nunes
 * 
 */
public class ContextSegregationTest extends TestCase {

	private static final String CT_DEFAULT = "test_context_segregation.cfg";

	/**
	 * In this test, I'll check if the new parameter is being properly processed
	 * by the experiment framework. We have two situations, int the first (this
	 * test), the model can be configured using only one parameter value for
	 * context switching.
	 * 
	 * We need only to assign the value to the first network and the
	 * ContextSegSwitchingTestremaining will take this value as well.
	 */
	@Test
	public void testDefaultParams() {
		System.out
				.println("Testing experiment framework, creating model from file...");
		// get the configuration file
		File file = new File(Thread.currentThread().getContextClassLoader()
				.getResource(CT_DEFAULT).getPath().toString());

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

		// this cast should work
		CTModel ctModel = (CTModel) model;

		for (Configuration cfg : experiment.getParameterSpace()) {

			// the default configuration is stored in the key for the first
			// network 0 which is network.0.cs
			assertTrue(cfg.containsKey(ctModel.getCTPKey(0)));

			// check if the parameter name is correct
			assertEquals("network.0.ct", ctModel.getCTPKey(0));
			// check the parameter value
			assertEquals(0.5, cfg.getDouble(ctModel.getCTPKey(0)));

			model.loadConfiguration(cfg);
			Configuration cfgFromModel = model.getConfiguration();

			// after configuration loading, the value should not be overriden
			assertTrue(cfgFromModel.containsKey(ctModel.getCTPKey(0)));
			assertEquals(0.5, cfgFromModel.getDouble(ctModel.getCTPKey(0)));
		}
		System.out
				.println("[OK] model created with a single homogenous configuration value");
	}

	@Test
	public void testRunModel() {
		System.out
				.println("Testing experiment framework, creating model from file...");
		// get the configuration file
		File file = new File(Thread.currentThread().getContextClassLoader()
				.getResource(CT_DEFAULT).getPath().toString());

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

		// this cast should work
		CTModel ctModel = (CTModel) model;

		// get the first configuration
		CombinedParameterSweep sweep = experiment.getParameterSpace();
		Configuration cfg = sweep.iterator().next();

		ctModel.loadConfiguration(cfg);

		// start the model
		ctModel.start();

		// step once to make sure nothing is broken
		ctModel.schedule.step(ctModel);
		System.out.println("[OK] model run without problems.");
	}

}
