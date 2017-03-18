package com.byteme.frontend.lexer;

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
     * <p>
     * This value should be matched by the Lexeme.
     */
    private final String value;

    private final int line;
    private final int linePosition;

    /**
     * Constructs a new Token with the specified Lexeme and Value.
     *  @param lexeme TODO
     * @param value  TODO
     * @param line
     * @param linePosition
     */
    public Token(Lexeme lexeme, String value, int line, int linePosition) {
        this.lexeme = lexeme;
        this.value = value;
        this.line = line;
        this.linePosition = linePosition;
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

    public int getLine() {
        return line;
    }

    public int getLinePosition() {
        return linePosition;
    }

    @Override
    public String toString() {
        return lexeme.toString() + ":" + value;
    }
}
