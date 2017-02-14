package JunitTest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import server.ast.ProgramImpl;
import server.exceptions.SyntaxError;
import server.parse.Tokenizer;

public class TokenizerTest
{

    public static void main(String[] args)
    {
        Reader reader = null;
        try
        {
            reader = new FileReader("critterWithoutAction");
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
            System.exit(0);
        }
        Tokenizer t = new Tokenizer(reader);
         while(t.hasNext()){
         System.out.println(t.next());
         }
//        ProgramImpl ast = new ProgramImpl();
//        try
//        {
//            ast = ParserImpl.parseProgram(t);
//        } catch (SyntaxError e)
//        {
//            System.out.println("Syntax Error!");
//        }
//        for (int i = 0; i < ast.size(); i++)
//        {
//            StringBuilder sb = new StringBuilder();
//            ast.nodeAt(i).prettyPrint(sb);
//            System.out.println(i + ": " + ast.nodeAt(i) + " " + sb);
//        }
    }

}
