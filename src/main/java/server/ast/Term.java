package server.ast;

import java.util.Random;
import java.util.ArrayList;

import server.parse.TokenType;

public class Term implements Expr
{
    public Factor l, r;
    public TokenType mulOperation;
    public int lineNo;
    public ArrayList<Factor> extraFactor = new ArrayList<Factor>();
    public ArrayList<TokenType> extraMulOp = new ArrayList<TokenType>();

    public Term(Factor l, TokenType mulOperation, Factor r, int lineNo)
    {
        this.l = l;
        this.mulOperation = mulOperation;
        this.r = r;
        this.lineNo = lineNo;
    }

    public void addExtraFactor(Factor factor, TokenType mulOperation)
    {
        extraFactor.add(factor);
        extraMulOp.add(mulOperation);
    }

    @Override
    public int size()
    {
        int size = 0;
        if (r == null)
            size = l.size() + 1;
        else
            size = l.size() + r.size() + 1;
        for (Factor f : extraFactor)
        {
            size += f.size();
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
            for (Factor f : extraFactor)
            {
                queue.add(f);
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
        if ((r == null) || (mulOperation == null))
        {
            l.prettyPrint(sb);
        } else
        {
            l.prettyPrint(sb);
            if (mulOperation.equals(TokenType.MUL))
                sb.append(" * ");
            else if (mulOperation.equals(TokenType.DIV))
                sb.append(" / ");
            else if (mulOperation.equals(TokenType.MOD))
                sb.append(" mod ");
            r.prettyPrint(sb);
        }
        if (extraFactor.size() > 0)
        {
            for (int i = 0; i < extraFactor.size(); i++)
            {
                if (extraMulOp.get(i).equals(TokenType.MUL))
                    sb.append(" * ");
                else if (extraMulOp.get(i).equals(TokenType.DIV))
                    sb.append(" / ");
                else if (extraMulOp.get(i).equals(TokenType.MOD))
                    sb.append(" mod ");
                extraFactor.get(i).prettyPrint(sb);
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
            Factor temp;
            temp = l;
            l = r;
            r = temp;
            return true;
        }
    }

    @Override
    public boolean transform()
    {
        if (mulOperation == null)
            return false;
        Random rand = new Random();
        boolean transformChance = rand.nextBoolean();// To decide the mulOp to
                                                     // transform into other
                                                     // two kinds
        if (mulOperation.equals(TokenType.MUL))
            mulOperation = transformChance ? TokenType.DIV : TokenType.MOD;
        else if (mulOperation.equals(TokenType.DIV))
            mulOperation = transformChance ? TokenType.MUL : TokenType.MOD;
        else if (mulOperation.equals(TokenType.MOD))
            mulOperation = transformChance ? TokenType.DIV : TokenType.MUL;
        return true;
    }

    @Override
    public boolean remove()
    {
        if (r != null)
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
        } else
        {
            if (l.child instanceof Sensor)
                if (((Sensor) l.child).child != null)
                {
                    l.child = ((Sensor) l.child).child;
                    return true;
                }
            if (l.child instanceof Memory)
            {
                l.child = ((Memory) l.child).value;
                return true;
            }
            if (l.isNegative)
            {
                l.isNegative = false;
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

    public Term clone()
    {
        if (r == null)
            return new Term(l.clone(), null, null, lineNo);
        return new Term(l.clone(), mulOperation, r.clone(), lineNo);
    }
}
