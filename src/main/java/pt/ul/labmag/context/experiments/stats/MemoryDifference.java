package pt.ul.labmag.context.experiments.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.bhave.experiment.Model;
import org.bhave.experiment.data.AbstractStatistics;
import org.bhave.experiment.data.Statistics;
import pt.ul.labmag.context.experiments.ContextModel;
import pt.ul.labmag.context.model.Agent;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Measures the average difference between number of opinions observed
 */
public class MemoryDifference extends AbstractStatistics {

    private static final String P_AVG_OP_DIFF = "avg-op-diff";
    private static final String P_STDEV_OP_DIFF = "stdev-op-diff";
    private static final String P_VARIANCE_OP_DIFF = "variance-op-diff";

    @Override
    public Properties measure(Model model) {
        ContextModel m = (ContextModel) model;
        Properties opinions = new Properties();


        DescriptiveStatistics stats = new DescriptiveStatistics();

        Agent[] agents = m.getAgents();
        for(Agent agent : agents){
            int[] memory = agent.getOpinionMemory();
            stats.addValue(FastMath.abs(memory[0]-memory[1]));
        }


        opinions.setProperty(P_AVG_OP_DIFF, Double.toString(stats.getMean()));
        opinions.setProperty(P_STDEV_OP_DIFF, Double.toString(stats.getStandardDeviation()));
        opinions.setProperty(P_VARIANCE_OP_DIFF, Double.toString(stats.getVariance()));


        return opinions;
    }

    @Override
    public String getName() {
        return "Opinion Count";
    }

    @Override
    public Class<? extends Model> getTargetModelClass() {
        return ContextModel.class;
    }



    @Override
    public List<String> getColumnNames() {
        List<String> cols = new LinkedList<String>();
        cols.add(P_AVG_OP_DIFF);
        cols.add(P_STDEV_OP_DIFF);
        cols.add(P_VARIANCE_OP_DIFF);
        return cols;
    }

    @Override
    protected Statistics createPrototype() {
        return new MemoryDifference();
    }
}
