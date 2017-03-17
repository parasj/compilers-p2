package com.byteme.frontend.grammar.sets;

import com.byteme.frontend.grammar.ProductionRule;
import com.byteme.frontend.grammar.Symbol;
import com.byteme.frontend.grammar.Terminal;
import com.byteme.frontend.lexer.KeywordLexeme;
import com.byteme.frontend.lexer.Lexeme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

import static com.byteme.TigerSpec.lexemes;

/**
 * Created by Rishav Bose on 3/12/2017.
 */
public final class FirstSet {

    private HashMap<Symbol, HashSet<Terminal>> firstSet;

    public FirstSet(LinkedList<ProductionRule> productionRules) {
        this.firstSet = generateFirstSet(productionRules);
    }


    public HashSet<Terminal> getHSet(Symbol s) {
        return firstSet.get(s);
    }

    /**
     * Generates the first set for the grammar.
     *
     * @return a Hash map of the firsts set
     */
    private HashMap<Symbol, HashSet<Terminal>> generateFirstSet(LinkedList<ProductionRule> productionRules) {
        this.firstSet = new HashMap<>();

        //Populate First sets for terminals
        for (Lexeme l : lexemes) {
            //TODO: Fix this hacky behavior
            Terminal t = new Terminal("NA", l);
            addToMap(this.firstSet, t, t);
        }

        Lexeme lepsilon = new KeywordLexeme("");
        Terminal tepsilon = new Terminal("", lepsilon);


        addToMap(this.firstSet, tepsilon, tepsilon);

        //Populate for epsilon
        for (ProductionRule pr : productionRules) {
            //If the first element is a terminal and epsilon
            //This should be covered by equals: pr.getDerivation().getFirst() instanceof Terminal
            if (pr.getDerivation().getFirst().equals(tepsilon)) {
                addToMap(this.firstSet, pr.getHeadNonTerminal(), (Terminal) pr.getDerivation().getFirst());
            }

//            } else if (pr.getDerivation().getFirst().getClass() == Terminal.class) {
//                addToMap(this.firstSet, pr.getHeadNonTerminal(), (Terminal) pr.getDerivation().getFirst());
//            }
        }

        //TODO: BROKEN AF
        //Add the last non-nullable symbol's First set
        boolean changing = true;
        while (changing) {
            changing = false;
            for (ProductionRule pr : productionRules) {
                int k = pr.getDerivation().size() - 1;
                for (int j = 0; j < pr.getDerivation().size(); j++) {
                    Symbol s = pr.getDerivation().get(j);
                    if (this.firstSet.get(s) == null || this.firstSet.get(s).isEmpty()
                            || !this.firstSet.get(s).contains(tepsilon)) {
                        k = j;
                        break;
                    }
                }

                HashSet<Terminal> rhs;

                if (firstSet.get(pr.getDerivation().get(0)) != null) {
                    rhs = new HashSet<>(firstSet.get(pr.getDerivation().get(0)));
                } else {
                    rhs = new HashSet<>();
                }

                if (rhs != null) {
                    rhs.remove(tepsilon);
                }


                int i = 0;
                while (firstSet.get(pr.getDerivation().get(i)) != null && firstSet.get(pr.getDerivation().get(i)).contains(tepsilon) && i <= k - 1) {
                    rhs.addAll(firstSet.get(pr.getDerivation().get(i + 1)));
                    rhs.remove(tepsilon);
                    i++;
                }
                if (i == k && firstSet.get(pr.getDerivation().get(k)) != null && firstSet.get(pr.getDerivation().get(k)).contains(tepsilon)) {
                    rhs.add(tepsilon);
                }
                if (this.firstSet.get(pr.getHeadNonTerminal()) == null || (rhs != null && !this.firstSet.get(pr.getHeadNonTerminal()).containsAll(rhs))) {
                    addAllToMap(this.firstSet, pr.getHeadNonTerminal(), rhs);
                    changing = true;
                }

            }
        }


        return this.firstSet;
    }

    private void addToMap(HashMap<Symbol, HashSet<Terminal>> map, Symbol key, Terminal... val) {
        HashSet<Terminal> list;
        if (map.get(key) != null) {
            list = map.get(key);
        } else {
            list = new HashSet<Terminal>();
        }
        for (Terminal v : val) {
            list.add(v);
        }
        map.put(key, list);
    }

    private void addAllToMap(HashMap<Symbol, HashSet<Terminal>> map, Symbol key, HashSet<Terminal> val) {
        if (key == null || val == null) {
            return;
        }
        for (Terminal t : val) {
            addToMap(map, key, t);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FirstList {\n");

        new TreeMap<>(firstSet).entrySet().stream()
                .filter(entry -> entry.getKey().getClass() != Terminal.class)
                .forEach(entry -> {
                    sb.append("\t").append(entry.getKey()).append(" : ");
                    entry.getValue().forEach(t -> sb.append(t.toString()).append(", "));
                    sb.append("\n");
                });

        sb.append("}\n");

        return sb.toString();
    }

    public HashMap<Symbol, HashSet<Terminal>> getFirstSet() {
        return firstSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FirstSet firstSet1 = (FirstSet) o;

        return firstSet != null ? firstSet.equals(firstSet1.firstSet) : firstSet1.firstSet == null;
    }

    @Override
    public int hashCode() {
        return firstSet != null ? firstSet.hashCode() : 0;
    }


}
