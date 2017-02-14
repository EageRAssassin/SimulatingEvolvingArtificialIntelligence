package JunitTest;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.junit.Test;

import server.ast.Condition;
import server.ast.Expr;
import server.ast.ProgramImpl;
import server.exceptions.SyntaxError;
import server.interpret.Interpreter;
import server.interpret.InterpreterImpl;
import server.parse.ParserImpl;
import server.parse.Tokenizer;
import server.world.Critter;
import server.world.World;

public class InterpreterTest
{

    /**
     * test Condition evaluation based on the InterpreterTestCon.txt, which
     * includes any corner cases we can think of
     */
    @Test
    public void testEvalCondition()
    {
        String filename = "InterpreterTestCon.txt";
        Reader reader = null;
        try
        {
            reader = new FileReader(filename);
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            System.exit(0);
        }

        Tokenizer t = new Tokenizer(reader);
        Condition c = null;
        try
        {
            c = ParserImpl.parseCondition(t, false);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }

        Tokenizer t2 = null;
        try
        {
            t2 = new Tokenizer(new FileReader("a.txt"));
        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t2);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        Critter critter = new Critter("a", 8, 1, 2, 3, 4, 5, null, 0, 0, 0, ast);
        Interpreter i = new InterpreterImpl(critter);
        assertTrue(i.eval(c));
    }

    /**
     * test Expression evaluation based on the InterpreterTestExpr.txt, which
     * includes any corner cases we can think of And this test case also include
     * the usage and evaluation of the mem array inside the critter object
     */
    @Test
    public void testEvalExpr()
    {
        String filename = "InterpreterTestExpr.txt";
        Reader reader = null;
        try
        {
            reader = new FileReader(filename);
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            System.exit(0);
        }
        Tokenizer t = new Tokenizer(reader);
        Expr ex = null;
        try
        {
            ex = ParserImpl.parseExpression(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }

        Tokenizer t2 = null;
        try
        {
            t2 = new Tokenizer(new FileReader("a.txt"));
        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t2);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        Critter critter = new Critter("a", 8, 1, 2, 3, 4, 5, null, 0, 0, 0, ast);
        Interpreter i = new InterpreterImpl(critter);
        assertEquals(i.eval(ex), 50);
    }

    /**
     * test Expression evaluation based on the InterpreterTestExpr.txt, which
     * includes any corner cases we can think of And this test case also include
     * the usage and evaluation of the mem array inside the critter object
     */
    @Test
	public void testInterpret() {
		Tokenizer t = null;
		try {
			t = new Tokenizer(new FileReader("InterpreterTestGeneral.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		ProgramImpl ast = new ProgramImpl();
		try {
			ast = ParserImpl.parseProgram(t);
		} catch (SyntaxError e) {
			System.out.println("Syntax Error!");
		}
		World world = new World();
		Critter critter = new Critter("a", 8, 1, 2, 3, 200, 0, world, 0, 0, 0, ast);
		Interpreter i = new InterpreterImpl(critter);
		i.interpret(null);
		assertEquals(critter.getMem(7),15);
		assertEquals(critter.getMem(5),16);
	}
}
