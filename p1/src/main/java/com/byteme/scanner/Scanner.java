package com.byteme.scanner;

import com.byteme.scanner.Tokens.*;

import java.io.File;
import java.util.Collections;
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
        for (ScannerToken t : tokens) {
            System.out.println(t + "\n\n");
        }
    }

    public List<ScannerToken> tokenize() {
        return Collections.emptyList();
    }
}
