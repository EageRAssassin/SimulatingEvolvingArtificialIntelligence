package JunitTest;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import server.exceptions.SyntaxError;
import server.parse.ParserImpl;
import server.parse.Tokenizer;
import server.ast.Mutation;
import server.ast.MutationFactory;
import server.ast.ProgramImpl;

public class JUnitTest
{
    @Ignore
    @Test
    // To test if the prettyprinting result is the same as the ast given to the
    // program
    public void testPrettyPrint()
    {
        Reader reader = null;
        try
        {
            reader = new FileReader("test");
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            System.exit(0);
        }
        Tokenizer t = new Tokenizer(reader);
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        System.out.println(ast);
        assertTrue(ast
                .toString()
                .equals("mem[1] != 17 --> mem[2] := 18;\n\nmem[1] != 17 --> mem[2] := 18;\n\nmem[4] != 17 --> mem[5] := 18;\n\nmem[1] != 12 --> mem[2] := 11;"));
    }

    // To see if the mutation can Duplicate mem[1] + 2 > 3 and ahead[4] > - (5
    // mod 6) --> mem[2] := 18\n\nbud; to a different ast tree
    @Test
    public void testMutationDuplicate()
    {
        String filename = "GeneralMutationTest.txt";
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
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        System.out.println("Before Dupicate:");
        System.out.println(ast.toString());
        Mutation m = MutationFactory.getDuplicate();
        m.addRules(ast.root);
        m.mutate(1);
        System.out.println("After Dupicate:");
        System.out.println(ast.toString());
        System.out.println();
        assertTrue(!ast.toString().equals(reader.toString()));
    }

    // To see if the mutation can Insert mem[1] + 2 > 3 and ahead[4] > - (5 mod
    // 6) --> mem[2] := 18\n\nbud; to a different ast tree
    @Test
    public void testMutationInsert()
    {
        String filename = "GeneralMutationTest.txt";
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
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        System.out.println("Before Insert:");
        System.out.println(ast.toString());
        Mutation m = MutationFactory.getInsert();
        m.addRules(ast.root);
        m.mutate(0);
        System.out.println("After Insert:");
        System.out.println(ast.toString());
        System.out.println();
        assertTrue(!ast.toString().equals(reader.toString()));
    }

    // To see if the mutation can Replace mem[1] + 2 > 3 and ahead[4] > - (5 mod
    // 6) --> mem[2] := 18\n\nbud; to a different ast tree
    @Test
    public void testMutationReplace()
    {
        String filename = "GeneralMutationTest.txt";
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
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        System.out.println("Before Replace:");
        System.out.println(ast.toString());
        Mutation m = MutationFactory.getReplace();
        m.addRules(ast.root);
        m.mutate(1);
        System.out.println("After Replace:");
        System.out.println(ast.toString());
        System.out.println();
        assertTrue(!ast.toString().equals(reader.toString()));
    }

    // To see if the mutation can Transform mem[1] + 2 > 3 and ahead[4] > - (5
    // mod 6) --> mem[2] := 18\n\nbud; to a different ast tree
    @Test
    public void testMutationTransform()
    {
        String filename = "GeneralMutationTest.txt";
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
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        System.out.println("Before Transform:");
        System.out.println(ast.toString());
        Mutation m = MutationFactory.getTransform();
        m.addRules(ast.root);
        m.mutate(0);
        System.out.println("After Transform:");
        System.out.println(ast.toString());
        System.out.println();
        assertTrue(!ast.toString().equals(reader.toString()));
    }

    // To see if the mutation can swap 1 > 2 --> bud; to 2 > 1 --> bud;
    // which is the smallest rule that the mutation can swap
    @Test
    public void testMutationSwap()
    {
        String filename = "SwapTest.txt";
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
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        Mutation m = MutationFactory.getSwap();
        m.addRules(ast.root);
        m.mutate(0);
        assertTrue(ast.toString().equals("2 > 1 --> bud;"));
    }

    // To see if the mutation can remove mem[1] > 2 --> bud; to 1 > 2 --> bud;
    // To see if the mutation can remove 1 + 1 > 2 --> bud; to 1 > 2 --> bud;
    // To see if the mutation can remove two "1 > 2 --> bud;" rules to one rule;
    // which are the smallest rules that the mutation can remove
    @Test
    public void testMutationRemove()
    {
        // Remove Test ONE
        String filename = "RemoveTest1.txt";
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
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        Mutation m = MutationFactory.getRemove();
        m.addRules(ast.root);
        m.mutate(0);
        assertTrue(ast.toString().equals("1 > 2 --> bud;"));
        // Remove Test TWO
        filename = "RemoveTest2.txt";
        try
        {
            reader = new FileReader(filename);
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            System.exit(0);
        }
        t = new Tokenizer(reader);
        ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        m = MutationFactory.getRemove();
        m.addRules(ast.root);
        m.mutate(0);
        assertTrue(ast.toString().equals("1 > 2 --> bud;"));
        // Remove Test THREE
        filename = "RemoveTest3.txt";
        try
        {
            reader = new FileReader(filename);
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            System.exit(0);
        }
        t = new Tokenizer(reader);
        ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        m = MutationFactory.getRemove();
        m.addRules(ast.root);
        m.mutate(0);
        assertTrue(ast.toString().equals("1 > 2 --> bud;"));
    }

    // To test if the mutated ast tree can be parsed again
    // Also to test if the continuous mutation could incur any errors
    @Test
    public void testMutation()
    {
        System.out
                .println("***To test if the mutated ast tree can be parsed again***");
        String filename = "a.txt";
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
        ProgramImpl ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        for (int i = 0; i < 50; i++)
        {
            Random rand = new Random();
            int mutateType = rand.nextInt(6);
            int index = rand.nextInt(ast.size());
            Mutation m = null;
            switch (mutateType)
            {
            case 0:
                m = MutationFactory.getDuplicate();
                m.addRules(ast.root);
                m.mutate(index);
                break;
            case 1:
                m = MutationFactory.getInsert();
                m.addRules(ast.root);
                m.mutate(index);
                break;
            case 2:
                m = MutationFactory.getRemove();
                m.addRules(ast.root);
                m.mutate(index);
                break;
            case 3:
                m = MutationFactory.getReplace();
                m.addRules(ast.root);
                m.mutate(index);
                break;
            case 4:
                m = MutationFactory.getSwap();
                m.addRules(ast.root);
                m.mutate(index);
                break;
            case 5:
                m = MutationFactory.getTransform();
                m.addRules(ast.root);
                m.mutate(index);
                break;
            }
        }
        Writer writer = null;
        try
        {
            writer = new FileWriter("a1.txt");
            writer.write(ast.toString());
            writer.flush();
        } catch (IOException e)
        {
        }
        try
        {
            reader = new FileReader("a1.txt");
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            System.exit(0);
        }
        t = new Tokenizer(reader);
        ast = new ProgramImpl();
        try
        {
            ast = ParserImpl.parseProgram(t);
        } catch (SyntaxError e)
        {
            System.out.println("Syntax Error!");
        }
        System.out.println(ast);
        System.out.println("***TESTING SUCCESSFULLY***");
    }
}
