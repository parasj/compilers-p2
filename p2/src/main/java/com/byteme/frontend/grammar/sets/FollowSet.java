package com.byteme.frontend.grammar.sets;

import com.byteme.frontend.grammar.NonTerminal;
import com.byteme.frontend.grammar.ProductionRule;
import com.byteme.frontend.grammar.Symbol;
import com.byteme.frontend.grammar.Terminal;
import com.byteme.frontend.lexer.KeywordLexeme;
import com.byteme.frontend.lexer.Lexeme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Rishav Bose on 3/12/2017.
 */
public class FollowSet {
    private HashMap<Symbol, HashSet<Terminal>> followSet;

    public FollowSet(LinkedList<ProductionRule> productionRules, FirstSet firstSet) {
        this.followSet = generateFollowSet(productionRules, firstSet);
    }

    public HashSet<Terminal> getHSet(Symbol s) {
        return followSet.get(s);
    }

    private HashMap<Symbol, HashSet<Terminal>> generateFollowSet(LinkedList<ProductionRule> productionRules, FirstSet fs) {
        followSet = new HashMap<>();
        HashMap<Symbol, HashSet<Terminal>> firstSet = fs.getFirstSet();

        Lexeme lepsilon = new KeywordLexeme("");
        Terminal tepsilon = new Terminal("", lepsilon);

        Lexeme leof = new KeywordLexeme("end");
        Terminal teof = new Terminal("end", leof);
        
        addToMap(followSet, productionRules.getFirst().getHeadNonTerminal(), teof);


        boolean changing = true;
        while (changing) {
            changing = false;
            for (ProductionRule pr : productionRules) {
                HashSet<Terminal> trailer;

                if (followSet.get(pr.getHeadNonTerminal()) != null) {
                    trailer = new HashSet<>(followSet.get(pr.getHeadNonTerminal()));
                } else {
                    trailer = new HashSet<>();
                }


                for (int i = pr.getDerivation().size() - 1; i >= 0; i--) {
                    if (pr.getDerivation().get(i).getClass() == NonTerminal.class) {
                        if (followSet.get(pr.getDerivation().get(i)) == null && !trailer.isEmpty()) {
                            followSet.put(pr.getDerivation().get(i), new HashSet<>(trailer));
                            changing = true;
                        } else if (followSet.get(pr.getDerivation().get(i)) != null && !trailer.isEmpty() && !followSet.get(pr.getDerivation().get(i)).containsAll(trailer)) {
                            addAllToMap(followSet, pr.getDerivation().get(i), trailer);
                            changing = true;
                        }

                        if (firstSet.get(pr.getDerivation().get(i)).contains(tepsilon)) {
                            HashSet<Terminal> temp = new HashSet<>(firstSet.get(pr.getDerivation().get(i)));
                            temp.remove(tepsilon);
                            trailer.addAll(temp);
                        } else {
                            HashSet<Terminal> temp = new HashSet<>(firstSet.get(pr.getDerivation().get(i)));
                            trailer = temp;
                        }
                    } else {
                        if (firstSet.get(pr.getHeadNonTerminal()) != null) {
                            trailer = new HashSet<>(firstSet.get(pr.getDerivation().get(i)));
                        } else {
                            trailer = new HashSet<>();
                        }
                    }
                }
            }

        }

        return followSet;
    }


    private void addToMap(HashMap<Symbol, HashSet<Terminal>> map, Symbol key, Terminal val) {
        HashSet<Terminal> list;
        if (map.get(key) != null) {
            list = map.get(key);
        } else {
            list = new HashSet<Terminal>();
        }

        list.add(val);
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

    public HashMap<Symbol, HashSet<Terminal>> getFollowSet() {
        return followSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FollowSet followSet1 = (FollowSet) o;

        return followSet != null ? followSet.equals(followSet1.followSet) : followSet1.followSet == null;
    }

    @Override
    public int hashCode() {
        return followSet != null ? followSet.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FollowList {\n");

//        if (this.followSet == null) {
//            System.out.println("Empty");
//        }

        for (Map.Entry<Symbol, HashSet<Terminal>> entry : followSet.entrySet()) {
            if (entry.getKey().getClass() != Terminal.class) {
                sb.append("\t" + entry.getKey().toString() + " : ");
                for (Terminal t : entry.getValue()) {
                    sb.append(t.toString() + ", ");
                }
                sb.append("\n");
            }
        }

        sb.append("}\n");

        return sb.toString();
    }

}
