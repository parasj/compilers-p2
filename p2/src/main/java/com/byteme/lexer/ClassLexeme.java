package com.byteme.lexer;

public abstract class ClassLexeme extends Lexeme {

    /**
     * The string 's' is the content of the class lexeme (i.e., the name of a floatlit variable)
     */
    private final String s;

    public ClassLexeme(String literal, String s) {
        super(literal);

        this.s = s;
    }

    // TODO replace with the concept of "tokens"
    // TODO Tokens have a Lexeme that they match with
    // TODO We just generate the DFAs for the lexemes once and assign them to each Token we want to match
    public abstract ClassLexeme newClassLexemeWithS(String s);

    @Override
    public String toString() {
        return s + ":" + super.literal;
    }
}
