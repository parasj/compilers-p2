package com.byteme.frontend.parser;

import com.byteme.frontend.grammar.ProductionRule;
import com.byteme.frontend.lexer.Token;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * p2
 */
public class ASTNodeNonterminal implements ASTNode {
    private Deque<ASTNode> children;
    private ProductionRule productionRule;
    private Token token;

    public ASTNodeNonterminal(ProductionRule productionRule, List<ASTNode> children) {
        this.children = new ArrayDeque<>(children);
        this.productionRule = productionRule;
    }

    public List<ASTNode> getChildren() {
        return new ArrayList<>(children);
    }

    public void pushChild(ASTNode child) {
        children.push(child);
    }

    public void removeChild(ASTNode child) {
        children.remove(child);
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public ProductionRule getProductionRule() {
        return productionRule;
    }

    public void setChildren(Deque<ASTNode> children) {
        this.children = children;
    }

    public void setProductionRule(ProductionRule productionRule) {
        this.productionRule = productionRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ASTNodeNonterminal that = (ASTNodeNonterminal) o;

        if (children != null ? !children.equals(that.children) : that.children != null) return false;
        return productionRule != null ? productionRule.equals(that.productionRule) : that.productionRule == null;

    }

    @Override
    public int hashCode() {
        int result = children != null ? children.hashCode() : 0;
        result = 31 * result + (productionRule != null ? productionRule.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ASTNodeNonterminal{" +
                "children=" + children +
                ", productionRule=" + productionRule +
                '}';
    }


    @Override
    public String toSExpression(int level) {
        StringBuilder delim = new StringBuilder("\n");
        IntStream.range(0, level + 1).forEach(x -> delim.append("\t"));

        List<String> childrenList = children.stream().map(x -> x.toSExpression(level + 1)).collect(Collectors.toList());
        String childStr = String.join(delim, childrenList);
        String s = String.format("(%s[%s] %s%s)", productionRule.getHeadNonTerminal().getName(), productionRule, delim, childStr);
        return s;
    }
}
