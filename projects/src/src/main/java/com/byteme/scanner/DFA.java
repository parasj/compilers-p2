package com.byteme.scanner;

import java.util.*;
import java.util.stream.IntStream;

/**
 * src
 */
public class DFA {
    private Map<DFAState, Map<Character, DFAState>> transitions;
    private Set<DFAState> accepting;
    private DFAState initial;

    public boolean accepts(String s) {
        DFAState curr = initial;
        return accepts(initial, s);
    }

    private boolean accepts(DFAState state, String str) {
        // dead state -> FALSE
        if (state == null)
            return false;

        // empty string -> state in accepting
        if (str == null || str.length() == 0)
            return accepting.contains(state);

        // recurse on remainder of input
        Map<Character, DFAState> delta = transitions.getOrDefault(str.charAt(0), Collections.emptyMap());
        DFAState statePrime = delta.get(str.charAt(0));
        String remaining = (str.length() > 0) ? str.substring(1) : "";
        return accepts(statePrime, remaining);
    }
}
