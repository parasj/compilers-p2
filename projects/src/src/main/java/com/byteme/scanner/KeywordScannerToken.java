package com.byteme.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * src
 */
public class KeywordScannerToken implements ScannerToken {
    private final String literal;
    private final DFA dfa;

    public KeywordScannerToken(String literal) {
        this.literal = literal;
        this.dfa = constructDFA(literal);
    }

    private DFA constructDFA(String literal) {
        Map<Integer, Map<Character, Integer>> table = new HashMap<>();
        Set<Integer> accept = new HashSet<>();

        int lastState = 0;
        int nextState = 1;

        for (char c : literal.toCharArray()) {
            Map<Character, Integer> transitions = table.getOrDefault(lastState, new HashMap<>());
            transitions.put(c, nextState);
            table.put(lastState, transitions);
            lastState = nextState++;
        }

        accept.add(lastState);

        return new DFA(table, accept);
    }


    @Override
    public DFA getDFA() {
        return dfa;
    }

    @Override
    public String toString() {
        return "KeywordScannerToken: " +
                "literal='" + literal + '\'' +
                ", dfa=\n" + dfa;
    }
}
