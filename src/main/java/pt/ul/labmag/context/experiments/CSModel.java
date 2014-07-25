package pt.ul.labmag.context.experiments;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Level;
import org.bhave.experiment.Model;
import org.bhave.experiment.data.producer.DataProducer;
import org.bhave.network.api.Network;
import org.bhave.sweeper.CombinedParameterSweep;

import pt.ul.labmag.context.model.Agent;
import pt.ul.labmag.context.model.ContextSwitching;

/**
 * Context Switching Model Wrapper that puts together the model and the
 * experiment framework.
 * 
 * TODO: I should refactor the experiment framework for allow for a easier
 * integration.
 * 
 * Note: the models are packaged with the experiment framework as a front-end.
 * To run different models, you just need to switch the model class in the
 * configuration file
 * 
 * @author Davide Nunes
 */
public class CSModel extends ContextSwitching implements ContextModel {
	private static final long serialVersionUID = 1L;

	// model log to display info
	long startTime = 0;

	protected Map<Integer, DataProducer> producers;

	public CSModel() {
		super(System.currentTimeMillis());
		producers = new HashMap<>();
		log.setLevel(Level.OFF);
	}

	@Override
	public Collection<? extends DataProducer> getProducers() {
		return producers.values();
	}

	@Override
	public void registerDataProducer(DataProducer producer) {
		this.producers.put(producer.getID(), producer);

	}

	@Override
	public DataProducer getDataProducer(int producerIndex) {
		return producers.get(producerIndex);
	}

	@Override
	public long getStep() {
		return schedule.getSteps();
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public int getRun() {
		int run = 1;
		if (config.containsKey(CombinedParameterSweep.RUN_PARAM)) {
			run = this.config.getInt(CombinedParameterSweep.RUN_PARAM);
		}
		return run;
	}



	final int DATA_PRODUCERS_EPOCH = 4;
	public static final String P_MEASURE_INTERVAL = "stat-interval";
	public static final int P_MEASURE_INTERVAL_DEFAULT = 1;

	/**
	 * The previous model sets up the agents to be executed, this one adds the
	 * data producers to the schedule
	 */
	@Override
	public void start() {
		super.start();

		// measure the model once at the beginning
		scheduleProducers();

	}

	/**
	 * EXECUTES THE PRODUCERS ONCE
	 * 
	 * TODO for future reference I need to refactor this in the next models.
	 * 
	 * This is not yet integrated in the experiment framework quite yet. For now
	 * it is simple enough. If the producers are available you just need to step
	 * through them and call produce for each one.
	 * 
	 * 
	 * @param repeating
	 */
	private void scheduleProducers() {
		for (Integer producerID : producers.keySet()) {
			final DataProducer producer = producers.get(producerID);
			producer.produce(this);
		}
	}

	@Override
	public Model create() {
		CSModel model = new CSModel();
		model.loadConfiguration(this.getConfiguration());
		return model;
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();

		this.start();
		log.info("sim started");

		while (!timeToStop()) {
			double time = this.schedule.getSteps();
			if (!this.schedule.step(this)) {
				break;
			}
			scheduleProducers();
			if (time % 50 == 0 && time != 0) {
				log.info("sim step:" + time);
			}
		}

		this.finish();
		log.info("sim ended");
	}

	public void finish() {
		super.finish();
	}

	private boolean timeToStop() {

		double consensusRequired = config.getDouble(P_CONSENSUS_REQUIRED);
		int population = agents.length;

		int[] opinionC = opinionCount();
		if (opinionC[0] / (population * 1.0) >= consensusRequired
				|| opinionC[1] / (population * 1.0) >= consensusRequired) {
			return true;
		}

		boolean withinSteps = schedule.getSteps() < config.getInt(P_MAX_STEPS);

		return !withinSteps;
	}

	/**
	 * Used to run quick tests with the default configuration
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		Model model = new CSModel();

		Thread t = new Thread(model);
		t.start();

		t.join();
		System.exit(0);
	}

	@Override
	public Network[] getNetworks() {
		return networks;
	}

	@Override
	public Agent[] getAgents() {
		return agents;
	}

}
