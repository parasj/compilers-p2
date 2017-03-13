package com.byteme.grammar;

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
    private HashMap<Symbol, HashSet<Terminal>> firstSet;

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

        firstSet = generateFirstSet();
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

    /**
     * Generates the first set for the grammar.
     *
     * @return a Hash map of the firsts set
     */
    private HashMap<Symbol, HashSet<Terminal>> generateFirstSet() {
        this.firstSet = new HashMap<Symbol, HashSet<Terminal>>();

        //Populate First Sets for terminals
        for(Lexeme l : lexemes) {
            //TODO: Fix this hacky behavior
            Terminal t = new Terminal("NA",l);
            addToMap(this.firstSet,t,t);
        }

        Lexeme  lepsilon = new KeywordLexeme("");
        Terminal tepsilon = new Terminal("", lepsilon);

        for (ProductionRule pr : productionRules) {
            //If the first element is a terminal and epsilon
            //This should be covered by equals: pr.getDerivation().getFirst() instanceof Terminal
            if (pr.getDerivation().getFirst().equals(tepsilon)) {
                addToMap(this.firstSet, pr.getHeadNonTerminal(), tepsilon);
            }


        }



        return firstSet;
    }

    private void addToMap(HashMap<Symbol, HashSet<Terminal>> map, Symbol key, Terminal val){
        HashSet<Terminal> list;
        if(map.get(key) != null){
            list = map.get(key);
        } else {
            list = new HashSet<Terminal>();
        }
        list.add(val);
        map.put(key,list);
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
