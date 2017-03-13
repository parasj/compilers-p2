package com.byteme.grammar;

import org.dom4j.*;

import com.byteme.lexer.Lexeme;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * Builds a Grammar object from an XML file, drawing from a provided pool of
 * Lexemes.
 */
public class GrammarBuilder {

    /**
     *
     * @param xmlFile
     * @return
     * @throws DocumentException
     */
    public static Document parseXML(File xmlFile) throws DocumentException {
        SAXReader saxReader = new SAXReader();

        return saxReader.read(xmlFile);
    }

    public static Grammar buildGrammar(Document xmlDoc, Lexeme ... lexemes) {
        /*
         *      First iteration over the XML "discovers" all NonTerminals, and also checks that each
         *      Terminal corresponds to a lexeme.
         *
         *      Second iteration over the XML builds the Grammar. Here, if we find a ProductionRule
         *      with an unrecognized NonTerminal, we can fail.
         *
         */
        Element rootElement = xmlDoc.getRootElement();
        Iterator rootIter = rootElement.elementIterator("productionRule"); // TODO: maybe make the argument a const?
        LinkedList<ProductionRule> productionRulesList = new LinkedList<>();
        ProductionRule[] productionRules;
        Hashtable<String, Lexeme> validLexemes = new Hashtable<>();
        Hashtable<String, Terminal> validTerminals = new Hashtable<>();
        Hashtable<String, NonTerminal> validNonTerminals = new Hashtable<>();

        // Construct Lexeme Hashtable
        for (Lexeme l : lexemes) validLexemes.putIfAbsent(l.getLiteral(), l);

        // 1st iteration: discover all NonTerminals, check that each Terminal has recognized Lexeme
        while (rootIter.hasNext()) {
            Element prElement = (Element) rootIter.next();
            Iterator prIter = prElement.elementIterator();
            String headNonTerminalName = prElement.attributeValue("headNonTerminal"); // TODO: maybe make the argument a const?
            NonTerminal headNonTerminal = new NonTerminal(headNonTerminalName);

            // Discover head NonTerminal
            validNonTerminals.putIfAbsent(headNonTerminalName, headNonTerminal);

            // Discover Symbols in derivation
            while (prIter.hasNext()) {
                Element symbolElement = (Element) prIter.next();

                switch (symbolElement.getName()) {
                    // Discover a Terminal Symbol in the derivation
                    case "terminal": // TODO: maybe make the case argument a const?
                        // Note: name of Lexeme that matches a Terminal == the name of the Terminal
                        String lexemeName = symbolElement.attributeValue("lexeme"); // TODO: maybe make the argument a const?

                        //System.out.println("\tTerminal: {Lexeme=" + lexemeName + "}"); // FIXME

                        // Check if Lexeme is recognized
                        if (validLexemes.containsKey(lexemeName)) {
                            Lexeme discoveredLexeme = validLexemes.get(lexemeName);
                            Terminal discoveredTerminal = new Terminal(lexemeName, discoveredLexeme);

                            validTerminals.putIfAbsent(lexemeName, discoveredTerminal);
                        }
                        else {
                            // TODO: throw an exception. For now print something
                            System.err.println("Unrecognized lexeme: " + lexemeName);
                        }
                        break;

                    // Discover a NonTerminal Symbol in the derivation
                    case "nonterminal": // TODO: maybe make the case argument a const?
                        String nonTerminalName = symbolElement.attributeValue("name"); // TODO: maybe make the argument a const
                        NonTerminal discovered = new NonTerminal(nonTerminalName);

                        // System.out.println("\tNonTerminal: {Rule=" + ruleStr + "}"); // FIXME

                        validNonTerminals.putIfAbsent(nonTerminalName, discovered);
                        break;
                }
            }
        }

        // Get a new Iterator
        rootIter = rootElement.elementIterator("productionRule"); // TODO: maybe make the argument a const?

        // 2nd iteration: build all ProductionRules
        while (rootIter.hasNext()) {
            Element prElement = (Element) rootIter.next();
            Iterator prIter = prElement.elementIterator();

            // Discover derivation
            while (prIter.hasNext()) {
                // TODO XKCD
            }
        }

        // Initialize and populate productionRules array, now that we know its size
        productionRules = new ProductionRule[productionRulesList.size()];

        productionRulesList.toArray(productionRules);
        return new Grammar(productionRules);
    }

    public static class GrammarBuilderException extends Exception {

        private ExceptionSource exceptionSource;

        public static enum ExceptionSource {
            MALFORMED_XML,
            UNRECOGNIZED_TERMINAL,
            UNRECOGNIZED_NONTERMINAL,
        }

        public GrammarBuilderException(String message, ExceptionSource exceptionSource) {
            super(message);

            this.exceptionSource = exceptionSource;
        }

        public ExceptionSource getExceptionSource() {
            return exceptionSource;
        }
    }

}
