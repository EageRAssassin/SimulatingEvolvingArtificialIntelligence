package server.ast;

import java.util.ArrayList;
import java.util.Random;

/**
 * A representation of a critter rule.
 */
public class Rule implements Node
{
    public Condition condition;
    public Command command;

    @Override
    public int size()
    {
        return condition.size() + command.size() + 1;
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
            queue.add(condition);
            queue.add(command);
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
    public StringBuilder prettyPrint(StringBuilder sb)
    {
        condition.prettyPrint(sb);
        sb.append(" --> ");
        command.prettyPrint(sb);
        sb.append(";");
        return sb;
    }

    /**
     * Add a condition Node to the Command Node
     * 
     * @param conditionNode
     *            The condition Node added to the Rule Node
     */
    public void addCondition(Condition conditionNode)
    {
        condition = conditionNode;
    }

    /**
     * Add a command Node to the Command Node
     * 
     * @param commandNode
     *            The command Node added to the Rule Node
     */
    public void addCommand(Command commandNode)
    {
        command = commandNode;
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
        Random rand = new Random();
        if (rand.nextBoolean())
        {
            return command.remove();
        } else
        {
            if (condition instanceof Relation)
                return false;
            if (condition instanceof BinaryCondition)
            {
                boolean random = rand.nextBoolean();
                if (random)
                {
                    condition = ((BinaryCondition) condition).l;
                    return true;
                } else
                {
                    condition = ((BinaryCondition) condition).r;
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean duplicate()
    {
        return false;
    }

    public Rule clone()
    {
        Rule temp = new Rule();
        temp.addCondition(condition.clone());
        temp.addCommand(command.clone());
        return temp;
    }
}
