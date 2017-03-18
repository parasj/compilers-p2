package com.byteme.frontend.parser;

import com.byteme.frontend.lexer.Token;

import java.util.stream.IntStream;

/**
 * p2
 */
public class ASTNodeTerminal implements ASTNode {
    private Token token;

    public ASTNodeTerminal(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASTNodeTerminal that = (ASTNodeTerminal) o;

        return token.equals(that.token);

    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    @Override
    public String toString() {
        return "ASTNodeTerminal{" +
                "token=" + token +
                '}';
    }

    @Override
    public String toSExpression(int level) {
        if (token == null)
            return "NULL";
        return token.getValue();
    }
}
