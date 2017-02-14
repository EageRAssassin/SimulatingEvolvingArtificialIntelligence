package server.ast;

import java.util.Random;

import server.parse.Token;
import server.parse.Token.NumToken;

public class Number implements Expr
{
    public NumToken value;

    public Number(Token value)
    {
        this.value = value.toNumToken();
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb)
    {
        sb.append(value);
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
        Random r = new Random();
        value = new NumToken(
                Math.abs(java.lang.Integer.MAX_VALUE / r.nextInt()),
                value.lineNo);
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
            throw new IndexOutOfBoundsException();
        }
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

    public Number clone()
    {
        return new Number(value);
    }
}
