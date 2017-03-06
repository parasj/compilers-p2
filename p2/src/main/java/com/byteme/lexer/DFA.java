package com.byteme.lexer;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * src
 */
public class DFA {
    public static final int DFA_DEAD = -2;
    public static final int DFA_ACCEPT = 1;
    public static final int DFA_REJECT = -1;
    private final int initial = 0;
    private Map<Integer, Map<Character, Integer>> transitions;
    private Set<Integer> accepting;

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
            Map<Character, Integer> delta = transitions.getOrDefault(state, Collections.emptyMap());
            Integer statePrime = delta.get(str.charAt(0));
            String remaining = (str.length() > 0) ? str.substring(1) : "";
            return evaluate(statePrime, remaining);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph dfa {\n\tnode [shape=\"circle\"];\n");

        // accepting
        sb.append("\t{node [shape = doublecircle];");
        for (int accept : accepting) {
            sb.append(String.format(" q_%d", accept));
        }
        sb.append(";}\n");

        // edges
        for (int fromState : transitions.keySet()) {
            Map<Character, Integer> t = transitions.get(fromState);
            for (char transition : t.keySet()) {
                int toState = t.get(transition);
                String line = String.format("q_%d -> q_%d [ label = \"%c\" ];", fromState, toState, transition);

                sb.append("\t");
                sb.append(line);
                sb.append("\n");
            }
        }

        sb.append("}\n");

        return sb.toString();
    }
}
