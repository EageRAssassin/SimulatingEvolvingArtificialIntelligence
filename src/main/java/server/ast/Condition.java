package server.ast;

/**
 * An interface representing a Boolean condition in a critter program.
 *
 */
public interface Condition extends Node
{
    /**
     * Clone the Node
     * 
     * @return the same new node which is cloned
     */
    Condition clone();
}
