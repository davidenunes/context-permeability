package pt.ul.labmag.context.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A model of context permeability in multiple social networks with associated
 * switching probabilities for each network.
 * 
 * 
 * At the beginning of the simulation, the agents are equally distributed
 * throughout all the networks
 * 
 * 
 * @author Davide Nunes
 */
public class ContextSwitching extends ContextPermability {
	private static final long serialVersionUID = 1L;

	// ADDITIONAL PARAMETERS
	// switching probability
	public static final String P_NETWORK_CS = "cs";
	public static final double P_NETWORK_CS_DEFAULT = 1.0;

	// constructor without seed (generated from system time)
	public ContextSwitching() {
		super();
	}

	// constructor with seed
	public ContextSwitching(long seed) {
		super(seed);
	}

	public void start() {
		super.start();

		// distribute agents randomly and evenly by networks
		distributeAgents();
	}

	/**
	 * Add default configuration for context switching
	 * 
	 * Note, if no individual configuration is available for each network, we
	 * take the value from the parameter network.0.cs as the default for all the
	 * networks
	 */
	protected Configuration defaultConfiguration() {
		Configuration defacultCFG = super.defaultConfiguration();

		String csBaseConfig = getCSPKey(0);

		// default context switching is 1.0 (allways switches)
		defacultCFG.setProperty(csBaseConfig, P_NETWORK_CS_DEFAULT);

		return defacultCFG;
	}

	/**
	 * Returns the property key for switching probability
	 * 
	 * @param networkIndex
	 *            the network index in this model
	 * 
	 * @return a string to be used to access this.config and retrieve the proper
	 *         context switching probability.
	 */
	private String getCSPKey(int networkIndex) {
		return P_NETWORK_BASE + "." + networkIndex + "." + P_NETWORK_CS;
	}

	/**
	 * Returns the switching probability for the given network
	 * 
	 * @param network
	 *            a network index from (0 to networks.length -1)
	 * 
	 * @return a value for the switching probability between 0 and 1
	 */
	public double getSwitchingProbability(int networkIndex) {
		// this should not happen as the method should be accessed by agents
		if (this.config == null)
			loadConfiguration(defaultConfiguration());

		String pKey = getCSPKey(networkIndex);
		Double p = config.getDouble(pKey);
		if (p == null) {
			if (networkIndex > 0) {
				pKey = getCSPKey(0);
				p = config.getDouble(pKey);
			}
		}
		if (p == null) {
			throw new RuntimeException(
					"Invalid configuration for switching probability:"
							+ "you must provide at least one value for the parameter ->"
							+ pKey);
		}

		return 0;
	}

	@Override
	public void loadConfiguration(Configuration cfg) {
		super.loadConfiguration(cfg);

		// verify the default value for switching probability
		String pKey = getCSPKey(0);
		Double cs = config.getDouble(pKey);
		if (cs == null) {
			throw new RuntimeException(
					"Invalid configuration for switching probability:"
							+ "you must provide at least one value for the parameter ->"
							+ pKey);
		}

		// verify that all the others are correct if not null
		int numNetworks = config.getInt(P_NUM_NETWORKS);
		for (int i = 0; i < numNetworks; i++) {
			pKey = getCSPKey(i);
			cs = config.getDouble(pKey);
			if (cs != null) {
				if (cs < 0 || cs > 1.0) {
					throw new RuntimeException(
							"Invalid Switching Probability Parameter: " + pKey
									+ " must have a value between 0 and 1");
				}
			}
		}
	}

	/**
	 * Distibutes the agents evenly by all the network in a random fashion
	 */
	private void distributeAgents() {
		// equal number of agents by context
		ArrayList<Agent> allAgents = new ArrayList<Agent>(Arrays.asList(agents));

		int agentsPerContext = agents.length / networks.length;

		for (int c = 1; c <= networks.length; c++) {// divide the agents bu
			// numContexts
			int added = 0;
			int toBeAdded = 0;

			if (c == networks.length)// for the last context add the remainder
										// of
										// the agents (if population %
										// numNetwork !=
										// 0)
			{
				toBeAdded = allAgents.size();
			} else {
				toBeAdded = agentsPerContext;
			}

			while (added < toBeAdded && !allAgents.isEmpty()) {

				int nextID = 0;
				if (allAgents.size() > 1) {
					nextID = random.nextInt(allAgents.size());
				}

				CSAgent agent = (CSAgent) allAgents.get(nextID);
				agent.setCurrentNetwork(c - 1);// put agent in context i

				allAgents.remove(agent);
				added++;
			}

		}

	}
}
