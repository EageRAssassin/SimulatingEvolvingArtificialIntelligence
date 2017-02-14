package JunitTest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Random;

import server.ast.Mutation;
import server.ast.MutationFactory;
import server.ast.ProgramImpl;
import server.exceptions.SyntaxError;
import server.parse.ParserImpl;
import server.parse.Tokenizer;

//To test all mutations gets equal probability of invoking

public class MutationTest
{

    public static void main(String[] args)
    {
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
        int total = 0, mutate1 = 0, mutate2 = 0, mutate3 = 0, mutate4 = 0, mutate5 = 0, mutate6 = 0;
        for (int i = 0; i < 10000; i++)
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
                mutate1++;
                break;
            case 1:
                m = MutationFactory.getInsert();
                m.addRules(ast.root);
                m.mutate(index);
                mutate2++;
                break;
            case 2:
                m = MutationFactory.getRemove();
                m.addRules(ast.root);
                m.mutate(index);
                mutate3++;
                break;
            case 3:
                m = MutationFactory.getReplace();
                m.addRules(ast.root);
                m.mutate(index);
                mutate4++;
                break;
            case 4:
                m = MutationFactory.getSwap();
                m.addRules(ast.root);
                m.mutate(index);
                mutate5++;
                break;
            case 5:
                m = MutationFactory.getTransform();
                m.addRules(ast.root);
                m.mutate(index);
                mutate6++;
                break;
            }
            total++;
            StringBuilder sb = new StringBuilder();
            ast.prettyPrint(sb);
            System.out.println(sb);
            System.out.println(m.toString());
            System.out.println();
        }
        System.out.println("Mutated " + total + " times");
        System.out.println("Mutated1 " + mutate1 + " times");
        System.out.println("Mutated2 " + mutate2 + " times");
        System.out.println("Mutated3 " + mutate3 + " times");
        System.out.println("Mutated4 " + mutate4 + " times");
        System.out.println("Mutated5 " + mutate5 + " times");
        System.out.println("Mutated6 " + mutate6 + " times");
    }
}
