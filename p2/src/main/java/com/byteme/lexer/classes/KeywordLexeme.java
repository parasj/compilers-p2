package com.byteme.lexer.classes;

import com.byteme.lexer.DFA;
import com.byteme.lexer.Lexeme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * src
 */
public class KeywordLexeme extends Lexeme {
    private final String literal;
    private final DFA dfa;

    public KeywordLexeme(String literal) {
        super(literal);

        this.literal = literal;
        this.dfa = constructDFA(literal);
    }

    private static DFA constructDFA(String literal) {
        Map<Integer, Map<Character, Integer>> table = new HashMap<>();
        Set<Integer> accept = new HashSet<>();

        int lastState = 0;
        int nextState = 1;

        for (char c : literal.toCharArray()) {
            Map<Character, Integer> transitions = table.getOrDefault(lastState, new HashMap<>());
            transitions.put(c, nextState);
            for (int i = 0; i < 128; i++) {
                if (i != c) {
                    transitions.put((char) (i), -1);
                }
            }
            table.put(lastState, transitions);
            lastState = nextState++;
        }

        // From accepting state, map any input to dead state
        Map<Character, Integer> transitions = table.getOrDefault(lastState, new HashMap<>());
        for (int i = 0; i < 128; i++) {
            transitions.put((char) (i), -1);
        }
        table.put(lastState, transitions);

        accept.add(lastState);

        return new DFA(table, accept);
    }


    @Override
    public DFA getDFA() {
        return dfa;
    }
}
