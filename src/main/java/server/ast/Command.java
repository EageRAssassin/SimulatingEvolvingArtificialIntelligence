package server.ast;

import java.util.ArrayList;
import java.util.Random;

public class Command implements Expr
{
    public ArrayList<Update> updates = new ArrayList<Update>();
    public Action action;
    public int lineNo;

    public Command(int lineNo)
    {
        this.lineNo = lineNo;
    }

    /**
     * Add a Update Node to the Command Node
     * 
     * @param update
     *            The update Node added to the Command Node
     */
    public void addUpdate(Update update)
    {
        updates.add(update);
    }

    /**
     * Set the current Action node to the given one
     * 
     * @param action
     *            The action Node given to the Command Node
     */
    public void setAction(Action action)
    {
        this.action = action;
    }

    @Override
    public int size()
    {
        int size = 0;
        for (Update update : updates)
        {
            size += update.size();
        }
        if (action != null)
            size += action.size();
        return size;
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
            if (updates.size() != 0)
            {
                for (Update update : updates)
                {
                    if (update != null)
                    {
                        queue.add(update);
                    }
                }
            }
            if (action != null)
            {
                queue.add(action);
            }
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
        for (Update update : updates)
        {
            update.prettyPrint(sb);
            if (updates.indexOf(update) < updates.size() - 1)
                sb.append("\n");
        }
        if (action != null)
        {
            if (updates.size() != 0)
                sb.append("\n");
            action.prettyPrint(sb);
        }
        return sb;
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
        if (updates.size() == 0)
            return false;
        if ((action == null) && (updates.size() == 1))
            return false;
        if (action == null)
        {
            updates.remove(rand.nextInt(updates.size()));
            return true;
        }
        int index = rand.nextInt(updates.size()) + 1;
        if (index == updates.size())
        {
            action = null;
            return true;
        }
        updates.remove(index);
        return true;
    }

    @Override
    public boolean duplicate()
    {
        if (updates.size() < 1)
            return false;
        Random rand = new Random();
        updates.add(updates.get(rand.nextInt(updates.size())).clone());
        return true;
    }

    public Command clone()
    {
        Command temp = new Command(lineNo);
        for (Update update : updates)
        {
            temp.addUpdate(update.clone());
        }
        if (action != null)
            temp.setAction(action.clone());
        return temp;
    }
}
