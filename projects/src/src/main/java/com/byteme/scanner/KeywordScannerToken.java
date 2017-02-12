package com.byteme.scanner;

/**
 * src
 */
public class KeywordScannerToken implements ScannerToken {
    private final String literal;
    private final DFA dfa;

    public KeywordScannerToken(String literal) {
        this.literal = literal;

    }
}
