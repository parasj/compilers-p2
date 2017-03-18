package com.byteme.frontend.parser;

/**
 * p2
 */
public abstract class ASTNode {
    private ASTNode parent;

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public abstract String toSExpression(int i);
}
