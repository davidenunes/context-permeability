package pt.ul.labmag.context.model;

import org.apache.commons.configuration.Configuration;

/**
 * The context segregation model adds the "switching by segregation" mechanic to
 * the context switching model.
 * 
 * <p>
 * We add an additional parameter called context tolerance ratio which
 * determines the ratio of opposing opinion values tolerated for a particular
 * network.
 * </p>
 * 
 * <p>
 * Much like what happens in the context switching model, tolerance is
 * homogenous for all the agents in a network. It is asociated with networks and
 * not with agents themselves.
 * </p>
 * 
 * <p>
 * To implement the switching by tolerance behaviour, we added a new type of
 * agents called context tolerance agents {@link CTAgent}. The behaviour is the
 * same as the switching aget but, at the end of an interaction, if the ratio of
 * opposing opinions in the current network is bigger than the tolerance
 * threshold for that network, the agent switches imidiately, if not, the agent
 * uses the context switching mechanic.
 * </p>
 * 
 * @see CTAgent
 * 
 * @author Davide Nunes
 * 
 */
public class ContextSegregation extends ContextSwitching {
	private static final long serialVersionUID = 1L;

	// ADDITIONAL PARAMETERS
	// context tolerance ratio
	public static final String P_NETWORK_CT = "ct";

	// default == 1, same as having the typical context switching model
	public static final double P_NETWORK_CT_DEFAULT = 1.0;

	// Constructors
	public ContextSegregation() {
		super();
	}

	public ContextSegregation(long seed) {
		super(seed);
	}

	/********************************************************************
	 * MODEL EXTENSION: we extend the previous model by providing different
	 * agents (with additional behaviour)
	 ********************************************************************/
	// overrides the agent creation to create context segregation agents
	protected CTAgent createAgent(int id) {
		return new CTAgent(id, this);
	}

	/**
	 * Add default configuration for context tolerance
	 * 
	 * Note, if no individual configuration is available for each network, we
	 * take the value from the parameter network.0.cs as the default for all the
	 * networks
	 */
	protected Configuration defaultConfiguration() {
		Configuration defacultCFG = super.defaultConfiguration();

		String csBaseConfig = getCTPKey(0);

		// default context tolerance is 1.0 (tolerate any proportion of opposite
		// neighbours)
		defacultCFG.setProperty(csBaseConfig, P_NETWORK_CT_DEFAULT);

		return defacultCFG;
	}

	/**
	 * Returns the property key for context tolerance
	 * 
	 * @param networkIndex
	 *            the network index in this model
	 * 
	 * @return a string to be used to access this.config and retrieve the proper
	 *         context tolerance ratio.
	 */
	public String getCTPKey(int networkIndex) {
		return P_NETWORK_BASE + "." + networkIndex + "." + P_NETWORK_CT;
	}

	/**
	 * Returns the context tolerance ratio for the given network
	 * 
	 * @param network
	 *            a network index from (0 to networks.length -1)
	 * 
	 * @return a value for the tolerance ratio between 0 and 1
	 */
	public double getContextTolerance(int networkIndex) {
		// this should not happen as the method should be accessed by agents
		if (this.config == null)
			loadConfiguration(defaultConfiguration());

		String pKey = getCTPKey(networkIndex);

		Double p = null;
		if (!config.containsKey(pKey)) {
			pKey = getCTPKey(0);
			if (!config.containsKey(pKey)) {
				throw new RuntimeException(
						"Invalid configuration for context tolerance:"
								+ "you must provide at least one value for the parameter ->"
								+ pKey);
			}
		}

		// if the key is there we can load the tolerance
		p = config.getDouble(pKey);

		return p;
	}

	@Override
	public void loadConfiguration(Configuration cfg) {
		super.loadConfiguration(cfg);

		// verify the default value for context tolerance
		String pKey = getCTPKey(0);
		Double ct = null;

		if (!config.containsKey(pKey)) {
			throw new RuntimeException(
					"Invalid configuration for context tolerance:"
							+ "you must provide at least one value for the parameter ->"
							+ pKey);
		}

		// verify that all the others are correct if not null
		int numNetworks = config.getInt(P_NUM_NETWORKS);

		for (int i = 0; i < numNetworks; i++) {
			ct = null;
			pKey = getCTPKey(i);
			if (config.containsKey(pKey)) {
				ct = config.getDouble(pKey);
				if (ct < 0 || ct > 1.0) {
					throw new RuntimeException(
							"Invalid Context Tolerance Parameter: " + pKey
									+ " must have a value between 0 and 1");
				}
			}
		}
	}

}
