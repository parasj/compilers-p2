package com.byteme.lexer.classes;

import com.byteme.lexer.DFA;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * p1
 */
public class CommentClassLexemeTest {
    @Test
    public void getDFA() throws Exception {
        CommentClassLexeme id = new CommentClassLexeme();
        DFA dfa = id.getDFA();

        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("/* 123 */"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("/* /* */"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("/**/"));

        assertEquals(DFA.DFA_REJECT, dfa.evaluate(""));

        assertEquals(DFA.DFA_DEAD, dfa.evaluate("*/ /*"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("/* */ */"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("/* */ */ */"));
    }
}