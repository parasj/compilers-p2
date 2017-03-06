package com.byteme.scanner;

import com.byteme.lexer.ClassLexeme;
import com.byteme.lexer.DFA;
import com.byteme.lexer.Lexeme;
import com.byteme.lexer.classes.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * src
 */
public class Scanner {
    private List<Lexeme> lexemeList = new ArrayList<>();
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
            new KeywordLexeme("_"),
            new CommentClassLexeme(null),
            new FloatlitClassLexeme(null),
            new IdClassLexeme(null),
            new IntlitClassLexeme(null)
    };

    public Scanner(File f) {
        this.f = f;
    }

    private Lexeme getFirstLexemeAccepting(String token) {
        Lexeme acceptingLexeme = null;

        // Iterate over all known lexemes, return first one that accepts this token
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
        try {
            // Assume UTF-8 encoding
            byte fileBytes[] = Files.readAllBytes(Paths.get(f.getPath()));
            char fileChars[] = new String(fileBytes, StandardCharsets.UTF_8).toCharArray();

            String token = "";

            // Iteratively build our candidate token
            for (int i = 0; i < fileChars.length; i++) {
                char newestChar = fileChars[i];

                // The number of DFAs in each state post-candidate token evaluation
                HashMap<Integer, Integer> dfaStateCounter = new HashMap<>();

                dfaStateCounter.put(DFA.DFA_ACCEPT, 0);
                dfaStateCounter.put(DFA.DFA_DEAD, 0);
                dfaStateCounter.put(DFA.DFA_REJECT, 0);

                // Treat newlines and tabs as whitespace so all input is ASCII
                if (newestChar == '\n' || newestChar == '\t') {
                    newestChar = ' ';
                }

                token = token.concat(Character.toString(newestChar));

                // Evaluate each DFA
                for (Lexeme st : lexemes) {
                    DFA stDFA = st.getDFA();

                    int state = st.getDFA().evaluate(token);
                    int newCount = dfaStateCounter.get(state) + 1;

                    dfaStateCounter.put(state, newCount);
                }

                /*
                 * Once all DFAs are dead, we must backtrack until we encounter an accepting DFA
                 *
                 * A backtracking function is called which will remove the last character from our candidate token,
                 * evaluate all DFAs with this new candidate token, and track the states resultant.
                 *
                 * If two DFAs are matching, this is (we can assume) that the token matches a keyword and an
                 * IdClassLexeme, so just return the Lexeme matching this as a keyword.
                 *
                 * If there are still more DFAs matching, we screwed up.
                 */
                if (lexemes.length == dfaStateCounter.get(DFA.DFA_DEAD)) {
                    String previousToken = token.substring(0, token.length() - 1);

                    // If there was no previous token, there is no need to backtrack
                    if (previousToken.length() == 0) {
                        if (token.trim().length() > 0) { // todo bad way of handling this
                            String fileSoFar = new String(fileChars).substring(0, i + 1);
                            int lineCount = fileSoFar.length() - fileSoFar.replace("\n", "").length() + 1;
                            int charCount = fileSoFar.length() - fileSoFar.lastIndexOf('\n') - 1;

                            System.out.println("line " + lineCount + ":" + charCount + " scanner error");
                            System.exit(1);
                        }
                        token = "";
                    }
                    // Otherwise, backtrack until we find a token that is accepted by some Lexeme
                    else {
                        consumeToken(previousToken);

                        token = "";
                        i -= 1; // So we can re-evaluate the discarded input
                    }
                }
            }

            // Consume any leftover tokens
            if (!token.isEmpty()) {
                consumeToken(token);
            }

            return lexemeList;
        } catch (IOException ioex) {
            return null;
        }
    }

    private boolean consumeToken(String token) {
        // Find a Lexeme that accepts the previous token
        Lexeme acceptingLexeme = getFirstLexemeAccepting(token);

        if (null != acceptingLexeme) {
            if (acceptingLexeme instanceof KeywordLexeme) {
                lexemeList.add(acceptingLexeme);
            } else {
                lexemeList.add(((ClassLexeme) acceptingLexeme).newClassLexemeWithS(token));
            }
            return true;
        } else {
            // TODO: This should be a scanner error, right?
            return false;
        }
    }
}
