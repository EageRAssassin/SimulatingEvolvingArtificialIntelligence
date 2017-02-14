package server.ast;

import java.util.Random;

public class MutationSwap implements Mutation
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
            mutated = rules.nodeAt(index).swap();
            index = rand.nextInt(rules.size());
        }
    }

    @Override
    public boolean equals(Mutation m)
    {
        return (m instanceof MutationSwap);
    }

    public String toString()
    {
        return "Swap Mutation (No.2)";
    }
}
