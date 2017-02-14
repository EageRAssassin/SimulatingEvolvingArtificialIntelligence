package server.ast;

import java.util.Random;

public class MutationRemove implements Mutation
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
        if (rules.size() >= 14) // To see if the rule is too short to remove
                                // anything
            while (!mutated)
            {
                mutated = rules.nodeAt(index).remove();
                if (!mutated)
                    index = rand.nextInt(rules.size());
            }
    }

    @Override
    public boolean equals(Mutation m)
    {
        return (m instanceof MutationRemove);
    }

    public String toString()
    {
        return "Remove Mutation (No.1)";
    }
}
