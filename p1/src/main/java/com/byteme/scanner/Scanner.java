package com.byteme.scanner;

import com.byteme.scanner.Tokens.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * src
 */
public class Scanner {
    private File f;
    private Lexeme[] lexemes = {
            new KeywordLexeme("array"),
            new KeywordLexeme("begin"),
            new KeywordLexeme("boolean"),
            new KeywordLexeme("break"),
            new KeywordLexeme("do"),
            new KeywordLexeme("else"),
            new KeywordLexeme("end"),
            new KeywordLexeme("enddo"),
            new KeywordLexeme("endif"),
            new KeywordLexeme("false"),
            new KeywordLexeme("float"),
            new KeywordLexeme("for"),
            new KeywordLexeme("func"),
            new KeywordLexeme("if"),
            new KeywordLexeme("in"),
            new KeywordLexeme("int"),
            new KeywordLexeme("let"),
            new KeywordLexeme("of"),
            new KeywordLexeme("return"),
            new KeywordLexeme("then"),
            new KeywordLexeme("to"),
            new KeywordLexeme("true"),
            new KeywordLexeme("type"),
            new KeywordLexeme("unit"),
            new KeywordLexeme("var"),
            new KeywordLexeme("while"),
            new KeywordLexeme(","),
            new KeywordLexeme(":"),
            new KeywordLexeme(";"),
            new KeywordLexeme("("),
            new KeywordLexeme(")"),
            new KeywordLexeme("["),
            new KeywordLexeme("]"),
            new KeywordLexeme("{"),
            new KeywordLexeme("}"),
            new KeywordLexeme("."),
            new KeywordLexeme("+"),
            new KeywordLexeme("-"),
            new KeywordLexeme("*"),
            new KeywordLexeme("/"),
            new KeywordLexeme("="),
            new KeywordLexeme("<>"),
            new KeywordLexeme("<"),
            new KeywordLexeme(">"),
            new KeywordLexeme("<="),
            new KeywordLexeme(">="),
            new KeywordLexeme("&"),
            new KeywordLexeme("|"),
            new KeywordLexeme(":="),
            new CommentClassLexeme(),
            new FloatlitClassLexeme(),
            new IdClassLexeme(),
            new IntlitClassLexeme()
    };

    public Scanner(File f) {
        this.f = f;
    }

    private Lexeme getFirstScannerTokenAccepting(String token) {
        Lexeme acceptingLexeme = null;

        // Iterate over all known scanner tokens, return first one that accepts this token
        for (Lexeme st : lexemes) {
            DFA dfa = st.getDFA();

            if (DFA.DFA_ACCEPT == dfa.evaluate(token)) {
                acceptingLexeme = st;
                break;
            }
        }

        return acceptingLexeme;
    }

    public List<Lexeme> tokenize() {
        List<Lexeme> lexemeList = new ArrayList();

        try {
            // Assume UTF-8 encoding
            byte fileBytes[] = Files.readAllBytes(Paths.get(f.getPath()));
            char fileChars[] = new String(fileBytes, StandardCharsets.UTF_8).toCharArray();

            String token = new String();

            // Iteratively build our candidate token
            for (int i = 0; i < fileChars.length; i++) {
                char newestChar = fileChars[i];

                // The number of DFAs in each state post-candidate token evaluation
                HashMap<Integer, Integer> dfaStateCounter = new HashMap();

                dfaStateCounter.put(DFA.DFA_ACCEPT, 0);
                dfaStateCounter.put(DFA.DFA_DEAD, 0);
                dfaStateCounter.put(DFA.DFA_REJECT, 0);

                // Treat newlines and tabs as whitespace so all input is ASCII
                if (newestChar == '\n' || newestChar == '\t') {
                    newestChar = ' ';
                }

                token = token.concat(Character.toString(newestChar));

                // TODO: This is temporary, for debugging
                //System.out.println("Candidate Token: " + token);

                // Evaluate each DFA
                for (Lexeme st : lexemes) {
                    DFA stDFA = st.getDFA();

                    int state = st.getDFA().evaluate(token);
                    int newCount = dfaStateCounter.get(state) + 1;

                    dfaStateCounter.put(state, newCount);
                }

                // If all DFAs are dead, throw out current input if it is whitespace

                /*
                 * Once all DFAs are dead, we must backtrack until we encounter an accepting DFA
                 *
                 * A backtracking function is called which will remove the last character from our candidate token,
                 * evaluate all DFAs with this new candidate token, and track the states resultant.
                 *
                 * If two DFAs are matching, this is (we can assume) that the token matches a keyword and an IdClassLexeme, so just
                 * return the Lexeme matching this as a keyword.
                 *
                 * If there are still more DFAs matching, we screwed up.
                 */
                if (lexemes.length == dfaStateCounter.get(DFA.DFA_DEAD)) {
                    String previousToken = token.substring(0, token.length() - 1);

                    // If there was no previous token, there is no need to backtrack
                    if (previousToken.length() == 0) {
                        token = new String();
                    }
                    // Otherwise, backtrack until we find a token that is accepted by some Lexeme
                    else {
                        // TODO - what if we match more than 2? An error should be thrown.

                        // Find a Lexeme that accepts the previous token
                        Lexeme acceptingLexeme = getFirstScannerTokenAccepting(previousToken);

                        if (null != acceptingLexeme) {
                            lexemeList.add(acceptingLexeme);
                        }
                        else {
                            // TODO Do we potentially have to go back more than one iteration?
                            System.out.println("==ERROR==");
                            break;
                        }

                        token = new String();
                        i -= 1; // So we can re-evaluate the discarded input
                    }
                }
            }

            return lexemeList;
        }
        // TODO: How do we want to handle encountering an IOException here?
        catch (IOException ioex) {
            return null;
        }
    }
}
