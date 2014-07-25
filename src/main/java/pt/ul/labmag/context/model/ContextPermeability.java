package pt.ul.labmag.context.model;

import static pt.ul.labmag.context.experiments.CPModel.P_MEASURE_INTERVAL;
import static pt.ul.labmag.context.experiments.CPModel.P_MEASURE_INTERVAL_DEFAULT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bhave.network.NetworkModule;
import org.bhave.network.api.Network;
import org.bhave.network.model.KRegularModel;
import org.bhave.network.model.NetworkModel;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A model of context permeability in multiple social networks
 * 
 * @author Davide Nunes
 * 
 *         TODO re-factor this to integrate the model with the parameterizable
 *         classF
 */
public class ContextPermeability extends sim.engine.SimState {
	private static final long serialVersionUID = 1L;
	protected Logger log;
	// attributes
	protected Configuration config;

	public Agent[] agents;
	public Network[] networks;
	public NetworkModel[] netModels;

	// STATS
	private int totalNumEncounters;

	public void recordEncounter() {
		totalNumEncounters++;
	}

	// constructors
	public ContextPermeability(long seed) {
		super(seed);
		log = Logger.getLogger(ContextPermeability.class);
		log.setLevel(Level.OFF);
		BasicConfigurator.configure();
	}

	public ContextPermeability() {
		this(System.currentTimeMillis());
	}

	@Override
	public void start() {
		super.start();

		// if we start the model without configuring it first
		// get the default configuration
		if (this.config == null) {
			loadConfiguration(defaultConfiguration());
			log.warn("The simulation model was not configured by the client. The default configuration was used instead.");
		}

		// initialize agent vector
		int numAgents = (int) config.getProperty(P_NUM_AGENTS);
		agents = new Agent[numAgents];

		// initialize network vector
		int numNetworks = (int) config.getProperty(P_NUM_NETWORKS);
		networks = new Network[numNetworks];

		createAgents(numAgents);

		// create networks
		for (int n = 0; n < numNetworks; n++) {
			networks[n] = createNetwork(n);
		}

		// distribute initial opinions before the opinion dominance
		initialOpinions();

		// add the opinion dominance monitoring to the schedule
		om = new OpinionMonitor(this);
		schedule.scheduleRepeating(Schedule.EPOCH, 3, om);
	}

	protected void createAgents(int numAgents) {
		// create agents
		for (int i = 0; i < numAgents; i++) {
			final Agent agent = createAgent(i);
			agents[i] = agent;
			schedule.scheduleRepeating(Schedule.EPOCH, 1, agent);
		}
	}

	protected Agent createAgent(int id) {
		return new Agent(id, this);
	}

	// added to schedule to monitor opinions
	private OpinionMonitor om;

	public OpinionMonitor getOpinionMonitor() {
		return om;
	}

	private void initialOpinions() {
		// we are using two opinion values so choose half of the agents randomly
		ArrayList<Agent> agentsLeft = new ArrayList<>(Arrays.asList(agents));

		int agentsPerOpinion = agents.length / 2;
		for (int opinion : new int[] { 0, 1 }) {
			int added = 0;
			while (added < agentsPerOpinion && !agentsLeft.isEmpty()) {

				int nextID = 0;
				if (agentsLeft.size() > 1) {
					nextID = random.nextInt(agentsLeft.size());
				}

				Agent agent = (Agent) agentsLeft.get(nextID);

				agent.setOpinion(opinion);
				agentsLeft.remove(agent);
				added++;
			}

		}
	}

	/***********************************************************************
	 * STATS
	 */
	public int[] opinionCount() {
		int opinion0 = 0;
		int opinion1 = 0;

		for (Agent agent : agents) {
			if (agent.getOpinion() == 0)
				opinion0++;
			else
				opinion1++;
		}
		return new int[] { opinion0, opinion1 };
	}

	/*********************************************************************
	 * LOAD CONFIGURATION
	 */

	/**
	 * Creates a network to occupy the level <b> n </b> layer of the simulation
	 * model.
	 * <p>
	 * The configuration for each network level should be stored in the
	 * configuration using the base name which is network.0 =
	 * org.bhave.network.model.BAModel for instance. These can be set
	 * individually or if the there's only one definition but a number of
	 * networks > n the model uses the definition for the first network level n
	 * = 0.
	 * 
	 * </p>
	 * 
	 * <p>
	 * As an example you can have a configuration
	 * 
	 * network.0.= regular network.0.k = 10
	 * 
	 * num-networks = 10
	 * 
	 * In this case, the 10 networks will use the first network definition
	 * 
	 * </p>
	 * 
	 * @param n
	 * @return
	 */
	private Network createNetwork(int n) {
		// get a new seed for the generator
		Configuration modelCfg = netModels[n].getConfiguration();
		modelCfg.setProperty("seed", this.random.nextLong());

		try {
			netModels[n].configure(modelCfg);
		} catch (ConfigurationException e) {
			log.error("something could be wrong with the network library, configure should not throw an exception here");
		}

		Network network = netModels[n].generate();
		return network;
	}

	public static final String P_CONSENSUS_REQUIRED = "consensus-required";
	public static final double P_CONSENSUS_REQUIRED_DEFAULT = 1.0;

	public static final String P_NETWORK_BASE = "network";

	public static final String P_MAX_STEPS = "max-steps";
	private static final int P_MAX_STEPS_DEFAULT = 2000;

	public static final String P_NUM_NETWORKS = "num-networks";
	private static final int P_NUM_NETWORKS_DEFAULT = 2;

	public static final String P_NUM_AGENTS = "num-agents";
	private static final int P_NUM_AGENTS_DEFAULT = 10;

	/**
	 * Creates a default configuration for this simulation model
	 * 
	 * @return a default configuration object
	 * 
	 * 
	 * @see Configuration
	 */
	protected Configuration defaultConfiguration() {
		Configuration defaultCfg = new PropertiesConfiguration();

		defaultCfg.setProperty(P_MAX_STEPS, P_MAX_STEPS_DEFAULT);
		defaultCfg.setProperty(P_NUM_AGENTS, P_NUM_AGENTS_DEFAULT);

		// stat interval measure
		defaultCfg.setProperty(P_MEASURE_INTERVAL, P_MEASURE_INTERVAL_DEFAULT);

		defaultCfg.setProperty(P_NUM_NETWORKS, P_NUM_NETWORKS_DEFAULT);

		String baseNetConfigP = P_NETWORK_BASE + ".0";

		defaultCfg.setProperty(baseNetConfigP,
				"org.bhave.network.model.KRegularModel");
		defaultCfg.setProperty(baseNetConfigP + "." + KRegularModel.P_K, 5);
		defaultCfg.setProperty(
				baseNetConfigP + "." + KRegularModel.P_NUM_NODES,
				P_NUM_AGENTS_DEFAULT);

		defaultCfg.addProperty(P_CONSENSUS_REQUIRED,
				P_CONSENSUS_REQUIRED_DEFAULT);

		return defaultCfg;
	}

	public Map<String, Class<? extends Object>> getConfigurableParameters() {
		Map<String, Class<? extends Object>> params = new HashMap<>();

		params.put(P_MAX_STEPS, Integer.class);
		params.put(P_NUM_AGENTS, Integer.class);
		params.put(P_MEASURE_INTERVAL, Integer.class);
		params.put(P_NUM_NETWORKS, Integer.class);
		params.put(P_CONSENSUS_REQUIRED, Double.class);

		return params;
	}

	public void loadConfiguration(Configuration cfg) {
		// load the default values and overrite the values that are not passed
		loadDefaultConfiguration(cfg);

		// init injector to load networks and network models
		if (injector == null) {
			injector = Guice.createInjector(new NetworkModule());
		}

		// load network models
		int numNetworks = config.getInt(P_NUM_NETWORKS);
		if (numNetworks < 1) {
			throw new RuntimeException(
					"Invalid configuration: the number of networks must be > 1");
		}

		netModels = new NetworkModel[numNetworks];

		// load the first config
		int m = 0;

		NetworkModel model0 = loadNetworkModel(m);
		if (model0 == null) {
			throw new RuntimeException(
					"Invalid configuration: there must be at least one currect network configuration");
		} else {
			netModels[m] = model0;
			// all good, load the rest of the network models
			for (m = 1; m < numNetworks; m++) {
				NetworkModel model = loadNetworkModel(m);
				if (model != null) {
					netModels[m] = model;
				} else {
					log.warn("Network model class configuration for layer " + m
							+ " was not found using the same model as layer 0");
					netModels[m] = model0;
				}

			}
		}
	}

	/**
	 * Loads and configures a network model for the layer n
	 * 
	 * @param n
	 *            layer for this network, it searches in the configuration for
	 *            network.n etc
	 * 
	 * @return a network model to generate network instances or null
	 */
	private NetworkModel loadNetworkModel(int n) {
		String modelClassName = null;
		try {
			String netModelBase = P_NETWORK_BASE + "." + n;

			modelClassName = config.getString(netModelBase);

			@SuppressWarnings("unchecked")
			Class<NetworkModel> modelClass = (Class<NetworkModel>) Class
					.forName(modelClassName);
			NetworkModel model = injector.getInstance(modelClass);
			log.debug("Network Model Loaded: " + modelClassName);

			Configuration netModelCfg = model.getConfiguration();

			Iterator<String> modelProps = netModelCfg.getKeys();
			// configure model
			while (modelProps.hasNext()) {
				String propKey = modelProps.next();
				String propKeyCFG = netModelBase + "." + propKey;

				Object value = null;
				if (config.containsKey(propKeyCFG)) {
					// override default
					value = config.getProperty(propKeyCFG);
					netModelCfg.setProperty(propKey, value);
					log.debug("configured property: " + propKey + "=" + value);

				}

			}
			netModelCfg.setProperty("numNodes",
					this.config.getInt(P_NUM_AGENTS));

			model.configure(netModelCfg);

			return model;
		} catch (ClassNotFoundException ex) {

			return null;
		} catch (ConfigurationException ex) {
			throw new RuntimeException(ex.getMessage());
		} catch (Exception ex) {

			return null;
		}
	}

	Injector injector;

	/**
	 * Loads the default configuration into the model
	 * 
	 * @param cfg
	 *            the configuration to be loaded that overrides any values of
	 *            the default.
	 */
	private void loadDefaultConfiguration(Configuration cfg) {
		Configuration defaultCfg = defaultConfiguration();
		this.config = new PropertiesConfiguration();

		// load default
		Iterator<String> defaultPropsIt = defaultCfg.getKeys();
		while (defaultPropsIt.hasNext()) {
			String key = defaultPropsIt.next();
			this.config.setProperty(key, defaultCfg.getProperty(key));
		}

		// overwrite
		Iterator<String> cfgPropsIt = cfg.getKeys();
		while (cfgPropsIt.hasNext()) {
			String key = cfgPropsIt.next();
			this.config.setProperty(key, cfg.getProperty(key));
		}
	}

	public int getTotalNumEncounters() {
		return totalNumEncounters;
	}

	/**
	 * Returns True if consensus was reached, and false otherwise
	 * 
	 * @return
	 */
	public boolean consensusAchieved() {
		int[] opinions = this.opinionCount();

		double consensus = config.getDouble(P_CONSENSUS_REQUIRED);

		boolean reached = (opinions[0] / (agents.length * 1.0) >= consensus)
				|| (opinions[1] / (agents.length * 1.0) >= consensus);

		return reached;

	}

	/**
	 * Used to track the state of convergence and if the stability phase was
	 * reached mark it
	 * 
	 * We defined the notion of instability by the bounding of the proportions
	 * between both sides of the choice in a 20% band. That is, we consider that
	 * the instability period is over as soon as the difference between the
	 * proportion of sides is higher than 20%. The winning side by that time is
	 * invariably the final winner of the game.
	 * 
	 * Question, whats the proportion necessary to win a consensus game?
	 * 
	 * @author Davide Nunes
	 * 
	 */
	public static class OpinionMonitor implements Steppable {
		private static final long serialVersionUID = 1L;

		// max opinion count different proportion (in relation to population
		// size)
		private double maxDiff = 0;
		private double tempMaxDiff = 0;

		// the ipinion that occurrs the most
		private int dominantOpinion = 0;
		private int lastDominantOpinion = 0;

		// number of times dominant opinion changed
		private int countSwitch = 0;

		// private LinkedList<Long> changeSteps;

		// register whinning opinion +value in the constructor
		public OpinionMonitor(ContextPermeability model) {
			// changeSteps = new LinkedList<>();
			int[] opinions = model.opinionCount();

			dominantOpinion = opinions[0] >= opinions[1] ? 0 : 1;
			lastDominantOpinion = dominantOpinion;

			int diff = FastMath.abs(opinions[0] - opinions[1]);

			double prop = diff / (model.agents.length * 1.0);

			maxDiff = prop;
			tempMaxDiff = maxDiff;
		}

		@Override
		public void step(SimState state) {
			ContextPermeability model = (ContextPermeability) state;
			int[] opinions = model.opinionCount();

			int diff = FastMath.abs(opinions[0] - opinions[1]);
			double prop = diff / (model.agents.length * 1.0);

			if (prop > maxDiff) {
				tempMaxDiff = prop;
			}

			int dominantOpinion = opinions[0] >= opinions[1] ? 0 : 1;

			// opinion dominance switch occurred
			if (dominantOpinion != lastDominantOpinion) {
				countSwitch++;

				// changeSteps.add(state.schedule.getSteps());
				lastDominantOpinion = dominantOpinion;
				// only switch if temp max diff is greater
				maxDiff = tempMaxDiff;

				model.log.debug("Opinion Dominance Changed: "
						+ Arrays.toString(opinions));
				model.log.debug("Max Opinion Diff: " + maxDiff);
				model.log
						.debug("Num Opinion Dominance Changes: " + countSwitch);
			}

		}

		/**
		 * Returns the number of times the model opinions were subserted since
		 * the simulation started.
		 * 
		 * @return count
		 */
		public int getOpinionSwitchCount() {
			return countSwitch;
		}

		/**
		 * Returns the difference of propotions between two opinion values
		 * 
		 * @return
		 */
		public double getMaxOpinionDiffProp() {
			return maxDiff;
		}

		/**
		 * Returns the current dominant opinion
		 * 
		 * @return
		 */
		public int getDominantOpinion() {
			return dominantOpinion;
		}

		// public List<Long> getOpinionChangeSteps() {
		// return this.changeSteps;
		// }
	}

    public Configuration getConfiguration() {

        if (this.config == null) {
            this.config = defaultConfiguration();
        }
        return this.config;
    }

	public Network[] getNetworks() {
		return networks;
	}

	public Agent[] getAgents() {
		return agents;
	}
}
