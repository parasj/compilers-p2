package com.byteme.frontend.grammar.sets;

import com.byteme.frontend.grammar.NonTerminal;
import com.byteme.frontend.grammar.ProductionRule;
import com.byteme.frontend.grammar.Symbol;
import com.byteme.frontend.grammar.Terminal;
import com.byteme.frontend.lexer.KeywordLexeme;
import com.byteme.frontend.lexer.Lexeme;

import java.util.*;

/**
 * p2
 */
public class LL1ParseTable {
    private static final Lexeme lepsilon = new KeywordLexeme("");
    private static final Terminal tepsilon = new Terminal("", lepsilon);
    private final FirstSet first;
    private final FollowSet follow;
    private final List<ProductionRule> productionRules;
    private final Map<ParseTableKey, ProductionRule> parseTable;

    public LL1ParseTable(FirstSet first, FollowSet follow, List<ProductionRule> productionRules) {
        this.first = first;
        this.follow = follow;
        this.productionRules = productionRules;
        this.parseTable = buildTable();
    }

    public ProductionRule get(NonTerminal nt, Terminal t) {
        return parseTable.get(new ParseTableKey(nt, t));
    }

    private Map<ParseTableKey, ProductionRule> buildTable() {
        Map<ParseTableKey, ProductionRule> table = new HashMap<>();

        for (ProductionRule pr : productionRules) {
            NonTerminal A = pr.getHeadNonTerminal();
            Set<Terminal> Fiw = productionRuleToFirstSet(pr);
            Set<Terminal> Fow = follow.getHSet(A);

            for (Terminal a : Fiw) {
                ParseTableKey k = new ParseTableKey(A, a);
                assert !(table.containsKey(k) && !table.get(k).equals(pr)) : "Parse table conflict between rules " + table.get(k) + " and " + pr + " on terminal " + a;
                table.put(k, pr);
            }

            if (Fiw.contains(tepsilon)) {
                if (Fow != null) {
                    for (Terminal a : Fow) {
                        ParseTableKey k = new ParseTableKey(A, a);
                        assert !(table.containsKey(k) && !table.get(k).equals(pr)) : "(1) Parse table conflict between rules " + table.get(k) + " and " + pr + " on terminal " + a;
                        table.put(k, pr);
                    }
                }
            }
        }

        return table;
    }

    private Set<Terminal> productionRuleToFirstSet(ProductionRule pr) {
        HashSet<Terminal> f = new HashSet<>();

        for (Symbol s : pr.getDerivation()) {
            f.addAll(first.getHSet(s));
            if (!first.getHSet(s).contains(tepsilon)) {
                return f;
            }
        }

        return f;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LL1ParseTable{\n");

        for (ParseTableKey k : parseTable.keySet()) {
            sb.append(k.nt).append("\t").append(k.t).append("\t").append(parseTable.get(k)).append("\n");
        }

        sb.append("{");

        return sb.toString();
    }

    private class ParseTableKey {
        public final NonTerminal nt;
        public final Terminal t;

        public ParseTableKey(NonTerminal nt, Terminal t) {
            this.nt = nt;
            this.t = t;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ParseTableKey)) return false;
            ParseTableKey key = (ParseTableKey) o;
            return nt.equals(key.nt) && t.equals(key.t);
        }

        @Override
        public int hashCode() {
            int result = nt.hashCode();
            result = 31 * result + t.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "ParseTableKey{" +
                    "nt=" + nt +
                    ", t=" + t +
                    '}';
        }
    }
}
