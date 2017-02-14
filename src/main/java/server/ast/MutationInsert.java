package server.ast;

import java.util.Random;

import server.parse.Token;
import server.parse.TokenType;

public class MutationInsert implements Mutation
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
        while (mutated != true)
        {
            index = rand.nextInt(rules.size());
            if (rules.nodeAt(index) instanceof Term)
            {
                int lineNo = ((Term) rules.nodeAt(index)).lineNo;
                if (rand.nextBoolean())
                {// Add Parent to left
                    int insertType = rand.nextInt(4);
                    switch (insertType)
                    {
                    case 0:// Add Memory Parent
                        ((Term) rules.nodeAt(index)).l = new Factor(false,
                                new Memory(((Term) rules.nodeAt(index)).l),
                                false);
                        break;
                    case 1:// Add NEARBY Parent
                        ((Term) rules.nodeAt(index)).l = new Factor(false,
                                new Sensor(new Token(TokenType.NEARBY, lineNo),
                                        ((Term) rules.nodeAt(index)).l), false);
                        break;
                    case 2:// Add AHEAD Parent
                        ((Term) rules.nodeAt(index)).l = new Factor(false,
                                new Sensor(new Token(TokenType.AHEAD, lineNo),
                                        ((Term) rules.nodeAt(index)).l), false);
                        break;
                    case 3:// Add RANDOM Parent
                        ((Term) rules.nodeAt(index)).l = new Factor(false,
                                new Sensor(new Token(TokenType.RANDOM, lineNo),
                                        ((Term) rules.nodeAt(index)).l), false);
                        break;
                    }
                    mutated = true;
                } else if (((Term) rules.nodeAt(index)).r != null)
                {// Add Parent to right
                    int insertType = rand.nextInt(4);
                    switch (insertType)
                    {
                    case 0:// Add Memory Parent
                        ((Term) rules.nodeAt(index)).r = new Factor(false,
                                new Memory(((Term) rules.nodeAt(index)).l),
                                false);
                        break;
                    case 1:// Add NEARBY Parent
                        ((Term) rules.nodeAt(index)).r = new Factor(false,
                                new Sensor(new Token(TokenType.NEARBY, lineNo),
                                        ((Term) rules.nodeAt(index)).l), false);
                        break;
                    case 2:// Add AHEAD Parent
                        ((Term) rules.nodeAt(index)).r = new Factor(false,
                                new Sensor(new Token(TokenType.AHEAD, lineNo),
                                        ((Term) rules.nodeAt(index)).l), false);
                        break;
                    case 3:// Add RANDOM Parent
                        ((Term) rules.nodeAt(index)).r = new Factor(false,
                                new Sensor(new Token(TokenType.RANDOM, lineNo),
                                        ((Term) rules.nodeAt(index)).l), false);
                        break;
                    }
                    mutated = true;
                }
            }
        }
    }

    @Override
    public boolean equals(Mutation m)
    {
        return (m instanceof MutationInsert);
    }

    public String toString()
    {
        return "Insert Mutation (No.5)";
    }

}
