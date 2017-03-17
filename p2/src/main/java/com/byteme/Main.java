package com.byteme;

import com.byteme.frontend.grammar.Grammar;
import com.byteme.frontend.grammar.GrammarBuilder;
import com.byteme.frontend.grammar.ProductionRule;
import com.byteme.frontend.lexer.KeywordLexeme;
import com.byteme.frontend.lexer.Lexeme;
import com.byteme.frontend.lexer.Token;
import com.byteme.frontend.parser.LLParser;
import com.byteme.frontend.scanner.Scanner;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.byteme.TigerSpec.GRAMMAR_XML_PATH;
import static com.byteme.TigerSpec.lexemes;

public class Main {

    private static Grammar buildGrammar() throws DocumentException {
        Lexeme[] grammarLexemes = new Lexeme[lexemes.length + 1];
        Lexeme epsilonLexeme = new KeywordLexeme(""); // for matching epsilon (the empty sequence)
        LinkedList<Lexeme> grammarLexemesList = new LinkedList<>();

        // Build list of Lexemes for the GrammarBuilder
        Collections.addAll(grammarLexemesList, lexemes);
        grammarLexemesList.add(epsilonLexeme);
        grammarLexemesList.toArray(grammarLexemes);

        Document doc = GrammarBuilder.parseXML(new File(GRAMMAR_XML_PATH));
        return GrammarBuilder.buildGrammar(doc, grammarLexemes);
    }

    private static List<Token> lexTokens(File f) {
        List<Token> toks = new ArrayList<>();
        Scanner scanner = new Scanner(f, lexemes);
        scanner.tokenize();

        while (scanner.hasNextToken()) {
            toks.add(scanner.getNextToken());
        }

        toks.add(new Token(new KeywordLexeme("end"), ""));

        return toks;
    }

    /*
     * Called when using the --tokens command-line argument.
     *
     * For Project 1.
     */
    private static void doScanningPhase(String fileName) {
        List<String> toks = lexTokens(new File(fileName)).stream()
                .map(t -> t.getLexeme().stringify(t.getValue()))
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        System.out.println(String.join(" ", toks));
    }

    /*
     * Called when using the --grammar command-line argument.
     *
     * For testing Grammar, GrammarBuilder, and grammar.xml during Project 2 development.
     */
    private static void doGrammarBuildingPhase() throws DocumentException {
        System.out.println(buildGrammar());
    }

    /*
     * Called when using the --ast command-line argument.
     *
     * For Project 2.
     */
    private static void doParsingPhase(String fileName) throws DocumentException {
        List<Token> toks = lexTokens(new File(fileName));
        Grammar g = buildGrammar();
        LLParser parser = new LLParser(g.getParseTable(), g);
        parser.parse(toks);
//                .map(ProductionRule::toString)
//                .collect(Collectors.toList());
//        System.out.println(String.join("\n", matchedRules));
    }

    /*
     * Displays information about how to run this program.
     */
    private static void printUsage() {
        System.out.println("Usage: java -jar compiler.jar SOURCE_FILE [--tokens | --grammar | --ast]\n");
    }

    public static void main(String[] args) throws DocumentException {
        if (args.length != 2) {
            printUsage();
            System.exit(1);
        }

        String fnameIn = args[0];
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
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
}
