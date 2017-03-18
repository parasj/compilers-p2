package com.byteme.frontend.scanner;

import com.byteme.frontend.lexer.DFA;
import com.byteme.frontend.lexer.Lexeme;
import com.byteme.frontend.lexer.Token;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * TODO
 */
public class Scanner implements Iterator<Token> {

    private int scannedTokensIndex = 0;

    private File inputFile;
    private Lexeme[] lexemes;
    private LinkedList<Token> scannedTokens;

    /**
     * Constructs a new Scanner over the specified Lexemes for a given File.
     *
     * @param inputFile the File to be scanned
     * @param lexemes   an array of Lexemes with which to accept and tokenize
     *                  input
     */
    public Scanner(File inputFile, Lexeme... lexemes) {
        this.inputFile = inputFile;
        this.lexemes = lexemes;

        scannedTokens = new LinkedList<>();
    }

    /**
     * Scans the input file and generates Tokens based on the provided Lexemes.
     */
    public void tokenize() {
        // Allow re-generation of tokens, in case we ever want to do this
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

                    String fileSoFar = new String(fileChars).substring(0, i + 1);
                    int lineCount = fileSoFar.length() - fileSoFar.replace("\n", "").length() + 1;
                    int charCount = fileSoFar.length() - fileSoFar.lastIndexOf('\n') - token.length();

                    // If there was no previous token, there is no need to backtrack
                    if (previousToken.length() == 0) {
                        // This is our error condition: if there is some input after backtracking
                        if (token.trim().length() > 0) { // todo bad way of handling this
                            System.out.println("line " + lineCount + ":" + charCount + " scanner error");
                            System.exit(1);
                        }
                        token = "";
                    }
                    // Otherwise, backtrack until we find a token that is accepted by some Lexeme
                    else {
                        scanToken(previousToken, lineCount, charCount);

                        token = "";
                        i -= 1; // So we can re-evaluate the discarded input
                    }
                }
            }

            // Consume any leftover tokens
            if (!token.isEmpty()) {
                if (!scanToken(token, -1, -1)) {
                    String fileSoFar = new String(fileChars).substring(0, fileChars.length - token.length());

                    int lineCount = fileSoFar.length() - fileSoFar.replace("\n", "").length() + 1;
                    int charCount = fileSoFar.length() - fileSoFar.lastIndexOf('\n');
                    System.out.println("line " + lineCount + ":" + charCount + " scanner error");
                    System.exit(1);
                }
            }
        } catch (IOException ioex) {
            System.out.println("ERROR: IOException encountered while reading from file");
            System.out.println("Stack trace below:");
            ioex.printStackTrace();
        }
    }

    /**
     * Checks whether there are any unconsumed Tokens in the queue.
     *
     * @return true if the Scanner has Tokens to return, else false.
     */
    public boolean hasNextToken() {
        return scannedTokensIndex < scannedTokens.size();
    }

    /**
     * Returns the next Token in the queue and consumes it.
     *
     * @return the next Token in the queue.
     */
    public Token getNextToken() {
        return scannedTokens.get(scannedTokensIndex++);
    }

    /**
     * Returns the next Token in the queue without consuming it.
     *
     * @return the next Token in the queue.
     */
    public Token peekNextToken() {
        return scannedTokens.get(scannedTokensIndex);
    }

    /**
     * Returns the first Lexeme which is found to accept the specified input, or
     * null if no match was not found.
     *
     * @param token the input to be matched
     * @return the first Lexeme found to accept the given input.
     */
    private Lexeme getFirstLexemeAccepting(String token) {
        Lexeme acceptingLexeme = null;

        // Iterate over all known lexemes, return first one that accepts this token
        for (Lexeme l : lexemes) {
            DFA dfa = l.getDFA();

            if (DFA.DFA_ACCEPT == dfa.evaluate(token)) {
                acceptingLexeme = l;
                break;
            }
        }

        return acceptingLexeme;
    }

    /**
     * Tokenizes the given input and adds it to the list of scanned Tokens.
     * <p>
     * Note that in order for the input to be tokenized, it must be accepted by
     * some Lexeme.
     *
     * @param token        the input to be scanned
     * @param line
     * @param linePosition
     * @return true if the input was able to be tokenized, else false.
     */
    private boolean scanToken(String token, int line, int linePosition) {
        // Find a Lexeme that accepts the previous token
        Lexeme acceptingLexeme = getFirstLexemeAccepting(token);

        if (null != acceptingLexeme) {
            scannedTokens.addLast(new Token(acceptingLexeme, token, line, linePosition));

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasNext() {
        return hasNextToken();
    }

    @Override
    public Token next() {
        return getNextToken();
    }
}
