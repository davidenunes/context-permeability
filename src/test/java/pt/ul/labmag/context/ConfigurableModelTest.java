package pt.ul.labmag.context;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.bhave.experiment.Experiment;
import org.bhave.experiment.ExperimentRunner;
import org.bhave.experiment.Model;
import org.bhave.experiment.data.DataExporter;
import org.bhave.experiment.data.Statistics;
import org.bhave.experiment.data.consumer.DataConsumer;
import org.bhave.experiment.data.producer.DataProducer;
import org.bhave.sweeper.CombinedParameterSweep;
import org.bhave.sweeper.ParameterSweep;

import junit.framework.TestCase;

/**
 * In this test we check if the simulation model can be configured correctly and
 * if the statistics are being correctly measured and presented.
 * 
 * @author Davide Nunes
 * 
 */
public class ConfigurableModelTest extends TestCase {

	// the test configuration file
	private static final String TEST_CFG = "test_config.cfg";

	public void testLoadConfiguration() {
		// test configuration file
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
		assertTrue(model instanceof pt.ul.labmag.context.experiments.CPModel);

		ExperimentRunner runner = experiment.getRunner();
		assertTrue(runner instanceof org.bhave.experiment.run.MultiThreadedRunner);

		// one producer only loaded
		Collection<? extends DataProducer> producers = experiment
				.getProducers();
		assertEquals(1, producers.size());

		// get statistics
		DataProducer producer = producers.iterator().next();
		List<Statistics> stats = producer.getStatistics();
		assertEquals(2, stats.size());

		// one consumer loaded
		Collection<? extends DataConsumer> consumers = experiment
				.getConsumers();
		assertEquals(1, consumers.size());

		// get exporters
		List<DataExporter> exporters = consumers.iterator().next()
				.getExporters();
		assertEquals(1, exporters.size());

		// Test Parameter space
		CombinedParameterSweep paramSpace = experiment.getParameterSpace();
		int runs = paramSpace.getNumRuns();
		assertEquals(2, runs);

		// get parameter sweeps
		Collection<? extends ParameterSweep> sweeps = paramSpace
				.getParameterSweeps();

		assertEquals(7, sweeps.size());

		// all good try to load the configuration into the model
		try {
			Configuration config = paramSpace.iterator().next();
			model.loadConfiguration(config);
		} catch (Exception cfgE) {
			cfgE.printStackTrace();
			fail("The configuration could not be loaded into the model");
		}
	}

	/**
	 * the test configuration has 1 run so this should be quick
	 */
	public void testRunConfiguredModel() {
		// test configuration file
		String filename = "full_cfg_test.cfg";
		File file = new File(Thread.currentThread().getContextClassLoader()
				.getResource(filename).getPath().toString());
		Experiment experiment = Experiment.fromFile(file);

		ExperimentRunner runner = experiment.getRunner();
		runner.load(experiment);
		// we don't want to write anything to files
		runner.setPrintParamSpace(false);

		// try to run it
		try {
			runner.start();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error Running the Experiment...");
		}

	}
}
