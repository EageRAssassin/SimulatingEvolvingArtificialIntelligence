package server.ast;

/**
 * A mutation to the AST
 */
public interface Mutation
{
    /**
     * Perform the designated mutation
     * 
     * @param index
     *            The index of node to mutate(The index should be smaller than
     *            the rule size)
     */
    void mutate(int index);

    /**
     * Compares the type of this mutation to {@code m}
     * 
     * @param m
     *            The mutation to compare with
     * @return Whether this mutation is the same type as {@code m}
     */
    boolean equals(Mutation m);

    /**
     * add Rules object to mutation
     * 
     * @param rule
     *            The Rules to give to method
     */
    void addRules(Rules rule);
}
