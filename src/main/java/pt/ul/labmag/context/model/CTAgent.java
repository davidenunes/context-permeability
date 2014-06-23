package pt.ul.labmag.context.model;

import java.util.List;

import sim.engine.SimState;

/**
 * Agents for context segregation model. These switch based on a context
 * tolerance parameter set for each network
 * 
 * @author Davide Nunes
 * 
 */
public class CTAgent extends CSAgent {
	private static final long serialVersionUID = 1L;

	// constructor
	public CTAgent(int id, ContextSwitching env) {
		super(id, env);
	}

	public void step(SimState state) {
		// access the agent environment
		ContextSegregation env = (ContextSegregation) state;

		// get a random neighbour (active in the same network)
		Agent partner = getRandomNeighbour(env);

		updateOpinions(partner, env);

		boolean switched = segregationBehaviour(env);

		// else, the agent already switched
		if (!switched) {
			// switching by probability
			switchingBehaviour(env);
		}
	}

	/**
	 * Implements the segregation behviour basically each agent looks at its
	 * current neighbourhood and determines the ratio of neighbours with
	 * opposing opinion values, if this value surpases the context tolerance
	 * threshold set for its current network it switches imidiately
	 * 
	 * @param env
	 *            the environmen model
	 * 
	 * @return true if agent switched as a result from the segregation
	 *         behaviour, false otherwise
	 */
	protected boolean segregationBehaviour(ContextSegregation env) {
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
		double tolerance = env.getContextTolerance(getCurrentNetwork());

		if (oppRatio > tolerance) {
			super.switchContext(this, env);
			return true;
		}

		return false;
	}
}
