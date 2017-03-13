package com.byteme.lexer;

/**
 * A Lexeme is an abstract representation of some DFA that accepts a string of input.
 */
public abstract class Lexeme {

    /**
     * The literal name of this lexeme (i.e., "ArrayInt")
     */
    protected final String literal;

    /**
     * TODO
     *
     * @param literal   TODO
     */
    public Lexeme(String literal) {
        this.literal = literal;
    }

    // TODO: eventually just use constructLexemeWithDFA or include in constructor

    /**
     * TODO
     *
     * @return
     */
    public abstract DFA getDFA();

    /**
     * TODO
     *
     * @param token TODO
     *
     * @return
     */
    public abstract String stringify(String token);

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lexeme lexeme = (Lexeme) o;

        return literal != null ? literal.equals(lexeme.literal) : lexeme.literal == null;
    }

    @Override
    public int hashCode() {
        return literal != null ? literal.hashCode() : 0;
    }
}
