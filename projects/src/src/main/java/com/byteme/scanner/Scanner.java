package com.byteme.scanner;

import java.io.File;
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
            new KeywordScannerToken(":=")
    };

    public Scanner(File f) {
        this.f = f;
    }

    public List<ScannerToken> tokenize() {
        return null;
    }
}