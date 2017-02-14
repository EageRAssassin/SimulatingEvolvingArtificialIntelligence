package server.parse;

import java.io.Reader;

import server.ast.Program;

/**
 * An interface for parsing a critter program.
 *
 */
public interface Parser
{

    /**
     * Parses a program in the given file.
     * 
     * @param r
     *            A reader to read the program
     * @return The parsed program, or null if the program contains a syntax
     *         error.
     */
    Program parse(Reader r);
}
