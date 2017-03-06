package com.byteme.grammar;

import com.byteme.lexer.*;

/**
 * Created by ebuntu on 3/5/17.
 */
public final class Terminal extends Symbol {

    private final Lexeme lexeme;

    /**
     * Constructs a new Terminal with the specified name and Lexeme.
     *
     * @param   name    - the (String) name of this Terminal
     * @param   lexeme  - the Lexeme that matches this Terminal
     */
    public Terminal(String name, Lexeme lexeme) {
        super(name);

        this.lexeme = lexeme;
    }

    // TODO - do we want to reveal a Terminal's Lexeme? or just provide a tryMatch method?
//    public Lexeme getLexeme() {
//        return lexeme;
//    }

    @Override
    public String toString() {
        return this.name.toUpperCase();
    }
}
