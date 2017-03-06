package com.byteme.lexer.classes;

import com.byteme.lexer.DFA;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * p1
 */
public class KeywordLexemeTest {
    @Test
    public void getDFA() throws Exception {
        DFA end = new KeywordLexeme("end").getDFA();
        DFA enddo = new KeywordLexeme("enddo").getDFA();
        DFA ifkwd = new KeywordLexeme("if").getDFA();

        assertEquals(DFA.DFA_ACCEPT, end.evaluate("end"));
        assertEquals(DFA.DFA_ACCEPT, enddo.evaluate("enddo"));
        assertEquals(DFA.DFA_ACCEPT, ifkwd.evaluate("if"));

        assertEquals(DFA.DFA_DEAD, end.evaluate("##"));
        assertEquals(DFA.DFA_DEAD, enddo.evaluate("aa"));
        assertEquals(DFA.DFA_DEAD, ifkwd.evaluate("123"));

        assertEquals(DFA.DFA_REJECT, enddo.evaluate("end"));
    }

}