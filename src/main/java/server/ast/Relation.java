package server.ast;

import java.util.Random;
import java.util.ArrayList;

import server.parse.Token;
import server.parse.TokenType;

public class Relation implements Condition
{
    public Token relOp;
    public Expr left, right;
    public int lineNo;

    public Relation(Token type, Expr left, Expr right, int lineNo)
    {
        this.relOp = type;
        this.left = left;
        this.right = right;
        this.lineNo = lineNo;
    }

    @Override
    public int size()
    {
        if (right == null)
            return left.size() + 1;
        else
            return left.size() + right.size() + 1;
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
            queue.add(left);
            queue.add(right);
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
        left.prettyPrint(sb);
        sb.append(" ");
        sb.append(relOp);
        sb.append(" ");
        right.prettyPrint(sb);
        return sb;
    }

    @Override
    public boolean swap()
    {
        if (right == null)
            return false;
        else
        {
            Expr temp;
            temp = left;
            left = right;
            right = temp;
            return true;
        }
    }

    @Override
    public boolean transform()
    {
        Random rand = new Random();
        int transformType = rand.nextInt(6);
        switch (transformType)
        {
        case 0:
            this.relOp = new Token(TokenType.LT, relOp.lineNo);
            break;
        case 1:
            this.relOp = new Token(TokenType.LE, relOp.lineNo);
            break;
        case 2:
            this.relOp = new Token(TokenType.EQ, relOp.lineNo);
            break;
        case 3:
            this.relOp = new Token(TokenType.GE, relOp.lineNo);
            break;
        case 4:
            this.relOp = new Token(TokenType.GT, relOp.lineNo);
            break;
        case 5:
            this.relOp = new Token(TokenType.NE, relOp.lineNo);
            break;
        }
        return true;
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

    public Relation clone()
    {
        return new Relation(relOp, left.clone(), right.clone(), lineNo);
    }
}
