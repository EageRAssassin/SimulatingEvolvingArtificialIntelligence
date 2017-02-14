package server.ast;

import java.util.ArrayList;
import java.util.Random;

import server.parse.TokenType;

public class ExprGeneral implements Expr
{
    public Term l, r;
    public TokenType addOperation;
    public ArrayList<Term> extraTerm = new ArrayList<Term>();
    public ArrayList<TokenType> extraAddOp = new ArrayList<TokenType>();

    public ExprGeneral(Term l, TokenType addOperation, Term r)
    {
        this.l = l;
        this.addOperation = addOperation;
        this.r = r;
    }

    public void addExtraTerm(Term term, TokenType addOperation)
    {
        extraTerm.add(term);
        extraAddOp.add(addOperation);
    }

    @Override
    public int size()
    {
        int size = 0;
        if (r == null)
            size = l.size() + 1;
        else
            size = l.size() + r.size() + 1;
        for (Term t : extraTerm)
        {
            size += t.size();
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
            queue.add(l);
            queue.add(r);
            for (Term t : extraTerm)
            {
                queue.add(t);
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
        if (r == null)
        {
            l.prettyPrint(sb);
        } else
        {
            l.prettyPrint(sb);
            if (addOperation.equals(TokenType.PLUS))
                sb.append(" + ");
            else if (addOperation.equals(TokenType.MINUS))
                sb.append(" - ");
            r.prettyPrint(sb);
        }
        if (extraTerm.size() > 0)
        {
            for (int i = 0; i < extraTerm.size(); i++)
            {
                if (extraAddOp.get(i).equals(TokenType.PLUS))
                    sb.append(" + ");
                else if (extraAddOp.get(i).equals(TokenType.MINUS))
                    sb.append(" - ");
                extraTerm.get(i).prettyPrint(sb);
            }
        }
        return sb;
    }

    @Override
    public boolean swap()
    {
        if (r == null)
            return false;
        else
        {
            Term temp;
            temp = l;
            l = r;
            r = temp;
            return true;
        }
    }

    @Override
    public boolean transform()
    {
        if (addOperation == null)
            return false;
        addOperation = addOperation.equals(TokenType.PLUS) ? TokenType.MINUS
                : TokenType.PLUS;
        return true;
    }

    @Override
    public boolean remove()
    {
        if (!(r == null))
        {
            Random rand = new Random();
            boolean random = rand.nextBoolean();
            if (random)
            {
                r = null;
                return true;
            } else
            {
                l = r;
                r = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean duplicate()
    {
        return false;
    }

    public ExprGeneral clone()
    {
        if (r == null)
            return new ExprGeneral(l.clone(), null, null);
        return new ExprGeneral(l.clone(), addOperation, r.clone());
    }
}
