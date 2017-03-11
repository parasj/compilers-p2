package com.byteme.lexer;

/**
 * A Token is simply some input which has been accepted by some Lexeme.
 */
public class Token {

    /**
     * The Lexeme that matches this Token.
     */
    private final Lexeme lexeme;

    /**
     * The actual String that this Token consists of.
     *
     * This value should be matched by the Lexeme.
     */
    private final String value;

    /**
     * Constructs a new Token with the specified Lexeme and Value.
     *
     * @param lexeme    TODO
     * @param value     TODO
     */
    public Token(Lexeme lexeme, String value) {
        this.lexeme = lexeme;
        this.value = value;
    }

    /**
     * TODO
     *
     * @return
     */
    public Lexeme getLexeme() {
        return lexeme;
    }

    /**
     * TODO
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return lexeme.toString() + ":" + value;
    }
}
