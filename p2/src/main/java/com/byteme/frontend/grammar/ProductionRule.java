package com.byteme.frontend.grammar;

import java.util.LinkedList;

/**
 * In the scope of context-free grammars, a production rule is some collection
 * of symbols which a given non-terminal symbol may derive as.
 * <p>
 * A production rule takes the form A -> s, where A is the head non-terminal
 * symbol, and s is the collection of symbols which A can derive as.
 * <p>
 * For example, an assignment might be derived as a variable, the assignment
 * operator, another variable, an arithmetic operator, and one last variable.
 * <p>
 * We could describe this in terms of a context-free grammar as a production
 * rule:
 * <p>
 * assignment -> variable assignOp variable arithmeticOp variable ;
 */
public final class ProductionRule {

    /**
     * The NonTerminal Symbol whose derivation is defined by this
     * ProductionRule.
     */
    private NonTerminal headNonTerminal;

    /**
     * An ordered list of Symbols that this ProductionRule derives as.
     * <p>
     * The LinkedList itself is not immutable, only the reference is final.
     */
    private final LinkedList<Symbol> derivation;

    /**
     * Constructs a ProductionRule (of the form A -> s).
     *
     * @param headNonTerminal -   the NonTerminal at the head of this
     *                        ProductionRule
     * @param derivation      -   the Symbols that the head NonTerminal
     *                        derives as
     */
    public ProductionRule(NonTerminal headNonTerminal, Symbol... derivation) {
        this.headNonTerminal = headNonTerminal;
        this.derivation = new LinkedList<>();

        // construct list of Symbols in the derivation for this rule
        for (Symbol s : derivation) {
            this.derivation.addLast(s);
        }
    }

    /**
     * Returns this production rule's head non-terminal symbol. If we consider
     * a production rule in the form A -> s, the head non-terminal is "A."
     *
     * @return this ProductionRule's head NonTerminal.
     */
    public NonTerminal getHeadNonTerminal() {
        return headNonTerminal;
    }

    public void setHeadNonTerminal(NonTerminal headNonTerminal) {
        this.headNonTerminal = headNonTerminal;
    }

    /**
     * Returns this production rule's derivation.
     *
     * @return this ProductionRule's Symbol derivation in a LinkedList.
     */
    public LinkedList<Symbol> getDerivation() {
        return derivation;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(headNonTerminal.toString());
        final Symbol lastSymbol = derivation.getLast();

        sb.append(" -> ");

        for (Symbol s : derivation) {
            sb.append(s.toString());

            // append whitespace if not last symbol (reference-equivalence)
            if (s != lastSymbol) sb.append(" ");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductionRule that = (ProductionRule) o;

        if (headNonTerminal != null ? !headNonTerminal.equals(that.headNonTerminal) : that.headNonTerminal != null)
            return false;
        return derivation != null ? derivation.equals(that.derivation) : that.derivation == null;
    }

    @Override
    public int hashCode() {
        int result = headNonTerminal != null ? headNonTerminal.hashCode() : 0;
        result = 31 * result + (derivation != null ? derivation.hashCode() : 0);
        return result;
    }
}
