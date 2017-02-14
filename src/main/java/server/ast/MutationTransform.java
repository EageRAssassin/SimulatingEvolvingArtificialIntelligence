package server.ast;

import java.util.Random;

public class MutationTransform implements Mutation
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
            mutated = rules.nodeAt(index).transform();
            index = rand.nextInt(rules.size() - 2) + 1;
        }
    }

    @Override
    public boolean equals(Mutation m)
    {
        return (m instanceof MutationTransform);
    }

    public String toString()
    {
        return "Transform Mutation (No.4)";
    }
}
