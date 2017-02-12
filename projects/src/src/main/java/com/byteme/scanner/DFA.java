package com.byteme.scanner;

import java.util.*;

/**
 * src
 */
public class DFA {
    private Map<Integer, Map<Character, Integer>> transitions;
    private Set<Integer> accepting;
    private final int initial = 0;

    private static final int DFA_DEAD = -2;
    private static final int DFA_ACCEPT = 1;
    private static final int DFA_REJECT = -1;

    public DFA(Map<Integer, Map<Character, Integer>> transitions, Set<Integer> accepting) {
        this.transitions = transitions;
        this.accepting = accepting;
    }

    public int evaluate(String s) {
        return evaluate(initial, s);
    }

    private int evaluate(int state, String str) {
        if (state < 0) { // dead state -> FALSE
            return DFA_DEAD;
        } else if (str == null || str.length() == 0) { // empty string -> state in accepting
            return (accepting.contains(state)) ? DFA_ACCEPT : DFA_REJECT;
        } else { // recurse on remainder of input
            Map<Character, Integer> delta = transitions.getOrDefault(str.charAt(0), Collections.emptyMap());
            Integer statePrime = delta.get(str.charAt(0));
            String remaining = (str.length() > 0) ? str.substring(1) : "";
            return evaluate(statePrime, remaining);
        }
    }
}
