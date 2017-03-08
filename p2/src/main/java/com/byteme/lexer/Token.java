package com.byteme.lexer;

/**
 * TODO
 *
 * Created by ebuntu on 3/8/17.
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
     * @param lexeme TODO
     * @param value TODO
     */
    public Token(Lexeme lexeme, String value) {
        this.lexeme = lexeme;
        this.value = value;
    }

}
