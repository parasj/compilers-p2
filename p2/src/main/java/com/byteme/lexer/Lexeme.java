package com.byteme.lexer;

/**
 * A Lexeme is an abstract representation of some DFA that accepts a string of input.
 */
public abstract class Lexeme {

    /**
     * The literal name of this lexeme (i.e., "ArrayInt")
     */
    protected final String literal;

    public Lexeme(String literal) {
        this.literal = literal;
    }

    // TODO: eventually just use constructLexemeWithDFA or include in constructor
    public abstract DFA getDFA();

    public abstract String stringify(String token);

    @Override
    public String toString() {
        return literal;
    }
}
