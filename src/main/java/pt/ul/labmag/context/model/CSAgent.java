package pt.ul.labmag.context.model;

import java.util.ArrayList;
import java.util.Collection;

import org.bhave.network.api.Network;
import org.bhave.network.api.Node;

import sim.engine.SimState;

/**
 * An agent that plays the majority game and switches contexts based on the
 * environment switching probabilities for each network.
 * 
 * @author Davide Nunes
 */
public class CSAgent extends Agent {
	private static final long serialVersionUID = 1L;

	// the current network in which the agent is interacting
	private int currentNetwork = 0;

	// constructor
	public CSAgent(int id, ContextSwitching env) {
		super(id, env);
	}

	public int getCurrentNetwork() {
		return currentNetwork;
	}

	public void setCurrentNetwork(int currentNetwork) {
		this.currentNetwork = currentNetwork;
	}

	public void step(SimState state) {
		// access the agent environment
		ContextSwitching env = (ContextSwitching) state;

		// get a random neighbour (active in the same network)
		Agent partner = getRandomNeighbour(env);

		updateOpinions(partner, env);

		switchingBehaviour(env);

	}

	/**
	 * This implements the context switching behaviour, it can be used as a
	 * building block to produce more complex agent behaviours.
	 * 
	 * @param env
	 */
	protected void switchingBehaviour(ContextSwitching env) {
		// attempt to switch context
		int numNetwork = env.config.getInt(ContextPermeability.P_NUM_NETWORKS);
		if (numNetwork > 1) {
			contextSwitching(this, env);
		}

	}

	/*******************************************
	 * CONTEXT SWITCHING
	 *******************************************/

	protected void contextSwitching(CSAgent agent, ContextSwitching model) {
		// get switching probability for current agent
		double switchingP = model.getSwitchingProbability(currentNetwork);

		double r = model.random.nextDouble();
		if (r < switchingP) {
			switchContext(agent, model);
		}

	}

	// roullete switching
	protected void switchContext(CSAgent agent, ContextSwitching model) {
		int numNetworks = model.config
				.getInt(ContextPermeability.P_NUM_NETWORKS);

		if (numNetworks > 1) {
			ArrayList<Integer> contexts = new ArrayList<>();
			for (int i = 0; i < numNetworks; i++) {
				if (i != agent.currentNetwork) {
					contexts.add(i);
				}
			}
			int nextContext = model.random.nextInt(numNetworks - 1);

			agent.currentNetwork = contexts.get(nextContext);
		}
	}

	/**
	 * Returns a random neighbour from the current context
	 * 
	 * @param model
	 *            the simulation model
	 * @return agent a random neighbour or null if none is available
	 */
	protected Agent getRandomNeighbour(ContextSwitching model) {

		ArrayList<Agent> activeNeighbours = getActiveNeighbours(model);

		Agent selected = null;
		if (activeNeighbours.size() > 0) // select a random neighbour
		{
			int r = model.random.nextInt(activeNeighbours.size());
			selected = activeNeighbours.get(r);
		}

		return selected;
	}

	/**
	 * Return a list of active neighbours (neighbours with the same
	 * currentNetwork value)
	 * 
	 * @param model
	 * @return a list of active neighbours
	 */
	protected ArrayList<Agent> getActiveNeighbours(ContextSwitching model) {
		Network network = model.networks[this.currentNetwork];

		Node currentNode = network.getNode(this.id);
		Collection<? extends Node> neighbours = network
				.getNeighbours(currentNode);

		// get neighbours in the same network
		ArrayList<Agent> activeNeighbours = new ArrayList<>();
		for (Node node : neighbours) {
			CSAgent neighbour = (CSAgent) model.agents[node.getID()];
			if (neighbour.currentNetwork == this.currentNetwork) {
				activeNeighbours.add(neighbour);
			}
		}
		return activeNeighbours;
	}

}
