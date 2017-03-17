package com.byteme.frontend.lexer.classes;

import com.byteme.frontend.lexer.DFA;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * p1
 */
public class FloatlitClassLexemeTest {
    @Test
    public void getDFA() throws Exception {
        FloatlitClassLexeme id = new FloatlitClassLexeme();
        DFA dfa = id.getDFA();

        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("123.123"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("123."));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("0.123"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("19.120"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("0.0"));

        assertEquals(DFA.DFA_DEAD, dfa.evaluate("."));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("123.123."));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("123.123.123"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("00.1"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("00"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("012.120"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("abc"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate(".1.2.3"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("1234a.201"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("12.34a"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("@1234.12"));


        assertEquals(DFA.DFA_REJECT, dfa.evaluate("0"));
        assertEquals(DFA.DFA_REJECT, dfa.evaluate("1230"));
    }

}