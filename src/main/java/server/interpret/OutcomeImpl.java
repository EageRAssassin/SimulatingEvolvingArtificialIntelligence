package server.interpret;
import server.ast.Rule;

public class OutcomeImpl implements Outcome
{
    boolean ruleExecuted = false;
    Rule lastRule;

    public OutcomeImpl(boolean ruleExecuted, Rule lastRule)
    {
        this.ruleExecuted = ruleExecuted;
        this.lastRule = lastRule;
    }

    @Override
    public boolean executed()
    {
        return ruleExecuted;
    }

    @Override
    public Rule lastRule()
    {
        return lastRule;
    }

}
