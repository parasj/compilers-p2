package com.byteme.scanner;

import java.util.*;

/**
 * src
 */
public class DFA {
    private Map<DFAState, Map<Character, DFAState>> transitions;
    private Set<DFAState> accepting;
    private DFAState initial;

    public boolean accepts(String s) {
        char[] chars = s.toCharArray();
        return accepts(Arrays.asList(chars));
    }

    public boolean accepts(List<Character> s) {

    }
}
