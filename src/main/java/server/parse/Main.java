package server.parse;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Random;

import server.exceptions.SyntaxError;
import server.ast.Mutation;
import server.ast.MutationFactory;
import server.ast.ProgramImpl;

public class Main
{

    public static void main(String[] args)
    {
        switch (args[0])
        {
        case "--mutate":
            mutate(args);
            break;
        default:
            parseAndPrint(args);
            break;
        }
    }

    private static void mutate(String[] args)
    {
        int numToMutate = Integer.parseInt(args[1]);
        String filename = args[2];
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
        for (int i = 0; i < numToMutate; i++)
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
            StringBuilder sb = new StringBuilder();
            ast.prettyPrint(sb);
            System.out.println(sb);
            System.out.println(m.toString());
            System.out.println();
        }
    }

    private static void parseAndPrint(String[] args)
    {
        String filename = args[0];
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
        StringBuilder sb = new StringBuilder();
        ast.prettyPrint(sb);
        System.out.println(sb);
    }
}
