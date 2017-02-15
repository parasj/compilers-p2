package com.byteme.scanner.Tokens;

import com.byteme.scanner.DFA;
import com.byteme.scanner.Lexeme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * src
 */
public class KeywordLexeme implements Lexeme {
    private final String literal;
    private final DFA dfa;

    public KeywordLexeme(String literal) {
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
            for (int i = 0; i < 128; i++) {
                if (i != c) {
                    transitions.put((char)(i), -1);
                }
            }
            table.put(lastState, transitions);
            lastState = nextState++;
        }

        // From accepting state, map any input to dead state
        Map<Character, Integer> transitions = table.getOrDefault(lastState, new HashMap<>());
        for (int i = 0; i < 128; i++) {
            transitions.put((char)(i), -1);
        }
        table.put(lastState, transitions);

        accept.add(lastState);

        return new DFA(table, accept);
    }


    @Override
    public DFA getDFA() {
        return dfa;
    }

    /*
     * For keyword lexemes, the string representation is simply the lexeme's literal.
     */
    @Override
    public String toString() {
        /*return "KeywordLexeme: " +
                "literal='" + literal + '\'' +
                ", dfa=\n" + dfa;*/
        return literal;
    }
}
