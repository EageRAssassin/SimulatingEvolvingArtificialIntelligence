package server.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Rules implements Node
{
    public ArrayList<Rule> rules = new ArrayList<Rule>();

    /**
     * Add a rule Node to the Rules Node
     *
     * @param rule
     *            The rule Node added to the Rules Node
     */
    public void addRule(Rule rule)
    {
        rules.add(rule);
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb)
    {
        for (Rule rule : rules)
        {
            rule.prettyPrint(sb);
            if (rules.indexOf(rule) < rules.size() - 1)
            {
                sb.append("\n\n");
            }
        }
        return sb;
    }

    @Override
    public boolean swap()
    {
        if (rules.size() < 2)
            return false;
        else
        {
            int swap1 = 0, swap2 = 0;
            Random rand = new Random();
            while (swap1 == swap2)
            {
                swap1 = rand.nextInt(rules.size());
                swap2 = rand.nextInt(rules.size());
            }
            Collections.swap(rules, swap1, swap2);
            return true;
        }
    }

    @Override
    public int size()
    {
        int size = 0;
        if (rules.size() == 0)
        {
            return 0;
        } else
        {
            for (Rule rule : rules)
            {
                size += rule.size();
            }
        }
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
            for (Rule rule : rules)
            {
                queue.add(rule);
            }
            while (queue.size() != 0)
            {
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
        if (rules.size() < 2)
            return false;
        else
        {
            rules.remove(rand.nextInt(rules.size()));
            return true;
        }
    }

    @Override
    public boolean duplicate()
    {
        Random rand = new Random();
        rules.add(rules.get(rand.nextInt(rules.size())).clone());
        return true;
    }

    public ArrayList<Rule> getRules()
    {
        return rules;
    }

    public Rules clone()
    {
        Rules temp = new Rules();
        for (Rule rule : rules)
        {
            temp.addRule(rule.clone());
        }
        return temp;
    }
}
