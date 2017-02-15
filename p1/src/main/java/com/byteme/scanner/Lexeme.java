package com.byteme.scanner;

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

    @Override
    public String toString() {
        return literal;
    }

    public abstract DFA getDFA();
    //public abstract Token tokenize();
}
