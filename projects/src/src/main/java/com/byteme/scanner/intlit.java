package com.byteme.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * src
 */
public class intlit implements ScannerToken {
    private final DFA dfa;

    public intlit() {
        this.dfa = constructDFA();
    }

    private DFA constructDFA() {
        Map<Integer, Map<Character, Integer>> table = new HashMap<>();
        Set<Integer> accept = new HashSet<>();

//        int lastState = 0;
//        int nextState = 1;
//
//        for (char c : literal.toCharArray()) {
//            Map<Character, Integer> transitions = table.getOrDefault(lastState, new HashMap<>());
//            transitions.put(c, nextState);
//            table.put(lastState, transitions);
//            lastState = nextState++;
//        }
//
//        accept.add(lastState);

        Map<Character, Integer> transitions = new HashMap<Character, Integer>();
        for (int i = 0; i < 128; i++) {
            if (i <= '9' && i >= '1') {
                transitions.put((char)(i), 1);
            } else if (i == '0') {
                transitions.put((char)(i), 2);
            } else {
                transitions.put((char)(i), -1);
            }
        }
        table.put(0, transitions);


        accept.add(1);
        accept.add(2);

        return new DFA(table, accept);
    }


    @Override
    public DFA getDFA() {
        return dfa;
    }

    @Override
    public String toString() {
        return "intlit " +
                "dfa=\n" + dfa;
    }
}
