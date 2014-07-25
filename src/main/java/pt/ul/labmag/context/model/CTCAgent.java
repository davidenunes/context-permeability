package pt.ul.labmag.context.model;

import java.util.List;

/**
 * <h2>Agent that segregates based on conformism</h2>
 * 
 * Agent that segregates avoiding conformism
 * the difference is in the way the agent segregates
 * if the agent encounters a context in which the ratio of
 * neighbours with the same opinion is about its conformism tolerance ratio,
 * it switches contexts.
 * <p/>
 * Created by Davide Nunes
 */
public class CTCAgent extends CTAgent {

    public CTCAgent(int id, ContextSwitching env) {
        super(id, env);
    }

    @Override
    protected boolean segregationBehaviour(ContextSegregation env) {
        // 1 get opinion ratios in the current context
        List<Agent> activeNeighbours = getActiveNeighbours(env);

        int countSameOpinion = 0;
        for (Agent neighbour : activeNeighbours) {
            if (neighbour.getOpinion() == this.getOpinion()) {
                countSameOpinion++;
            }
        }

        // same ratio
        double sameRatio = countSameOpinion / (activeNeighbours.size() * 1.0);

        // get tolerance for current network
        double tolerance = env.getContextTolerance(this.getCurrentNetwork());

        if (sameRatio > tolerance) {
            switchContext(this, env);
            return true;
        }

        return false;
    }
}
