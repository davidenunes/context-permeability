package pt.ul.labmag.context;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.math3.util.FastMath;
import org.bhave.network.model.KRegularModel;
import org.junit.Test;

import pt.ul.labmag.context.experiments.CPModel;
import pt.ul.labmag.context.model.Agent;
import pt.ul.labmag.context.model.ContextPermability;

public class ModelTest extends TestCase {

	@Test
	public void testOpinionDistribution() {
		System.out.println("Test opinion distribution...");
		ContextPermability model = new ContextPermability();
		model.start();

		int opinion1 = 0;
		int opinion0 = 0;

		for (Agent agent : model.agents) {

			if (agent.getOpinion() == 0)
				opinion0++;
			else
				opinion1++;
		}

		int opinionDiff = FastMath.abs(opinion0 - opinion1);

		assertTrue(opinionDiff <= 1 && opinionDiff >= 0);

	}

	@Test
	public void testConvergence() {
		System.out.println("Test convergence...");
		CPModel model = new CPModel();
		Configuration cfg = model.getConfiguration();
		cfg.setProperty(CPModel.P_MAX_STEPS, 3000);
		cfg.setProperty(CPModel.P_NUM_AGENTS, 300);
		cfg.setProperty(CPModel.P_NUM_NETWORKS, 3);

		String baseNetConfigP = CPModel.P_NETWORK_BASE + ".0";
		cfg.setProperty(baseNetConfigP, "org.bhave.network.model.KRegularModel");
		cfg.setProperty(baseNetConfigP + "." + KRegularModel.P_K, 60);
		cfg.setProperty(baseNetConfigP + "." + KRegularModel.P_NUM_NODES, 300);

		model.loadConfiguration(cfg);

		model.run();

		System.out.println(Arrays.toString(model.opinionCount()));

	}
}
