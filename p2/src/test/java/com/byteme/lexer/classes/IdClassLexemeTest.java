package com.byteme.lexer.classes;

import com.byteme.lexer.DFA;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * p1
 */
public class IdClassLexemeTest {
    @Test
    public void getDFA() throws Exception {
        IdClassLexeme id = new IdClassLexeme("test");
        DFA dfa = id.getDFA();

        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("abc"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("abc123"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("abc_123"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("a12345_1234"));

        assertEquals(DFA.DFA_DEAD, dfa.evaluate("123"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("@123"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("@printf"));
    }
}