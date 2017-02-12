package com.byteme.scanner;

import java.util.*;
import java.util.stream.IntStream;

/**
 * src
 */
public class DFA {
    private Map<Integer, Map<Character, Integer>> transitions;
    private Set<Integer> accepting;
    private final int initial = 0;

    public DFA(Map<Integer, Map<Character, Integer>> transitions, Set<Integer> accepting) {
        this.transitions = transitions;
        this.accepting = accepting;
    }

    public boolean accepts(String s) {
        return accepts(initial, s);
    }

    private boolean accepts(int state, String str) {
        // dead state -> FALSE
        if (state < 0)
            return false;

        // empty string -> state in accepting
        if (str == null || str.length() == 0)
            return accepting.contains(state);

        // recurse on remainder of input
        Map<Character, Integer> delta = transitions.getOrDefault(str.charAt(0), Collections.emptyMap());
        Integer statePrime = delta.get(str.charAt(0));
        String remaining = (str.length() > 0) ? str.substring(1) : "";
        return accepts(statePrime, remaining);
    }
}
