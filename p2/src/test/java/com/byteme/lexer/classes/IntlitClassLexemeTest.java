package com.byteme.lexer.classes;

import com.byteme.lexer.DFA;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * p1
 */
public class IntlitClassLexemeTest {
    @Test
    public void getDFA() throws Exception {
        IntlitClassLexeme id = new IntlitClassLexeme("test");
        DFA dfa = id.getDFA();

        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("123"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("1"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("1230"));
        assertEquals(DFA.DFA_ACCEPT, dfa.evaluate("0"));


        assertEquals(DFA.DFA_DEAD, dfa.evaluate("abc"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("!2"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("_1"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("-2"));
        assertEquals(DFA.DFA_DEAD, dfa.evaluate("-2123"));

    }

}