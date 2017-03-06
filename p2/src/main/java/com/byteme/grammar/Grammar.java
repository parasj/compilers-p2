package com.byteme.grammar;

import java.util.LinkedList;

/**
 * The Grammar class describes a context-free grammar.
 *
 * A context-free grammar, in formal language theory, is a collection of
 * production rules that describe all the possible strings in a given formal
 * language (source: Wikipedia).
 */
public final class Grammar {

    private final LinkedList<ProductionRule> productionRules;

    /**
     * Constructs a Grammar and initializes it with the provided production
     * rules.
     *
     * @param   productionRules -   a list of any initial ProductionRules to
     *                              include in this Grammar
     */
    public Grammar(ProductionRule ... productionRules) {
        this.productionRules = new LinkedList<>();

        // add initial production rules to list
        for (ProductionRule pr : productionRules) {
            this.productionRules.addLast(pr);
        }
    }

    /**
     * Adds a new ProductionRule to the Grammar.
     *
     * @param   productionRule  -   the ProductionRule to add to this Grammar
     */
    public void addProductionRule(ProductionRule productionRule) {
        productionRules.addLast(productionRule);
    }

    @Override
    public String toString() {
        int index = 0;
        StringBuilder sb = new StringBuilder("grammar {\n");

        for (ProductionRule pr : productionRules) {
            sb.append("(" + (index++) + ")\t" + pr + "\n");
        }

        sb.append("}");

        return sb.toString();
    }
}
