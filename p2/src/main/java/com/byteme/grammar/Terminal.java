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
    //I need this
    public Lexeme getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return this.lexeme.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Terminal terminal = (Terminal) o;

        return lexeme != null ? lexeme.equals(terminal.lexeme) : terminal.lexeme == null;
    }

    @Override
    public int hashCode() {
        return lexeme != null ? lexeme.hashCode() : 0;
    }
}
