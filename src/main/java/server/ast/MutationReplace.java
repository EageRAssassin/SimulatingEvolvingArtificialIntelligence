package server.ast;

import java.util.Random;

import server.parse.Token;
import server.parse.Token.NumToken;
import server.parse.TokenType;

public class MutationReplace implements Mutation
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

            // Replace the Condition Node
            if (rules.nodeAt(index) instanceof BinaryCondition)
            {
                int lineNo = ((BinaryCondition) rules.nodeAt(index)).lineNo;
                while (mutated != true)
                {
                    if (rand.nextBoolean())
                    {
                        // replace the node
                        for (int times = 0; (times < rules.size() / 2)
                                && (mutated == false); times++)
                        {
                            int indexToBeReplaced = rand.nextInt(rules.size());
                            if (rules.nodeAt(indexToBeReplaced) instanceof Condition)
                            {
                                ((BinaryCondition) rules.nodeAt(index)).l = ((Condition) rules
                                        .nodeAt(indexToBeReplaced)).clone();
                                mutated = true;
                            }
                        }
                    } else
                    {
                        // generate a random Condition Node
                        if (rand.nextBoolean())
                        {
                            ((BinaryCondition) rules.nodeAt(index)).l = new Relation(
                                    new Token(TokenType.PLUS, lineNo),
                                    expressionGenerator(lineNo),
                                    expressionGenerator(lineNo), lineNo);
                            ((BinaryCondition) rules.nodeAt(index)).l
                                    .transform();
                        } else
                        {
                            ((BinaryCondition) rules.nodeAt(index)).l = new Relation(
                                    new Token(TokenType.PLUS, lineNo),
                                    expressionGenerator(lineNo),
                                    expressionGenerator(lineNo), lineNo);
                            ((BinaryCondition) rules.nodeAt(index)).l
                                    .transform();
                        }
                        mutated = true;
                    }
                }
            }

            // Replace the Expression Node
            if (rules.nodeAt(index) instanceof Relation)
            {
                int lineNo = ((Relation) rules.nodeAt(index)).lineNo;
                while (mutated != true)
                {
                    if (rand.nextBoolean())
                    {
                        // replace the node
                        for (int times = 0; (times < rules.size() / 2)
                                && (mutated == false); times++)
                        {
                            int indexToBeReplaced = rand.nextInt(rules.size());
                            if (rules.nodeAt(indexToBeReplaced) instanceof Expr)
                            {
                                if (rand.nextBoolean())
                                    ((Relation) rules.nodeAt(index)).left = ((Expr) rules
                                            .nodeAt(indexToBeReplaced)).clone();
                                else
                                    ((Relation) rules.nodeAt(index)).right = ((Expr) rules
                                            .nodeAt(indexToBeReplaced)).clone();
                                mutated = true;
                            }
                        }
                    } else
                    {
                        // generate a random Expression Node
                        if (rand.nextBoolean())
                            ((Relation) rules.nodeAt(index)).left = expressionGenerator(lineNo);
                        else
                            ((Relation) rules.nodeAt(index)).right = expressionGenerator(lineNo);
                        mutated = true;
                    }
                }
            }

            // Replace the Update Node
            if (rules.nodeAt(index) instanceof Command)
            {
                int lineNo = ((Command) rules.nodeAt(index)).lineNo;
                if (((Command) rules.nodeAt(index)).updates.size() > 0)
                {
                    while (mutated != true)
                    {
                        if (rand.nextBoolean())
                        {
                            // replace the node
                            if (((Command) rules.nodeAt(index)).updates.size() > 1)
                            {
                                ((Command) rules.nodeAt(index)).updates
                                        .add(((Command) rules.nodeAt(index)).updates.get(rand
                                                .nextInt(((Command) rules
                                                        .nodeAt(index)).updates
                                                        .size())));
                                ((Command) rules.nodeAt(index)).updates
                                        .remove(((Command) rules.nodeAt(index)).updates.get(rand
                                                .nextInt(((Command) rules
                                                        .nodeAt(index)).updates
                                                        .size())));
                                mutated = true;
                            }
                        } else
                        {
                            // generate a random Update Node
                            if (((Command) rules.nodeAt(index)).updates.size() > 1)
                            {
                                ((Command) rules.nodeAt(index)).updates
                                        .remove(rand.nextInt(((Command) rules
                                                .nodeAt(index)).updates.size() - 1));
                                ((Command) rules.nodeAt(index)).updates
                                        .add(new Update(
                                                expressionGenerator(lineNo),
                                                expressionGenerator(lineNo),
                                                false));
                                mutated = true;
                            } else
                            {
                                ((Command) rules.nodeAt(index)).updates
                                        .remove(0);
                                ((Command) rules.nodeAt(index)).updates
                                        .add(new Update(
                                                expressionGenerator(lineNo),
                                                expressionGenerator(lineNo),
                                                false));
                                mutated = true;
                            }
                        }
                    }
                }
            }

            // Replace the Action Node
            if (rules.nodeAt(index) instanceof Command)
            {
                // generate a random Action Node
                int generateType = rand.nextInt(12);
                int lineNo = ((Command) rules.nodeAt(index)).lineNo;
                switch (generateType)
                {
                case 0:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.WAIT, lineNo), lineNo);
                    break;
                case 1:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.FORWARD, lineNo), lineNo);
                    break;
                case 2:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.BACKWARD, lineNo), lineNo);
                    break;
                case 3:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.LEFT, lineNo), lineNo);
                    break;
                case 4:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.RIGHT, lineNo), lineNo);
                    break;
                case 5:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.EAT, lineNo), lineNo);
                    break;
                case 6:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.ATTACK, lineNo), lineNo);
                    break;
                case 7:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.GROW, lineNo), lineNo);
                    break;
                case 8:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.BUD, lineNo), lineNo);
                    break;
                case 9:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.MATE, lineNo), lineNo);
                    break;
                case 10:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.TAG, lineNo), lineNo);
                    ((Command) rules.nodeAt(index)).action
                            .setExpr(expressionGenerator(lineNo));
                    break;
                case 11:
                    ((Command) rules.nodeAt(index)).action = new Action(
                            new Token(TokenType.SERVE, lineNo), lineNo);
                    ((Command) rules.nodeAt(index)).action
                            .setExpr(expressionGenerator(lineNo));
                    break;
                }
                mutated = true;
            }
        }
    }

    @Override
    public boolean equals(Mutation m)
    {
        return (m instanceof MutationReplace);
    }

    /**
     * Generate a new Expression Node, with equal probability of mem, sensor and
     * number
     * 
     * @return a randomly new Expression Node
     */
    public Expr expressionGenerator(int lineNo)
    {
        Random rand = new Random();
        int generateType = rand.nextInt(3);
        switch (generateType)
        {
        case 0:
            return new Memory(new Number(new NumToken(
                    java.lang.Integer.MAX_VALUE / rand.nextInt(), lineNo)));

        case 1:
            int sensorType = rand.nextInt(4);
            switch (sensorType)
            {
            case 0:
                return new Sensor(new Token(TokenType.NEARBY, lineNo),
                        new Number(new NumToken(java.lang.Integer.MAX_VALUE
                                / rand.nextInt(), lineNo)));
            case 1:
                return new Sensor(new Token(TokenType.AHEAD, lineNo),
                        new Number(new NumToken(java.lang.Integer.MAX_VALUE
                                / rand.nextInt(), lineNo)));
            case 2:
                return new Sensor(new Token(TokenType.RANDOM, lineNo),
                        new Number(new NumToken(java.lang.Integer.MAX_VALUE
                                / rand.nextInt(), lineNo)));
            case 3:
                return new Sensor(new Token(TokenType.SMELL, lineNo), null);
            }
            break;

        case 2:
            return new Number(new NumToken(java.lang.Integer.MAX_VALUE
                    / rand.nextInt(), lineNo));
        }
        return null;
    }

    public String toString()
    {
        return "Replace Mutation (No.4)";
    }
}
