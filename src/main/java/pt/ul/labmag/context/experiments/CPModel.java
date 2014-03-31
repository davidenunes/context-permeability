package pt.ul.labmag.context.experiments;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Level;
import org.bhave.experiment.Model;
import org.bhave.experiment.data.producer.DataProducer;
import org.bhave.sweeper.CombinedParameterSweep;
import pt.ul.labmag.context.model.ContextPermability;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * 
 * @author Davide Nunes
 */
public class CPModel extends ContextPermability implements
		Model {
	private static final long serialVersionUID = 1L;

	// model log to display info
	long startTime = 0;

	protected Map<Integer, DataProducer> producers;

	public CPModel() {
		super();
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

	@Override
	public Configuration getConfiguration() {

		if (this.config == null) {
			this.config = defaultConfiguration();
		}
		return this.config;
	}

	final int DATA_PRODUCERS_EPOCH = 2;
	public static final String P_MEASURE_INTERVAL = "stat-interval";
	public static final int P_MEASURE_INTERVAL_DEFAULT = 100;

	/**
	 * The previous model sets up the agents to be executed, this one adds the
	 * data producers to the schedule
	 */
	@Override
	public void start() {
		super.start();

		// initial epoch for producers, starts after agents are executed
		for (Integer producerID : producers.keySet()) {
			final DataProducer producer = producers.get(producerID);
			/**
			 * Create a data producer steppable
			 */
			Steppable producerStep = new Steppable() {
				private static final long serialVersionUID = 1L;

				@Override
				public void step(SimState state) {
					CPModel model = (CPModel) state;
					producer.produce(model);
				}
			};
			schedule.scheduleRepeating(Schedule.EPOCH, DATA_PRODUCERS_EPOCH,
					producerStep, config.getInt(P_MEASURE_INTERVAL));
		}
	}

	@Override
	public Model create() {
		CPModel model = new CPModel();
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
			if (time % 50 == 0 && time != 0) {
				log.info("sim step:" + time);
			}
		}
		this.finish();
		log.info("sim ended");
	}

	private boolean timeToStop() {

		double consensusRequired = config.getDouble(P_CONSENSUS_REQUIRED);
		int population = agents.length;

		int[] opinionC = opinionCount();
		if (opinionC[0] / (population * 1.0) >= consensusRequired
				|| opinionC[1] / (population * 1.0) >= consensusRequired) {
			return true;
		}

		boolean withinSteps = schedule.getSteps() <= config.getInt(P_MAX_STEPS);

		return !withinSteps;

	}

	/**
	 * Used to run quick tests with the default configuration
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		Model model = new CPModel();

		Thread t = new Thread(model);
		t.start();

		t.join();
		System.exit(0);
	}
}
