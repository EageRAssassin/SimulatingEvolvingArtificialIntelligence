package server.ast;

import java.util.ArrayList;
import java.util.Random;

import server.parse.*;

public class Action implements Node
{
    public Token value;
    public Expr child;
    public int lineNo;

    public Action(Token value, int lineNo)
    {
        this.value = value;
        this.lineNo = lineNo;
    }

    /**
     * set the Expression inside the Action Node
     * 
     * @param child
     *            The Expression inside the Action Node
     */
    public void setExpr(Expr child)
    {
        this.child = child;
    }

    @Override
    public int size()
    {
        if (child == null)
            return 1;
        else
            return child.size() + 1;
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
            queue.add(child);
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
        if (child == null)
        {
            sb.append(value);
        } else
        {
            sb.append(value);
            sb.append("[");
            child.prettyPrint(sb);
            sb.append("]");
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
        Random rand = new Random();
        int transformType = rand.nextInt(10);
        switch (transformType)
        {
        case 0:
            value = new Token(TokenType.WAIT, lineNo);
            break;
        case 1:
            value = new Token(TokenType.FORWARD, lineNo);
            break;
        case 2:
            value = new Token(TokenType.BACKWARD, lineNo);
            break;
        case 3:
            value = new Token(TokenType.LEFT, lineNo);
            break;
        case 4:
            value = new Token(TokenType.RIGHT, lineNo);
            break;
        case 5:
            value = new Token(TokenType.EAT, lineNo);
            break;
        case 6:
            value = new Token(TokenType.ATTACK, lineNo);
            break;
        case 7:
            value = new Token(TokenType.GROW, lineNo);
            break;
        case 8:
            value = new Token(TokenType.BUD, lineNo);
            break;
        case 9:
            value = new Token(TokenType.MATE, lineNo);
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

    public Action clone()
    {
        Action temp = new Action(value, lineNo);
        if (child != null)
            temp.setExpr(child.clone());
        return temp;
    }
}
