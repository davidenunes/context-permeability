package pt.ul.labmag.context.experiments;

import org.bhave.experiment.Model;
import org.bhave.network.api.Network;

import pt.ul.labmag.context.model.Agent;
import pt.ul.labmag.context.model.ContextPermeability.OpinionMonitor;

/**
 * Tagging interface to be used in the experiment framework it is used to mark
 * all the context models and thus the stats that can be applied to all Context
 * Models
 * 
 * @author Davide
 * 
 */
public interface ContextModel extends Model {
	public int[] opinionCount();

	public OpinionMonitor getOpinionMonitor();

	public int getTotalNumEncounters();

	public Network[] getNetworks();

	public boolean consensusAchieved();

	public Agent[] getAgents();

	public void recordEncounter();
}
