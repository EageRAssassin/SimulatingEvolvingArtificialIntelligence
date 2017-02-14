package server.ast;

import java.util.ArrayList;
import java.util.Random;

import server.parse.TokenType;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 *
 */
public class BinaryCondition implements Condition
{
    public Operator op;
    public Condition l, r;
    public int lineNo;
    private boolean hasBrace = false;
    public ArrayList<Condition> extraCondition = new ArrayList<Condition>();

    /**
     * Create an AST representation of l op r.
     *
     * @param l
     * @param op
     * @param r
     */
    public BinaryCondition(Condition l, Operator op, Condition r,
            boolean hasBrace, int lineNo)
    {
        this.l = l;
        this.r = r;
        this.op = op;
        this.hasBrace = hasBrace;
        this.lineNo = lineNo;
    }

    public void addExtraCondition(Condition condition)
    {
        extraCondition.add(condition);
    }

    /**
     * An enumeration of all possible binary condition operators.
     */
    public enum Operator
    {
        OR, AND;
    }

    @Override
    public int size()
    {
        int size = l.size() + r.size() + 1;
        for (Condition c : extraCondition)
        {
            size += c.size();
        }
        return size;
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb)
    {
        if (hasBrace)
            sb.append("{");
        l.prettyPrint(sb);
        sb.append(" ");
        sb.append(op.toString().toLowerCase());
        sb.append(" ");
        r.prettyPrint(sb);
        if (extraCondition.size() > 0)
            for (int i = 0; i < extraCondition.size(); i++)
            {
                sb.append(" ");
                sb.append(op.toString().toLowerCase());
                sb.append(" ");
                extraCondition.get(i).prettyPrint(sb);
            }
        if (hasBrace)
            sb.append("}");
        return sb;
    }

    @Override
    public boolean swap()
    {
        Condition temp;
        temp = l;
        l = r;
        r = temp;
        return true;
    }

    @Override
    public boolean transform()
    {
        op = op.equals(Operator.OR) ? Operator.AND : Operator.OR;
        return true;
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
            queue.add(l);
            queue.add(r);
            for (Condition c : extraCondition)
            {
                queue.add(c);
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
    public boolean remove()
    {
        Random rand = new Random();
        boolean random1 = rand.nextBoolean();
        if (random1)
        {
            if (l instanceof BinaryCondition)
            {
                boolean random2 = rand.nextBoolean();
                if (random2)
                {
                    l = ((BinaryCondition) l).l;
                    return true;
                } else
                {
                    l = ((BinaryCondition) l).r;
                    return true;
                }
            }
        } else
        {
            if (r instanceof BinaryCondition)
            {
                boolean random2 = rand.nextBoolean();
                if (random2)
                {
                    r = ((BinaryCondition) r).l;
                    return true;
                } else
                {
                    r = ((BinaryCondition) r).r;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean duplicate()
    {
        return false;
    }

    public BinaryCondition clone()
    {
        return new BinaryCondition(l.clone(), op, r.clone(), hasBrace,
                this.lineNo);
    }
}
