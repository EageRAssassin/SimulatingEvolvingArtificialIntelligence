package server.parse;

import static server.parse.TokenCategory.ACTION;
import static server.parse.TokenCategory.ADDOP;
import static server.parse.TokenCategory.MEMSUGAR;
import static server.parse.TokenCategory.MULOP;
import static server.parse.TokenCategory.SENSOR;
import static server.parse.TokenType.EOF;
import static server.parse.TokenType.ERROR;
import static server.parse.TokenType.NUM;

import java.util.InputMismatchException;

/**
 * A Token represents a legal token (symbol) in the critter language
 */
public class Token
{
    private final TokenType type;
    public final int lineNo;

    /**
     * Create a token with the specified type.
     * 
     * @param type
     *            The ID of the desired token type
     * @param lineNo
     *            The line number in the input file containing this token.
     */
    public Token(final TokenType type, final int lineNo)
    {
        this.type = type;
        this.lineNo = lineNo;
    }

    /**
     * @return The type of this token
     */
    public TokenType getType()
    {
        return type;
    }

    /**
     * @return The line number in the input file of this token.
     */
    int lineNumber()
    {
        return lineNo;
    }

    /**
     * Determine whether this token is of number type.
     * 
     * @return true if this token is of number type
     */
    boolean isNum()
    {
        return type == NUM;
    }

    /**
     * @return The number token associated with this token.
     * @throws InputMismatchException
     *             if this token is not of number type
     */
    public NumToken toNumToken()
    {
        if (isNum())
            return (NumToken) this;
        throw new InputMismatchException("Token is not a number.");
    }

    /**
     * Determine whether this token is of action type.
     * 
     * @return true if this token is of action type
     */
    boolean isAction()
    {
        return type.category == ACTION;
    }

    /**
     * Determine whether this token is of addop type.
     * 
     * @return true if this token is of addop type
     */
    boolean isAddOp()
    {
        return type.category == ADDOP;
    }

    /**
     * Determine whether this token is of mulop type.shorthand.
     * 
     * @return true if this token is of mulop type.shorthand
     */
    boolean isMulOp()
    {
        return type.category == MULOP;
    }

    /**
     * Determine whether this token is of sensor type.shorthand.
     * 
     * @return true if this token is of sensor type.shorthand
     */
    boolean isSensor()
    {
        return type.category == SENSOR;
    }

    /**
     * Determine whether this token is syntactic sugar for memory locations
     * 
     * @return true if this token is syntactic sugar for memory locations
     */
    boolean isMemSugar()
    {
        return type.category == MEMSUGAR;
    }

    @Override
    public String toString()
    {
        return type.stringRep;
    }

    /**
     * A NumToken is a token containing a number.
     * 
     * @author Chinawat
     */
    public static class NumToken extends Token
    {

        private final int value;

        public NumToken(final int value, final int lineNo)
        {
            super(NUM, lineNo);
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return Integer.toString(value);
        }
    }

    static class EOFToken extends Token
    {

        private final String bufferContents;

        EOFToken(final String bufferContents, final int lineNo)
        {
            super(EOF, lineNo);
            this.bufferContents = bufferContents;
        }

        String getBufferContents()
        {
            return bufferContents;
        }

        @Override
        public String toString()
        {
            return "Buffer contained " + bufferContents + " at EOF";
        }
    }

    static class ErrorToken extends Token
    {

        private final String value;

        ErrorToken(final String value, final int lineNo)
        {
            super(ERROR, lineNo);
            this.value = value;
        }

        String getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "[error@" + lineNo + "]: " + value;
        }
    }
}
