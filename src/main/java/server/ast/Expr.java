package server.ast;

/**
 * A critter program expression that has an integer value.
 */
public interface Expr extends Node
{
    /**
     * Clone the Expression
     * 
     * @return the same new Expression which is cloned
     */
    Expr clone();
}
