package pt.ul.labmag.context.model;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bhave.network.api.Network;
import org.bhave.network.api.Node;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * An agent that plays the majority game
 * 
 * @author Davide Nunes
 */
public class Agent implements Steppable {
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agent other = (Agent) obj;
		if (id != other.id)
			return false;
		return true;
	}

	private int id;
	Logger log;

	int[] memory;
	int currentOpinion;

	public Agent(int id, ContextPermability env) {
		this.id = id;
		memory = new int[2];
		log = Logger.getLogger(Agent.class);
		log.setLevel(Level.INFO);
	}

	public void setOpinion(int value) {
		this.currentOpinion = value;
	}

	public int[] getOpinionMemory() {
		return new int[] { memory[0], memory[1] };
	}

	public int getOpinion() {
		return currentOpinion;
	}

	@Override
	public void step(SimState ss) {

		// access to the agent environment
		ContextPermability env = (ContextPermability) ss;

		Agent partner = getRandomNeighbour(env);

		if (partner != null) {
			env.recordEncounter();
			int partnerO = partner.getOpinion();
			memory[partnerO]++;

			int otherO = currentOpinion == 0 ? 1 : 0;

			// switch opinion value if agent observes more opinions of the
			// contrary
			// value
			if (memory[otherO] > memory[currentOpinion])
				currentOpinion = otherO;
		}

	}

	private Agent getRandomNeighbour(ContextPermability env) {
		int rndNetwork = env.random.nextInt(env.config
				.getInt(ContextPermability.P_NUM_NETWORKS));
		Network network = env.networks[rndNetwork];

		log.debug("network link count: " + (network.getLinkCount()));
		log.debug("network node count: " + (network.getNodeCount()));

		Node currentNode = network.getNode(this.id);

		Collection<? extends Node> neighbours = network
				.getNeighbours(currentNode);

		log.debug("Number of neighbours found " + neighbours.size());

		ArrayList<Node> neighboursArray = new ArrayList<>(neighbours);

		Agent neighbourA = null;
		if (neighbours.size() > 0) {
			int randNeigbour = env.random.nextInt(neighbours.size());
			Node neighbour = neighboursArray.get(randNeigbour);

			log.debug("Node " + id + " selected Neighbour Node "
					+ neighbour.getID());

			neighbourA = env.agents[neighbour.getID()];

			log.debug("Agent " + id + " selected Neighbour "
					+ neighbourA.getID());
		} else {
			log.debug("No Neighbours available");
		}
		return neighbourA;

	}

	public int getID() {
		return id;
	}

}
