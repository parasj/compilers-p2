package com.byteme;

import com.byteme.grammar.Grammar;
import com.byteme.grammar.GrammarBuilder;
import com.byteme.lexer.KeywordLexeme;
import com.byteme.lexer.Lexeme;
import com.byteme.lexer.Token;
import com.byteme.lexer.classes.*;
import com.byteme.scanner.Scanner;
import org.dom4j.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Main {

    // TODO: Move this to GrammarBuilder or Grammar or something
    //Made public to use
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

    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            System.exit(1);
        }

        String fnameIn = args[0];
        String phase = args[1];

        if (phase.equals("--tokens")) {
            Scanner sc = new Scanner(new File(fnameIn), lexemes);

            // Generate tokens
            sc.tokenize();

            while (sc.hasNextToken()) {
                Token t = sc.getNextToken();
                Lexeme l = t.getLexeme();

                // Format the token output
                String tokenString = l.stringify(t.getValue());

                // Only write tokenString if it is not empty
                if (!tokenString.isEmpty()) {
                    System.out.print(l.stringify(t.getValue()));

                    if (sc.hasNextToken())
                        System.out.print(" ");
                }
            }
        }
        // TODO: This is temporary for testing purposes
        else if (phase.equals("--buildgrammar")) {
            try {
                Lexeme[] grammarLexemes = new Lexeme[lexemes.length + 1];
                LinkedList<Lexeme> grammarLexemesList = new LinkedList<>();
                Document doc = GrammarBuilder.parseXML(new File(fnameIn));

                Collections.addAll(grammarLexemesList, lexemes);
                grammarLexemesList.add(new KeywordLexeme("")); // to match epsilon
                grammarLexemesList.toArray(grammarLexemes);

                System.out.println(GrammarBuilder.buildGrammar(doc, grammarLexemes));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            printUsage();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar compiler.jar SOURCE_FILE [—-tokens]\n");
    }
}
