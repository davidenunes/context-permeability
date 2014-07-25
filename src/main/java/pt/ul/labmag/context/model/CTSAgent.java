package pt.ul.labmag.context.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The different between CTSAgent and CTAgent is in the switching mechanism.
 * While Context tolerance agents switch only based on tolerance set for the
 * multiple networks. CTSAgent agents switch based on previous history.
 * 
 * They compute the number of times they switched from a particular context
 * based on segregation and choose the next context based a probability
 * inversely proportional to that value.
 * 
 * In this model, the network to which the agent switches is no longer random.
 * 
 * @author Davide Nunes
 * 
 */
public class CTSAgent extends CTAgent {
	private static final long serialVersionUID = 1L;

	int[][] switchingCount;
	private static final int totalSIndex = 0;
	private static final int segregationSIndex = 1;

	public CTSAgent(int id, ContextSegregationSelective env) {
		super(id, env);
		int numNetworks = env.config
				.getInt(ContextSegregationSelective.P_NUM_NETWORKS);
		// counts the number of total switchings and the number of switchings by
		// segregation
		// switchingCount[0][0] - total switchings for network 0
		// switchingCount[1][0] - total switchings for network 1
		// switchingCount[0][1] - switching by segregation network 0
		// etc.
		switchingCount = new int[numNetworks][2];
	}

	// the extra behaviour is just the added counter
	protected boolean segregationBehaviour(ContextSegregationSelective env) {
		boolean switched = false;
		// 1 get opinion ratios in the current context
		List<Agent> activeNeighbours = getActiveNeighbours(env);

		int countOpposites = 0;
		for (Agent neighbour : activeNeighbours) {
			if (neighbour.getOpinion() != this.getOpinion()) {
				countOpposites++;
			}
		}

		// opposite ratio
		double oppRatio = countOpposites / (activeNeighbours.size() * 1.0);

		// get tolerance for current network
		double tolerance = env.getContextTolerance(this.getCurrentNetwork());

		if (oppRatio > tolerance) {
			switched = switchContextForSeg(this, env);
		}
		if (switched) {
			int currentNetwork = getCurrentNetwork();
			switchingCount[currentNetwork][segregationSIndex]++;
			switchingCount[currentNetwork][totalSIndex]++;
		}
		return switched;
	}

	// added counter of total switching to behaviour
	protected void contextSwitching(CSAgent agent, ContextSwitching model) {
		int previousNetwork = getCurrentNetwork();
		super.contextSwitching(agent, model);
		int newNetwork = getCurrentNetwork();
		boolean switched = previousNetwork != newNetwork;
		if (switched) {
			switchingCount[previousNetwork][totalSIndex]++;
		}
	}

	// switching is not longer based on a roullete
	protected boolean switchContextForSeg(CSAgent agent, ContextSwitching model) {
		int numNetworks = model.config
				.getInt(ContextPermeability.P_NUM_NETWORKS);

		boolean switched = false;
		if (numNetworks > 1) {
			ArrayList<Integer> contexts = new ArrayList<>();
			for (int i = 0; i < numNetworks; i++) {
				if (i != agent.getCurrentNetwork()) {
					contexts.add(i);
				}
			}

			double sum = 0;
			for (int i = 0; i < contexts.size(); i++) {
				int currentNetwork = contexts.get(i);
				sum += (1 - (switchingCount[currentNetwork][segregationSIndex] / switchingCount[currentNetwork][totalSIndex]));
			}

			if (sum > 0) {
				double r = model.random.nextDouble();
				double cum = 0.0;
				int i = 0;
				while (i < contexts.size() && !switched) {
					double previousCum = cum;
					int currentNetwork = contexts.get(i);

					cum = (i == (contexts.size() - 1) ? 1
							: cum
									+ (1 - (switchingCount[currentNetwork][segregationSIndex] / switchingCount[currentNetwork][totalSIndex]))
									/ sum);

					if (r >= previousCum && r < cum) {
						switched = true;
						this.setCurrentNetwork(contexts.get(i));
					}
					i++;
				}
			}
		}
		return switched;

	}
}
