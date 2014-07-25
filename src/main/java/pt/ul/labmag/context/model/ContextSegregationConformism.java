package pt.ul.labmag.context.model;

/**
 * Created by Davide Nunes on 21-07-2014.
 */
public class ContextSegregationConformism extends ContextSegregation {

    // Constructors
    public ContextSegregationConformism() {
        super();
    }

    public ContextSegregationConformism(long seed) {
        super(seed);
    }


    @Override
    protected CTCAgent createAgent(int id) {
        return new CTCAgent(id, this);
    }
}
