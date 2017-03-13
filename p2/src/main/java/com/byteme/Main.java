package com.byteme;

import com.byteme.grammar.GrammarBuilder;
import com.byteme.lexer.KeywordLexeme;
import com.byteme.lexer.Lexeme;
import com.byteme.lexer.Token;
import com.byteme.lexer.classes.*;
import com.byteme.scanner.Scanner;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;

public class Main {

    public static Lexeme[] lexemes = {
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
            new CommentClassLexeme(),
            new FloatlitClassLexeme(),
            new IdClassLexeme(),
            new IntlitClassLexeme()
    };

    /*
     * Called when using the --tokens command-line argument.
     *
     * For Project 1.
     */
    private static void doScanningPhase(String fileName) {
        Scanner scanner = new Scanner(new File(fileName), lexemes);

        // Generate Tokens for this file
        scanner.tokenize();

        // Iterate over generated Tokens
        while (scanner.hasNextToken()) {
            Token t = scanner.getNextToken();
            Lexeme l = t.getLexeme();

            // Format the token output
            String tokenString = l.stringify(t.getValue());

            // Only write tokenString if it is not empty
            if (!tokenString.isEmpty()) {
                System.out.print(l.stringify(t.getValue()));

                if (scanner.hasNextToken())
                    System.out.print(" ");
            }
        }
    }

    /*
     * Called when using the --grammar command-line argument.
     *
     * For testing Grammar, GrammarBuilder, and grammar.xml during Project 2 development.
     */
    private static void doGrammarBuildingPhase() {
        final String GRAMMAR_XML_PATH = "grammar.xml";

        Lexeme[] grammarLexemes = new Lexeme[lexemes.length + 1];
        Lexeme epsilonLexeme = new KeywordLexeme(""); // for matching epsilon (the empty sequence)
        LinkedList<Lexeme> grammarLexemesList = new LinkedList<>();

        // Build list of Lexemes for the GrammarBuilder
        Collections.addAll(grammarLexemesList, lexemes);
        grammarLexemesList.add(epsilonLexeme);
        grammarLexemesList.toArray(grammarLexemes);

        try {
            Document doc = GrammarBuilder.parseXML(new File(GRAMMAR_XML_PATH));

            System.out.println(GrammarBuilder.buildGrammar(doc, grammarLexemes));
        }
        catch (DocumentException dex) {
            dex.printStackTrace();
        }
    }

    /*
     * Called when using the --ast command-line argument.
     *
     * For Project 2.
     */
    private static void doParsingPhase(String fileName) {
        // TODO: Implement this once our parser is complete.
        return;
    }

    /*
     * Displays information about how to run this program.
     */
    private static void printUsage() {
        System.out.println(
                "Usage: java -jar compiler.jar SOURCE_FILE [--tokens | --grammar | --ast]\n"
        );
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            System.exit(1);
        }

        String fnameIn = args[0];
        String phase = args[1];

        switch (phase) {
            case "--tokens":
                doScanningPhase(fnameIn);
                break;

            case "--grammar":
                doGrammarBuildingPhase();
                break;

            case "--ast":
                doParsingPhase(fnameIn);
                break;

            default:
                printUsage();
                System.exit(1);
                break;
        }
    }
}
