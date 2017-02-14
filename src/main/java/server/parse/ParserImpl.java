package server.parse;

import java.io.Reader;

import server.ast.*;
import server.ast.BinaryCondition.Operator;
import server.ast.Number;
import server.exceptions.SyntaxError;
import server.parse.Token.NumToken;

public class ParserImpl implements Parser
{

    @Override
    public Program parse(Reader r)
    {
        Tokenizer tokenizer = new Tokenizer(r);
        try
        {
            return parseProgram(tokenizer);
        } catch (SyntaxError e)
        {
            return null;
        }
    }

    /**
     * Parses a program from the stream of tokens provided by the Tokenizer,
     * consuming tokens representing the program. All following methods with a
     * name "parseX" have the same spec except that they parse syntactic form X.
     * 
     * @return the created AST
     * @throws SyntaxError
     *             if there the input tokens have invalid syntax
     */
    public static ProgramImpl parseProgram(Tokenizer t) throws SyntaxError
    {
        ProgramImpl ast = new ProgramImpl();
        while (t.hasNext())
        {
            ast.addRule(parseRule(t));
        }
        return ast;
    }

    public static Rule parseRule(Tokenizer t) throws SyntaxError
    {
        Rule rule = new Rule();
        rule.addCondition(parseCondition(t, false));
        consume(t, TokenType.ARR);
        rule.addCommand(parseCommand(t));
        consume(t, TokenType.SEMICOLON);
        return rule;
    }

    public static Condition parseCondition(Tokenizer t, boolean hasBrace)
            throws SyntaxError
    {
        Condition condition = parseConjunction(t, hasBrace);
        if ((t.peek()).getType().equals(TokenType.OR))
        {
            consume(t, TokenType.OR);
            condition = new BinaryCondition(condition, Operator.OR,
                    parseConjunction(t, false), false, t.peek().lineNo);
        }
        while ((t.peek()).getType().equals(TokenType.OR))
        {
            consume(t, TokenType.OR);
            ((BinaryCondition) condition).addExtraCondition(parseConjunction(t,
                    false));
        }
        return condition;
    }

    public static Condition parseConjunction(Tokenizer t, boolean hasBrace)
            throws SyntaxError
    {
        Condition conjunction = parseRelation(t);
        if ((t.peek()).getType().equals(TokenType.AND))
        {
            consume(t, TokenType.AND);
            conjunction = new BinaryCondition(conjunction, Operator.AND,
                    parseRelation(t), hasBrace, t.peek().lineNo);
        }
        while ((t.peek()).getType().equals(TokenType.AND))
        {
            consume(t, TokenType.AND);
            ((BinaryCondition) conjunction).addExtraCondition(parseRelation(t));
        }
        return conjunction;
    }

    public static Condition parseRelation(Tokenizer t) throws SyntaxError
    {
        Relation relation;
        Expr left, right;
        Token type;
        if (t.peek().toString().equals("{"))
        {
            consume(t, TokenType.LBRACE);
            Condition condition = parseCondition(t, true);
            consume(t, TokenType.RBRACE);
            return condition;
        }
        left = parseExpression(t);
        type = t.next();
        right = parseExpression(t);
        relation = new Relation(type, left, right, t.peek().lineNo);
        return relation;
    }

    public static Command parseCommand(Tokenizer t) throws SyntaxError
    {
        Command command = new Command(t.peek().lineNo);
        while (t.peek().toString() != ";")
        {
            if (t.peek().isAction())
                command.setAction(parseAction(t));
            else
                command.addUpdate(parseUpdate(t));
        }
        return command;
    }

    public static Expr parseExpression(Tokenizer t) throws SyntaxError
    {
        Term left, right;
        boolean isPlus;
        left = (Term) parseTerm(t);
        ExprGeneral e = null;
        if (t.peek().isAddOp())
        {
            if (t.peek().getType() == TokenType.PLUS)
            {
                consume(t, TokenType.PLUS);
                isPlus = true;
            } else
            {
                consume(t, TokenType.MINUS);
                isPlus = false;
            }
            right = (Term) parseTerm(t);
            if (isPlus)
            {
                e = new ExprGeneral(left, TokenType.PLUS, right);
            } else
            {
                e = new ExprGeneral(left, TokenType.MINUS, right);
            }
        }
        while (t.peek().isAddOp())
        {
            if (t.peek().getType() == TokenType.PLUS)
            {
                consume(t, TokenType.PLUS);
                isPlus = true;
            } else
            {
                consume(t, TokenType.MINUS);
                isPlus = false;
            }
            right = (Term) parseTerm(t);
            if (isPlus)
            {
                e.addExtraTerm(right, TokenType.PLUS);
            } else
            {
                e.addExtraTerm(right, TokenType.MINUS);
            }
        }
        if (e != null)
            return e;
        return new ExprGeneral(left, null, null);
    }

    public static Expr parseTerm(Tokenizer t) throws SyntaxError
    {
        Factor left, right;
        String type = "";
        left = (Factor) parseFactor(t);
        Term term = null;
        if (t.peek().isMulOp())
        {
            if (t.peek().getType() == TokenType.MUL)
            {
                consume(t, TokenType.MUL);
                type = "MUL";
            } else if (t.peek().getType() == TokenType.DIV)
            {
                consume(t, TokenType.DIV);
                type = "DIV";
            } else if (t.peek().getType() == TokenType.MOD)
            {
                consume(t, TokenType.MOD);
                type = "MOD";
            }
            right = (Factor) parseFactor(t);
            switch (type)
            {
            case "MUL":
                term = new Term(left, TokenType.MUL, right, t.peek().lineNo);
                break;
            case "DIV":
                term = new Term(left, TokenType.DIV, right, t.peek().lineNo);
                break;
            case "MOD":
                term = new Term(left, TokenType.MOD, right, t.peek().lineNo);
                break;
            }
        }

        while (t.peek().isMulOp())
        {
            if (t.peek().getType() == TokenType.MUL)
            {
                consume(t, TokenType.MUL);
                type = "MUL";
            } else if (t.peek().getType() == TokenType.DIV)
            {
                consume(t, TokenType.DIV);
                type = "DIV";
            } else if (t.peek().getType() == TokenType.MOD)
            {
                consume(t, TokenType.MOD);
                type = "MOD";
            }
            right = (Factor) parseFactor(t);
            switch (type)
            {
            case "MUL":
                term.addExtraFactor(right, TokenType.MUL);
                break;
            case "DIV":
                term.addExtraFactor(right, TokenType.DIV);
                break;
            case "MOD":
                term.addExtraFactor(right, TokenType.MOD);
                break;
            }
        }
        if (term != null)
            return term;
        return new Term(left, null, null, t.peek().lineNo);

    }

    public static Expr parseFactor(Tokenizer t) throws SyntaxError
    {
        Factor factor = null;
        Token sensorType;
        switch (t.peek().getType())
        {
        case NUM:
            factor = new Factor(false, new Number(t.next()), false);
            break;
        case MEM:
            consume(t, TokenType.MEM);
            consume(t, TokenType.LBRACKET);
            factor = new Factor(false, new Memory(parseExpression(t)), false);
            consume(t, TokenType.RBRACKET);
            break;
        case LPAREN:
            consume(t, TokenType.LPAREN);
            factor = new Factor(false, parseExpression(t), true);
            consume(t, TokenType.RPAREN);
            break;
        case MINUS:
            consume(t, TokenType.MINUS);
            factor = new Factor(true, parseExpression(t), false);
            break;
        case NEARBY:
            sensorType = t.next();
            consume(t, TokenType.LBRACKET);
            factor = new Factor(false, new Sensor(sensorType,
                    parseExpression(t)), false);
            consume(t, TokenType.RBRACKET);
            break;
        case AHEAD:
            sensorType = t.next();
            consume(t, TokenType.LBRACKET);
            factor = new Factor(false, new Sensor(sensorType,
                    parseExpression(t)), false);
            consume(t, TokenType.RBRACKET);
            break;
        case RANDOM:
            sensorType = t.next();
            consume(t, TokenType.LBRACKET);
            factor = new Factor(false, new Sensor(sensorType,
                    parseExpression(t)), false);
            consume(t, TokenType.RBRACKET);
            break;
        case SMELL:
            sensorType = t.next();
            factor = new Factor(false, new Sensor(sensorType, null), false);
            break;
        case ABV_DEFENSE:
            NumToken numToken1 = new NumToken(1, t.peek().lineNo);
            Number number1 = new Number(numToken1);
            Factor factortemp1 = new Factor(false, number1, false);
            Term term1 = new Term(factortemp1, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral1 = new ExprGeneral(term1, null, null);
            Memory memory1 = new Memory(exprGeneral1);
            factor = new Factor(false, memory1, false);
            consume(t, TokenType.ABV_DEFENSE);
            break;
        case ABV_ENERGY:
            NumToken numToken4 = new NumToken(4, t.peek().lineNo);
            Number number4 = new Number(numToken4);
            Factor factortemp4 = new Factor(false, number4, false);
            Term term4 = new Term(factortemp4, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral4 = new ExprGeneral(term4, null, null);
            Memory memory4 = new Memory(exprGeneral4);
            factor = new Factor(false, memory4, false);
            consume(t, TokenType.ABV_ENERGY);
            break;
        case ABV_MEMSIZE:
            NumToken numToken0 = new NumToken(0, t.peek().lineNo);
            Number number0 = new Number(numToken0);
            Factor factortemp0 = new Factor(false, number0, false);
            Term term0 = new Term(factortemp0, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral0 = new ExprGeneral(term0, null, null);
            Memory memory0 = new Memory(exprGeneral0);
            factor = new Factor(false, memory0, false);
            consume(t, TokenType.ABV_MEMSIZE);
            break;
        case ABV_OFFENSE:
            NumToken numToken2 = new NumToken(2, t.peek().lineNo);
            Number number2 = new Number(numToken2);
            Factor factortemp2 = new Factor(false, number2, false);
            Term term2 = new Term(factortemp2, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral2 = new ExprGeneral(term2, null, null);
            Memory memory2 = new Memory(exprGeneral2);
            factor = new Factor(false, memory2, false);
            consume(t, TokenType.ABV_OFFENSE);
            break;
        case ABV_PASS:
            NumToken numToken5 = new NumToken(5, t.peek().lineNo);
            Number number5 = new Number(numToken5);
            Factor factortemp5 = new Factor(false, number5, false);
            Term term5 = new Term(factortemp5, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral5 = new ExprGeneral(term5, null, null);
            Memory memory5 = new Memory(exprGeneral5);
            factor = new Factor(false, memory5, false);
            consume(t, TokenType.ABV_PASS);
            break;
        case ABV_POSTURE:
            NumToken numToken7 = new NumToken(7, t.peek().lineNo);
            Number number7 = new Number(numToken7);
            Factor factortemp7 = new Factor(false, number7, false);
            Term term7 = new Term(factortemp7, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral7 = new ExprGeneral(term7, null, null);
            Memory memory7 = new Memory(exprGeneral7);
            factor = new Factor(false, memory7, false);
            consume(t, TokenType.ABV_POSTURE);
            break;
        case ABV_SIZE:
            NumToken numToken3 = new NumToken(3, t.peek().lineNo);
            Number number3 = new Number(numToken3);
            Factor factortemp3 = new Factor(false, number3, false);
            Term term3 = new Term(factortemp3, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral3 = new ExprGeneral(term3, null, null);
            Memory memory3 = new Memory(exprGeneral3);
            factor = new Factor(false, memory3, false);
            consume(t, TokenType.ABV_SIZE);
            break;
        case ABV_TAG:
            NumToken numToken6 = new NumToken(6, t.peek().lineNo);
            Number number6 = new Number(numToken6);
            Factor factortemp6 = new Factor(false, number6, false);
            Term term6 = new Term(factortemp6, null, null, t.peek().lineNo);
            ExprGeneral exprGeneral6 = new ExprGeneral(term6, null, null);
            Memory memory6 = new Memory(exprGeneral6);
            factor = new Factor(false, memory6, false);
            consume(t, TokenType.ABV_TAG);
            break;
        default:
            break;
        }
        return factor;
    }

    public static Update parseUpdate(Tokenizer t) throws SyntaxError
    {
        boolean isSugar = false;
        Expr exprInMem, exprGiven;
        if (t.peek().getType() == TokenType.MEM)
        {
            consume(t, TokenType.MEM);
            consume(t, TokenType.LBRACKET);
            exprInMem = parseExpression(t);
            consume(t, TokenType.RBRACKET);
        } else
        {
            exprInMem = parseExpression(t);
            isSugar = true;
        }
        consume(t, TokenType.ASSIGN);
        exprGiven = parseExpression(t);
        Update update = new Update(exprInMem, exprGiven, isSugar);
        return update;
    }

    public static Action parseAction(Tokenizer t) throws SyntaxError
    {
        Action action;
        if ((t.peek().toString().equals("tag"))
                || (t.peek().toString().equals("serve")))
        {
            action = new Action(t.next(), t.peek().lineNo);
            consume(t, TokenType.LBRACKET);
            action.setExpr(parseExpression(t));
            consume(t, TokenType.RBRACKET);
        } else
        {
            action = new Action(t.next(), t.peek().lineNo);
        }
        return action;
    }

    /**
     * Consumes a token of the expected type.
     * 
     * @throws SyntaxError
     *             if the wrong kind of token is encountered.
     */
    public static void consume(Tokenizer t, TokenType tt) throws SyntaxError
    {
        if (t.peek().getType().equals(tt))
            t.next();
        else
        {
            System.out.println("Line " + t.peek().lineNo);
            throw new SyntaxError();
        }

    }
}
