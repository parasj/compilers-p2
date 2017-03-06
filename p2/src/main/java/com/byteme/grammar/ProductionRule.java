package com.byteme.grammar;

import java.util.LinkedList;

/**
 * In the scope of context-free grammars, a production rule is some collection
 * of symbols that can be derived from a conceptual class, or "rule."
 *
 * A production rule takes the form A -> s, where A is the rule itself, and s
 * is the collection of symbols which A can derive as.
 *
 * For example, an assignment might be derived as a variable, the assignment
 * operator, another variable, an arithmetic operator, and one last variable.
 *
 * We could describe this in terms of a context-free grammar as a production
 * rule:
 *
 *      assignment -> variable assignOp variable arithmeticOp variable ;
 */
public final class ProductionRule extends NonTerminal {

    /**
     * An ordered list of Symbols that this ProductionRule derives as.
     *
     * The LinkedList itself is not immutable, only the reference is final.
     */
    protected final LinkedList<Symbol> derivation;

    /**
     * Constructs a ProductionRule (of the form A -> s).
     *
     * @param   name    - the (String) name of this production rule
     * @param   symbols - the Symbols that this production rule derives as
     */
    public ProductionRule(String name, Symbol ... symbols) {
        super(name);

        this.derivation = new LinkedList<>();

        // construct list of Symbols in the derivation for this rule
        for (Symbol s : symbols) {
            this.derivation.addLast(s);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.name + " -> ");
        final Symbol lastSymbol = derivation.getLast();

        for (Symbol s : derivation) {
            sb.append(s);

            // append whitespace if not last symbol (reference-equivalence)
            if (s != lastSymbol) sb.append(" ");
        }

        return sb.toString();
    }
}
