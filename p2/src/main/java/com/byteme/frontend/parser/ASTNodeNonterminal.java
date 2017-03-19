package com.byteme.frontend.parser;

import com.byteme.frontend.grammar.ProductionRule;
import com.byteme.frontend.lexer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * p2
 */
public class ASTNodeNonterminal extends ASTNode {
    private List<ASTNode> children;
    private ProductionRule productionRule;
    private Token token;

    public ASTNodeNonterminal(ProductionRule productionRule, List<ASTNode> children) {
        this.children = new ArrayList<>(children);
        this.productionRule = productionRule;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public void setChildren(List<ASTNode> children) {
        this.children = children;
    }

    public void pushChild(ASTNode child) {
        children.add(0, child);
        child.setParent(this);
    }

    public void removeChild(ASTNode child) {
        children.remove(child);
        child.setParent(null);
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

        List<String> childrenList =
                children.stream().map(x -> x.toSExpression(level + 1))
                        .map(String::trim)
                        .filter(x -> x.length() > 0)
                        .collect(Collectors.toList());

        String childStr = String.join(" ", childrenList); // delim

        String s;
        if (childStr.length() > 0) {
            s = String.format("(%s %s)", productionRule.getHeadNonTerminal().getName(), childStr);
        } else {
            s = String.format("%s", productionRule.getHeadNonTerminal().getName());
        }
        return s;
    }
}
