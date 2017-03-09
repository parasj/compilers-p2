package com.byteme.scanner;

import com.byteme.lexer.DFA;
import com.byteme.lexer.Lexeme;
import com.byteme.lexer.Token;
import com.byteme.lexer.classes.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * src
 */
public class Scanner {

    private int scannedTokensIndex = 0;
    //private List<Lexeme> lexemeList = new ArrayList<>();
    private File inputFile;
    private Lexeme[] lexemes;
    private LinkedList<Token> scannedTokens;

    /**
     *
     * @param inputFile
     * @param lexemes
     */
    public Scanner(File inputFile, Lexeme ... lexemes) {
        this.inputFile = inputFile;
        this.lexemes = lexemes;

        scannedTokens = new LinkedList<>();
    }

    /**
     * Scans the input file and generates tokens based on the provided Lexemes.
     */
    public void tokenize() {
        // Allow re-tokenization, in case we ever want to do this
        if (scannedTokens.size() > 0) {
            scannedTokens.clear();
            scannedTokensIndex = 0;
        }

        try {
            // Assume UTF-8 encoding
            byte fileBytes[] = Files.readAllBytes(Paths.get(inputFile.getPath()));
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
                        // This is our error condition: if there is some input after backtracking
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

            //return lexemeList;
        } catch (IOException ioex) {
            System.out.println("ERROR: IOException encountered while reading from file");
            System.out.println("Stack trace below:");
            ioex.printStackTrace();
        }
    }

    /**
     * TODO
     * @return
     */
    public boolean hasNextToken() {
        return scannedTokensIndex < scannedTokens.size();
    }

    /**
     * TODO
     * @return
     */
    public Token getNextToken() {
        return scannedTokens.get(scannedTokensIndex++);
    }

    /**
     * TODO
     * @return
     */
    public Token peekNextToken() {
        return scannedTokens.get(scannedTokensIndex);
    }

    /**
     * TODO
     * @param token
     * @return
     */
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

    /**
     * TODO
     * @param token
     * @return
     */
    private boolean consumeToken(String token) {
        // Find a Lexeme that accepts the previous token
        Lexeme acceptingLexeme = getFirstLexemeAccepting(token);

        if (null != acceptingLexeme) {
            scannedTokens.addLast(new Token(acceptingLexeme, token));

//            if (acceptingLexeme instanceof KeywordLexeme) {
//                lexemeList.add(acceptingLexeme);
//            } else {
//                lexemeList.add(((ClassLexeme) acceptingLexeme).newClassLexemeWithS(token));
//            }
            return true;
        } else {
            // TODO: This should be a scanner error, right?
            return false;
        }
    }
}
