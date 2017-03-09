package com.byteme.lexer.classes;

import com.byteme.lexer.ClassLexeme;
import com.byteme.lexer.DFA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntlitClassLexeme extends ClassLexeme {
    private static final String LITERAL = "intlit";

    private final DFA dfa;

    public IntlitClassLexeme() {
        super(LITERAL);

        this.dfa = constructDFA();
    }

    private DFA constructDFA() {
        Map<Integer, Map<Character, Integer>> table = new HashMap<>();
        Set<Integer> accept = new HashSet<>();

        //For State 0
        Map<Character, Integer> transitions = new HashMap<Character, Integer>();
        for (int i = 0; i < 128; i++) {
            if (i <= '9' && i >= '1') {
                transitions.put((char) (i), 1);
            } else if (i == '0') {
                transitions.put((char) (i), 2);
            } else {
                transitions.put((char) (i), -1);
            }
        }
        table.put(0, transitions);

        //For state 1
        transitions = new HashMap<Character, Integer>();
        for (int i = 0; i < 128; i++) {
            if (i <= '9' && i >= '1') {
                transitions.put((char) (i), 1);
            } else if (i == '0') {
                transitions.put((char) (i), 1);
            } else {
                transitions.put((char) (i), -1);
            }
        }
        table.put(1, transitions);

        //For state 2
        transitions = new HashMap<Character, Integer>();
        for (int i = 0; i < 128; i++) {
            if (i <= '9' && i >= '1') {
                transitions.put((char) (i), -1);
            } else if (i == '0') {
                transitions.put((char) (i), -1);
            } else {
                transitions.put((char) (i), -1);
            }
        }
        table.put(2, transitions);


        accept.add(1);
        accept.add(2);

        return new DFA(table, accept);
    }

    @Override
    public DFA getDFA() {
        return dfa;
    }

    @Override
    public String stringify(String token) {
        return token + ":" + literal;
    }
}
