package com.byteme.grammar.sets;

import com.byteme.grammar.ProductionRule;
import com.byteme.grammar.Symbol;
import com.byteme.grammar.Terminal;

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

    public HashSet<Terminal> get(Symbol s) {
        return followSet.get(s);
    }

    private HashMap<Symbol, HashSet<Terminal>> generateFollowSet(LinkedList<ProductionRule> productionRules, FirstSet fs) {
        HashMap<Symbol, HashSet<Terminal>> firstSet = fs.getFirstSet();



        return null;
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
        StringBuilder sb = new StringBuilder("FirstList {\n");

        for (Map.Entry<Symbol, HashSet<Terminal>> entry: followSet.entrySet()) {
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
