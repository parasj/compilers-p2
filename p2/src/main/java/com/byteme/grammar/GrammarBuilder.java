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

    private static final String ELEMENT_NAME_GRAMMAR = "grammar";
    private static final String ELEMENT_NAME_PRODUCTIONRULE = "productionRule";
    private static final String ELEMENT_NAME_NONTERMINAL = "nonTerminal";
    private static final String ELEMENT_NAME_TERMINAL = "terminal";

    private static final String ATTRIBUTE_NAME_NAME = "name";
    private static final String ATTRIBUTE_NAME_STARTSYMBOL = "startSymbol";
    private static final String ATTRIBUTE_NAME_HEADNONTERMINAL = "headNonTerminal";
    private static final String ATTRIBUTE_NAME_LEXEME = "lexeme";

//    private static ProductionRule buildProductionRule(
//            final Hashtable<String, Terminal> validTerminals,
//            final Hashtable<String, NonTerminal> validNonTerminals,
//            NonTerminal headNonTerminal,
//            Symbol[] derivation
//    ) {
//
//
//        return null;
//    }

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
        int prIndex = 0;
        Element rootElement = xmlDoc.getRootElement();
        Hashtable<String, Lexeme> validLexemes = new Hashtable<>();
        Hashtable<String, Terminal> validTerminals = new Hashtable<>();
        Hashtable<String, NonTerminal> validNonTerminals = new Hashtable<>();
        Iterator rootIter = rootElement.elementIterator(ELEMENT_NAME_PRODUCTIONRULE);
        ProductionRule[] productionRules = new ProductionRule[rootElement.elements().size()];

        // Construct Lexeme Hashtable
        for (Lexeme l : lexemes) validLexemes.putIfAbsent(l.getLiteral(), l);

        // 1st iteration: discover all NonTerminals, check that each Terminal has recognized Lexeme
        while (rootIter.hasNext()) {
            Element prElement = (Element) rootIter.next();
            Iterator prIter = prElement.elementIterator();
            String headNonTerminalName = prElement.attributeValue(ATTRIBUTE_NAME_HEADNONTERMINAL);
            NonTerminal headNonTerminal = new NonTerminal(headNonTerminalName);

            // Discover head NonTerminal
            validNonTerminals.putIfAbsent(headNonTerminalName, headNonTerminal);

            // Discover Symbols in derivation
            while (prIter.hasNext()) {
                Element symbolElement = (Element) prIter.next();

                switch (symbolElement.getName()) {
                    // Discover a Terminal Symbol in the derivation
                    case ELEMENT_NAME_TERMINAL:
                        // Note: name of Lexeme that matches a Terminal == the name of the Terminal
                        String lexemeName = symbolElement.attributeValue(ATTRIBUTE_NAME_LEXEME);

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
                    case ELEMENT_NAME_NONTERMINAL:
                        String nonTerminalName = symbolElement.attributeValue(ATTRIBUTE_NAME_NAME);
                        NonTerminal discovered = new NonTerminal(nonTerminalName);

                        // System.out.println("\tNonTerminal: {Rule=" + ruleStr + "}"); // FIXME

                        validNonTerminals.putIfAbsent(nonTerminalName, discovered);
                        break;
                }
            }
        }

        // Get a new Iterator
        rootIter = rootElement.elementIterator(ELEMENT_NAME_PRODUCTIONRULE);

        // 2nd iteration: build all ProductionRules
        while (rootIter.hasNext()) {
            int derivationIndex = 0;
            Element prElement = (Element) rootIter.next();
            Iterator prIter = prElement.elementIterator();
            String headNonTerminalName = prElement.attributeValue(ATTRIBUTE_NAME_HEADNONTERMINAL);
            NonTerminal headNonTerminal = validNonTerminals.getOrDefault(headNonTerminalName, null);
            Symbol[] derivation = new Symbol[prElement.elements().size()];

            // Encounter derivation
            while (prIter.hasNext()) {
                Element symbolElement = (Element) prIter.next();
                String attributeValue = null;
                Symbol encounteredSymbol = null;

                switch (symbolElement.getName()) {
                    // Encounter a Terminal Symbol in the derivation
                    case ELEMENT_NAME_TERMINAL:
                        attributeValue = symbolElement.attributeValue(ATTRIBUTE_NAME_LEXEME);
                        encounteredSymbol = validTerminals.getOrDefault(attributeValue, null);
                        break;

                    // Encounter a NonTerminal Symbol in the derivation
                    case ELEMENT_NAME_NONTERMINAL:
                        attributeValue = symbolElement.attributeValue(ATTRIBUTE_NAME_NAME);
                        encounteredSymbol = validNonTerminals.getOrDefault(attributeValue, null);
                        break;
                }

                // Add Symbol to derivation if it was scanned in the discovery phase (1st iteration)
                if (null != encounteredSymbol) {
                    derivation[derivationIndex++] = encounteredSymbol;
                }
                else {
                    // FIXME: throw an error
                    System.err.println("Unrecognized symbol: " + attributeValue);
                }
            }

            // Construct this ProductionRule if the head NonTerminal was scanned in discovery phase
            if (null != headNonTerminal) {
                productionRules[prIndex++] = new ProductionRule(headNonTerminal, derivation);
            }
            else {
                // FIXME: throw an error
                System.err.println("Unrecognized head NonTerminal: " + headNonTerminalName);
            }
        }

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
