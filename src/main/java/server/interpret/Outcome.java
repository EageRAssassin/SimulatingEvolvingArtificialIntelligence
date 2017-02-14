package server.interpret;

import server.ast.Rule;
/**
 * An example interface for representing an outcome of interpreting a critter
 * program.
 */
public interface Outcome
{

    /**
     * inform the user whether the rules have been executed
     * 
     * @return whether the rules have been executed
     */
    boolean executed();

    /**
     * inform the user whether the last rule executed
     * 
     * @return the last rule executed
     */
    Rule lastRule();
}
