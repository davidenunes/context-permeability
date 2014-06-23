package pt.ul.labmag.context.switching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.math3.util.FastMath;
import org.bhave.experiment.Experiment;
import org.bhave.experiment.Model;
import org.junit.Test;

import pt.ul.labmag.context.experiments.CSModel;
import pt.ul.labmag.context.model.Agent;
import pt.ul.labmag.context.model.CSAgent;

/**
 * A test unit for the context switching model
 * 
 * @author Davide Nunes
 * 
 */
public class ContextSwitchingTest extends TestCase {

	/**
	 * Initially, the agents should be distributed randomly throughout both the
	 * networks. They are only active in one network at a time.
	 * 
	 * I set a test configuration with 2 networks to evaluate this initial
	 * distribution.
	 */
	@Test
	public void testAgentDistribution() {
		System.out.println("Test agent distribution...");
		CSModel model = new CSModel();
		assertNotNull(model);

		model.start();

		int[] numAgents = new int[2];

		for (Agent agent : model.agents) {
			CSAgent csAgent = (CSAgent) agent;
			numAgents[csAgent.getCurrentNetwork()]++;
		}

		// agents are equally distributed
		assertTrue(FastMath.abs(numAgents[0] - numAgents[1]) <= 1);

		model.finish();

		System.out.println("[OK] agents equally distributed");
	}

	private static final String CS_DEFAULT = "test_context_switching.cfg";

	/**
	 * In this test, I'll check if the new parameter is being properly processed
	 * by the experiment framework. We have two situations, int the first (this
	 * test), the model can be configured using only one parameter value for
	 * context switching.
	 * 
	 * We need only to assign the value to the first network and the remaining
	 * will take this value as well.
	 */
	@Test
	public void testDefaultParams() {
		System.out
				.println("Testing experiment framework, creating model from file...");
		// get the configuration file
		File file = new File(Thread.currentThread().getContextClassLoader()
				.getResource(CS_DEFAULT).getPath().toString());

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
		CSModel csModel = (CSModel) model;

		for (Configuration cfg : experiment.getParameterSpace()) {

			// the default configuration is stored in the key for the first
			// network 0 which is network.0.cs
			assertTrue(cfg.containsKey(csModel.getCSPKey(0)));

			// check if the parameter name is correct
			assertEquals("network.0.cs", csModel.getCSPKey(0));
			// check the parameter value
			assertEquals(0.5, cfg.getDouble(csModel.getCSPKey(0)));

			model.loadConfiguration(cfg);
			Configuration cfgFromModel = model.getConfiguration();

			// after configuration loading, the value should not be overriden
			assertTrue(cfgFromModel.containsKey(csModel.getCSPKey(0)));
			assertEquals(0.5, cfgFromModel.getDouble(csModel.getCSPKey(0)));
		}
		System.out
				.println("[OK] model created with a single homogenous configuration value");
	}

	private static final String CS_MORE_PARAMS = "test_context_switching_more_params.cfg";

	/**
	 * Test to assess if all the networks are assign the correct switching value
	 */
	@Test
	public void testMoreParameters() {
		System.out.println("Validating loading heterogenous parameters...");
		// get the configuration file
		File file = new File(Thread.currentThread().getContextClassLoader()
				.getResource(CS_MORE_PARAMS).getPath().toString());

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
		CSModel csModel = (CSModel) model;

		for (Configuration cfg : experiment.getParameterSpace()) {

			assertTrue(cfg.containsKey(csModel.getCSPKey(0)));
			assertTrue(cfg.containsKey(csModel.getCSPKey(1)));

			// check if the parameter name is correct
			assertEquals("network.0.cs", csModel.getCSPKey(0));
			assertEquals("network.1.cs", csModel.getCSPKey(1));
			// check the parameter value
			assertEquals(0.7, cfg.getDouble(csModel.getCSPKey(0)));
			assertEquals(0.8, cfg.getDouble(csModel.getCSPKey(1)));

			model.loadConfiguration(cfg);
			Configuration cfgFromModel = model.getConfiguration();

			// after configuration loading, the value should not be overriden
			assertTrue(cfgFromModel.containsKey(csModel.getCSPKey(0)));
			assertTrue(cfgFromModel.containsKey(csModel.getCSPKey(1)));
			assertEquals(0.7, cfgFromModel.getDouble(csModel.getCSPKey(0)));
			assertEquals(0.8, cfgFromModel.getDouble(csModel.getCSPKey(1)));
		}
		System.out
				.println("[OK] model created with a single heterogenous configuration value");
	}

	private static final String CS_ALLWAYS = "allways_switch.cfg";

	/**
	 * I created a configuration file with the switching probability set to 1
	 * which means the agents will allways switch from one context to another
	 * after one step.
	 * 
	 * They should at least, this is what I'm testing after all.
	 */
	@Test
	public void testSwitching() {
		System.out.println("Validating loading heterogenous parameters...");
		// get the configuration file
		File file = new File(Thread.currentThread().getContextClassLoader()
				.getResource(CS_ALLWAYS).getPath().toString());

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
		CSModel csModel = (CSModel) model;

		// start the model manually
		csModel.start();

		// record the initial positions of the agents
		int[] initialNetworks = new int[csModel.agents.length];
		for (int i = 0; i < initialNetworks.length; i++) {
			initialNetworks[i] = ((CSAgent) csModel.agents[i])
					.getCurrentNetwork();
		}

		// step the model manually
		csModel.schedule.step(csModel);

		// they should all have switched networks, verify this
		for (int i = 0; i < initialNetworks.length; i++) {
			CSAgent csAgent = (CSAgent) csModel.agents[i];
			assertTrue(csAgent.getCurrentNetwork() != initialNetworks[i]);
		}
		System.out.println("[OK] Switching probability is working");
	}

	
}
