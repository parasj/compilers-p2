package com.byteme.grammar;

import com.byteme.grammar.Sets.FirstSet;
import com.byteme.lexer.KeywordLexeme;
import com.byteme.lexer.Lexeme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.HashMap;

import static com.byteme.Main.lexemes;

/**
 * The Grammar class describes a context-free grammar.
 *
 * A context-free grammar, in formal language theory, is a collection of
 * production rules that describe all the possible strings in a given formal
 * language (source: Wikipedia).
 */
public final class Grammar {

    /**
     * Note: the first rule in this list is considered the rule with our
     * grammar's start symbol.
     */
    private final LinkedList<ProductionRule> productionRules;
    private FirstSet firstSet;

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

        this.firstSet = new FirstSet(this.productionRules);
    }

    /**
     * Adds a new production rule to the grammar.
     *
     * @param   productionRule  -   the ProductionRule to add to this Grammar
     */
    public void addProductionRule(ProductionRule productionRule) {
        productionRules.addLast(productionRule);
    }

    /**
     * Returns this grammar's production rules.
     *
     * @return  a LinkedList of this Grammar's ProductionRules.
     */
    public LinkedList<ProductionRule> getProductionRules() {
        // TODO: Note that this directly returns the LinkedList reference, so it may be altered by someone else!
        return productionRules;
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
