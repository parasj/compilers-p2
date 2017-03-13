package com.byteme.grammar.Sets;

import com.byteme.grammar.ProductionRule;
import com.byteme.grammar.Symbol;
import com.byteme.grammar.Terminal;
import com.byteme.lexer.KeywordLexeme;
import com.byteme.lexer.Lexeme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import static com.byteme.Main.lexemes;

/**
 * Created by Rishav Bose on 3/12/2017.
 */
public final class FirstSet {

    private HashMap<Symbol, HashSet<Terminal>> firstSet;

    public FirstSet(LinkedList<ProductionRule> productionRules) {
        this.firstSet = generateFirstSet(productionRules);
    }


    /**
     * Generates the first set for the grammar.
     *
     * @return a Hash map of the firsts set
     */
    private HashMap<Symbol, HashSet<Terminal>> generateFirstSet(LinkedList<ProductionRule> productionRules) {
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
        //Add the last non-nullable symbol's First set
        for (ProductionRule pr : productionRules) {
            if (!pr.getDerivation().getFirst().equals(tepsilon)) {
                int i = pr.getDerivation().size() - 1;
                for (int j = 0; j < pr.getDerivation().size(); j++) {
                    Symbol s = pr.getDerivation().get(j);
                    if (this.firstSet.get(s) == null || this.firstSet.get(s).isEmpty()
                            || !this.firstSet.get(s).contains(tepsilon)) {
                        i = j;
                        break;
                    }
                }

                addToMap(this.firstSet, pr.getHeadNonTerminal(), (Terminal[]) this.firstSet.get(pr.getDerivation().get(i)).toArray());
            }
        }


        return firstSet;
    }

    private void addToMap(HashMap<Symbol, HashSet<Terminal>> map, Symbol key, Terminal ... val){
        HashSet<Terminal> list;
        if(map.get(key) != null){
            list = map.get(key);
        } else {
            list = new HashSet<Terminal>();
        }
        for (Terminal v : val) {
            list.add(v);
        }
        map.put(key,list);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FirstList {\n");

        for (Map.Entry<Symbol, HashSet<Terminal>> entry: firstSet.entrySet()) {
            sb.append(entry.getKey().toString() + " : ");
            for (Terminal t : entry.getValue()) {
                sb.append(t.toString() + ", ");
            }
            sb.append("\n");
        }

        sb.append("}");

        return sb.toString();
    }
}
