package com.byteme.scanner;

import com.byteme.scanner.Tokens.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * src
 */
public class Scanner {
    private File f;
    private ScannerToken[] tokens = {
            new KeywordScannerToken("array"),
            new KeywordScannerToken("begin"),
            new KeywordScannerToken("boolean"),
            new KeywordScannerToken("break"),
            new KeywordScannerToken("do"),
            new KeywordScannerToken("else"),
            new KeywordScannerToken("end"),
            new KeywordScannerToken("enddo"),
            new KeywordScannerToken("endif"),
            new KeywordScannerToken("false"),
            new KeywordScannerToken("float"),
            new KeywordScannerToken("for"),
            new KeywordScannerToken("func"),
            new KeywordScannerToken("if"),
            new KeywordScannerToken("in"),
            new KeywordScannerToken("int"),
            new KeywordScannerToken("let"),
            new KeywordScannerToken("of"),
            new KeywordScannerToken("return"),
            new KeywordScannerToken("then"),
            new KeywordScannerToken("to"),
            new KeywordScannerToken("true"),
            new KeywordScannerToken("type"),
            new KeywordScannerToken("unit"),
            new KeywordScannerToken("var"),
            new KeywordScannerToken("while"),
            new KeywordScannerToken(","),
            new KeywordScannerToken(":"),
            new KeywordScannerToken(";"),
            new KeywordScannerToken("("),
            new KeywordScannerToken(")"),
            new KeywordScannerToken("["),
            new KeywordScannerToken("]"),
            new KeywordScannerToken("{"),
            new KeywordScannerToken("}"),
            new KeywordScannerToken("."),
            new KeywordScannerToken("+"),
            new KeywordScannerToken("-"),
            new KeywordScannerToken("*"),
            new KeywordScannerToken("/"),
            new KeywordScannerToken("="),
            new KeywordScannerToken("<>"),
            new KeywordScannerToken("<"),
            new KeywordScannerToken(">"),
            new KeywordScannerToken("<="),
            new KeywordScannerToken(">="),
            new KeywordScannerToken("&"),
            new KeywordScannerToken("|"),
            new KeywordScannerToken(":="),
            new comment(),
            new floatlit(),
            new id(),
            new intlit()
    };

    public Scanner(File f) {
        this.f = f;
        //for (ScannerToken t : tokens) {
        //    System.out.println(t + "\n\n");
        //}
    }

    public List<ScannerToken> tokenize() {
        try {
            // Assume UTF-8 encoding
            byte fileBytes[] = Files.readAllBytes(Paths.get(f.getPath()));
            HashMap<Integer, Integer> stateCounter = new HashMap();
            String fileString = new String(fileBytes, StandardCharsets.UTF_8);
            String candidateToken = new String();

            stateCounter.put(DFA.DFA_ACCEPT, 0);
            stateCounter.put(DFA.DFA_DEAD, 0);
            stateCounter.put(DFA.DFA_REJECT, 0);

            // Iteratively build our candidate token
            // TODO: we have to keep track of start and end index in string
            for (char c : fileString.toCharArray()) {
                // TODO: This is temporary so that only the first "word" is evaluated
                if (c == ' ' || c == '\n' || c == '\t') continue;

                candidateToken = candidateToken.concat(Character.toString(c));

                // TODO: This is temporary, for debugging
                System.out.println("Candidate Token: " + candidateToken);

                // Evaluate each DFA
                for (ScannerToken st : tokens) {
                    DFA stDFA = st.getDFA();

                    System.out.println("\t\tCurrent DFA: " + stDFA.toString());

                    int state = st.getDFA().evaluate(candidateToken);
                    int newCount = stateCounter.get(state) + 1;

                    stateCounter.put(state, newCount);
                }

                System.out.println("\tDFA Count: " + tokens.length);
                System.out.println("\tDFA_ACCEPT: " + stateCounter.get(DFA.DFA_ACCEPT));
                System.out.println("\tDFA_REJECT: " + stateCounter.get(DFA.DFA_REJECT));
                System.out.println("\tDFA_DEAD: " + stateCounter.get(DFA.DFA_DEAD));
                System.out.println("");
            }

            return Collections.emptyList();
        }
        // TODO: How do we want to handle encountering an IOException here?
        catch (IOException ioex) {
            return null;
        }
    }
}
