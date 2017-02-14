package server.ast;

import java.util.Random;

public class MutationDuplicate implements Mutation
{
    Rules rules;

    public void addRules(Rules rules)
    {
        this.rules = rules;
    }

    @Override
    public void mutate(int index)
    {
        Random rand = new Random();
        boolean mutated = false;
        while (!mutated)
        {
            mutated = rules.nodeAt(index).duplicate();
            index = rand.nextInt(rules.size());
        }
    }

    @Override
    public boolean equals(Mutation m)
    {
        return (m instanceof MutationDuplicate);
    }

    public String toString()
    {
        return "Duplicate Mutation (No.6)";
    }

}
