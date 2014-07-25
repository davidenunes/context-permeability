package pt.ul.labmag.context.model;

public class ContextSegregationSelective extends ContextSegregation {
	private static final long serialVersionUID = 1L;

	// Constructors
	public ContextSegregationSelective() {
		super();
	}

	public ContextSegregationSelective(long seed) {
		super(seed);
	}

	/********************************************************************
	 * MODEL EXTENSION: we extend the previous model by providing different
	 * agents (with additional behaviour)
	 ********************************************************************/
	// overrides the agent creation to create context segregation agents
	protected CTSAgent createAgent(int id) {
		return new CTSAgent(id, this);
	}

}
