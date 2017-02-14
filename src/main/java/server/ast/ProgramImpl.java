package server.ast;

import java.util.ArrayList;
import java.util.Random;

/**
 * A data structure representing a critter program.
 *
 */
public class ProgramImpl implements Program
{
    public Rules root = new Rules();

    @Override
    public int size()
    {
        int size = 0;
        for (Rule rule : root.rules)
        {
            size += rule.size();
        }
        return size;
    }

    /**
     * Add a Rule Node to the ProgramImpl Node
     * 
     * @param rule
     *            The rule Node added to the ProgramImpl Node
     */
    public void addRule(Rule rule)
    {
        root.addRule(rule);
    }

    @Override
    public Node nodeAt(int index)
    {
        if (index == 0)
        {
            return this;
        } else
        {
            int current = 0;
            ArrayList<Node> queue = new ArrayList<Node>();
            queue.add(root);
            while (queue.size() != 0)
            {
                Node temp = queue.get(0);
                current += 1;
                if (current == index)
                {
                    return temp;
                }
                if (temp instanceof Action)
                {
                    if (((Action) temp).child != null)
                        queue.add(((Action) temp).child);
                }
                if (temp instanceof BinaryCondition)
                {
                    if (((BinaryCondition) temp).l != null)
                        queue.add(((BinaryCondition) temp).l);
                    if (((BinaryCondition) temp).r != null)
                        queue.add(((BinaryCondition) temp).r);
                    for (Condition c : ((BinaryCondition) temp).extraCondition)
                    {
                        queue.add(c);
                    }
                }
                if (temp instanceof Command)
                {
                    if (((Command) temp).updates.size() != 0)
                    {
                        for (Update update : ((Command) temp).updates)
                        {
                            queue.add(update);
                        }
                    }
                    if (((Command) temp).action != null)
                    {
                        queue.add(((Command) temp).action);
                    }
                }
                if (temp instanceof ExprGeneral)
                {
                    if (((ExprGeneral) temp).l != null)
                        queue.add(((ExprGeneral) temp).l);
                    if (((ExprGeneral) temp).r != null)
                        queue.add(((ExprGeneral) temp).r);
                    for (Term t : ((ExprGeneral) temp).extraTerm)
                    {
                        queue.add(t);
                    }
                }
                if (temp instanceof Factor)
                {
                    if (((Factor) temp).child != null)
                        queue.add(((Factor) temp).child);
                }
                if (temp instanceof Memory)
                {
                    if (((Memory) temp).value != null)
                        queue.add(((Memory) temp).value);
                }
                if (temp instanceof Relation)
                {
                    if (((Relation) temp).left != null)
                        queue.add(((Relation) temp).left);
                    if (((Relation) temp).right != null)
                        queue.add(((Relation) temp).right);
                }
                if (temp instanceof Rule)
                {
                    if (((Rule) temp).condition != null)
                        queue.add(((Rule) temp).condition);
                    if (((Rule) temp).command != null)
                        queue.add(((Rule) temp).command);
                }
                if (temp instanceof Rules)
                {
                    for (Rule rule : ((Rules) temp).rules)
                    {
                        queue.add(rule);
                    }
                }
                if (temp instanceof Sensor)
                {
                    if (((Sensor) temp).child != null)
                        queue.add(((Sensor) temp).child);
                }
                if (temp instanceof Term)
                {
                    if (((Term) temp).l != null)
                        queue.add(((Term) temp).l);
                    if (((Term) temp).r != null)
                        queue.add(((Term) temp).r);
                    for (Factor f : ((Term) temp).extraFactor)
                    {
                        queue.add(f);
                    }
                }
                if (temp instanceof Update)
                {
                    if (((Update) temp).exprGiven != null)
                        queue.add(((Update) temp).exprGiven);
                    if (((Update) temp).exprInMem != null)
                        queue.add(((Update) temp).exprInMem);
                }
                queue.remove(0);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Program mutate()
    {
        Mutation m = MutationFactory.getDuplicate();
        m.addRules(root);
        Random rand = new Random();
        int index = rand.nextInt(root.size());
        m.mutate(index);
        return this;
    }

    @Override
    public Program mutate(int index, Mutation m)
    {
        m.addRules(root);
        m.mutate(index);
        return this;
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb)
    {
        root.prettyPrint(sb);
        return sb;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        prettyPrint(sb);
        return sb.toString();
    }

    @Override
    public boolean swap()
    {
        return false;
    }

    @Override
    public boolean transform()
    {
        return false;
    }

    @Override
    public boolean remove()
    {
        return false;
    }

    @Override
    public boolean duplicate()
    {
        return false;
    }

    @Override
    public ProgramImpl clone()
    {
        ProgramImpl temp = new ProgramImpl();
        for (Rule rule : root.rules)
        {
            temp.addRule(rule.clone());
        }
        return temp;
    }

}
