package com.byteme.frontend.grammar;

import com.byteme.frontend.grammar.sets.FirstSet;
import com.byteme.frontend.grammar.sets.FollowSet;
import com.byteme.frontend.grammar.sets.LL1ParseTable;

import java.util.Hashtable;
import java.util.LinkedList;

/**
 * The Grammar class describes a context-free grammar.
 * <p>
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

    /**
     * A language for some NonTerminal is the set of all ProductionRules with it as its head
     * NonTerminal.
     */
    private final Hashtable<NonTerminal, LinkedList<ProductionRule>> languages;

    private FirstSet firstSet;
    private FollowSet followSet;
    private LL1ParseTable parseTable;

    /**
     * Constructs a Grammar and initializes it with the provided production
     * rules.
     *
     * @param productionRules -   a list of any initial ProductionRules to
     *                        include in this Grammar
     */
    public Grammar(ProductionRule... productionRules) {
        this.productionRules = new LinkedList<>();
        this.languages = new Hashtable<>();

        // Left factor

        for (ProductionRule pr : productionRules) {
            NonTerminal headNonTerminal = pr.getHeadNonTerminal();


            this.productionRules.addLast(pr);
        }

        // add initial production rules to list
        for (ProductionRule pr : this.productionRules) {
            NonTerminal headNonTerminal = pr.getHeadNonTerminal();
            LinkedList<ProductionRule> language = languages.getOrDefault(
                    headNonTerminal, new LinkedList<>()
            );

            // Update list of ProductionRules linked to this NonTerminal
            language.addLast(pr);

            // Update languages. Even though we modify ref, we must add in case it was not present
            this.languages.put(headNonTerminal, language);
        }


        this.firstSet = new FirstSet(this.productionRules);
        this.followSet = new FollowSet(this.productionRules, this.firstSet);
        this.parseTable = new LL1ParseTable(this.firstSet, this.followSet, this.productionRules);
    }


    /**
     * Returns this grammar's production rules.
     *
     * @return a LinkedList of this Grammar's ProductionRules.
     */
    public LinkedList<ProductionRule> getProductionRules() {
        // TODO: Note that this directly returns the LinkedList reference, so it may be altered by someone else!
        return productionRules;
    }

    public Hashtable<NonTerminal, LinkedList<ProductionRule>> getLanguages() {
        return languages;
    }

    public FirstSet getFirstSet() {
        return firstSet;
    }

    public FollowSet getFollowSet() {
        return followSet;
    }

    public LL1ParseTable getParseTable() {
        return parseTable;
    }

    @Override
    public String toString() {
        int index = 0;
        StringBuilder sb = new StringBuilder("grammar {\n");

        for (ProductionRule pr : productionRules) {
            sb.append("\t(" + (++index) + ")\t" + pr + "\n");
        }

        sb.append("}\n");

        // Use to see the FirstSet/FollowSet
        sb.append(firstSet.toString());
        sb.append(followSet.toString());
        sb.append(parseTable.toString());

        return sb.toString();
    }
}
