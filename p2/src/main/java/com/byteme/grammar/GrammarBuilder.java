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

    private static final String ELEMENT_NAME_GRAMMAR = "Grammar";
    private static final String ELEMENT_NAME_PRODUCTIONRULE = "ProductionRule";
    private static final String ELEMENT_NAME_NONTERMINAL = "NonTerminal";
    private static final String ELEMENT_NAME_TERMINAL = "Terminal";

    private static final String ATTRIBUTE_NAME_NAME = "name";
    private static final String ATTRIBUTE_NAME_STARTSYMBOL = "startSymbol";
    private static final String ATTRIBUTE_NAME_HEADNONTERMINAL = "headNonTerminal";
    private static final String ATTRIBUTE_NAME_LEXEME = "lexeme";

    private static void doDiscoveryPhase(
            Iterator rootIter,
            Hashtable<String, Lexeme> validLexemes,
            Hashtable<String, Terminal> validTerminals,
            Hashtable<String, NonTerminal> validNonTerminals
    ) {
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

                        // Check if Lexeme is recognized
                        if (validLexemes.containsKey(lexemeName)) {
                            Lexeme discoveredLexeme = validLexemes.get(lexemeName);
                            Terminal discoveredTerminal = new Terminal(lexemeName, discoveredLexeme);

                            validTerminals.putIfAbsent(lexemeName, discoveredTerminal);
                        }
                        else {
                            throw new GrammarBuilderException(
                                    "Unrecognized Terminal: " + lexemeName,
                                    GrammarBuilderException.ExceptionSource.UNRECOGNIZED_TERMINAL
                            );
                        }
                        break;

                    // Discover a NonTerminal Symbol in the derivation
                    case ELEMENT_NAME_NONTERMINAL:
                        String nonTerminalName = symbolElement.attributeValue(ATTRIBUTE_NAME_NAME);
                        NonTerminal discovered = new NonTerminal(nonTerminalName);

                        validNonTerminals.putIfAbsent(nonTerminalName, discovered);
                        break;

                    // Ignore if not a Terminal or NonTerminal
                    default:
                        continue;
                }
            }
        }
    }

    private static void doBuildingPhase(
        Iterator rootIter,
        Hashtable<String, Terminal> validTerminals,
        Hashtable<String, NonTerminal> validNonTerminals,
        ProductionRule[] productionRules
    ) {
        int prIndex = 0;

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
                Symbol encounteredSymbol = null;

                switch (symbolElement.getName()) {
                    // Encounter a Terminal Symbol in the derivation
                    case ELEMENT_NAME_TERMINAL:
                        String lexemeName = symbolElement.attributeValue(ATTRIBUTE_NAME_LEXEME);
                        encounteredSymbol = validTerminals.getOrDefault(lexemeName, null);

                        // Throw exception if encounteredSymbol is null
                        if (null == encounteredSymbol) {
                            throw new GrammarBuilderException(
                                    "Unrecognized Terminal: " + lexemeName,
                                    GrammarBuilderException.ExceptionSource.UNRECOGNIZED_TERMINAL
                            );
                        }

                        break;

                    // Encounter a NonTerminal Symbol in the derivation
                    case ELEMENT_NAME_NONTERMINAL:
                        String nonTerminalName = symbolElement.attributeValue(ATTRIBUTE_NAME_NAME);
                        encounteredSymbol = validNonTerminals.getOrDefault(nonTerminalName, null);

                        // Throw exception if encounteredSymbol is null
                        if (null == encounteredSymbol) {
                            throw new GrammarBuilderException(
                                    "Unrecognized NonTerminal: " + nonTerminalName,
                                    GrammarBuilderException.ExceptionSource.UNRECOGNIZED_NONTERMINAL
                            );
                        }

                        break;

                    // Ignore if not a Terminal or NonTerminal
                    default:
                        continue;
                }

                // Add Symbol to derivation if it was scanned in the discovery phase (1st iteration)
                if (null != encounteredSymbol) {
                    derivation[derivationIndex++] = encounteredSymbol;
                }
            }

            // Construct this ProductionRule if the head NonTerminal was scanned in discovery phase
            if (null != headNonTerminal) {
                productionRules[prIndex++] = new ProductionRule(headNonTerminal, derivation);
            }
            else {
                throw new GrammarBuilderException(
                        "Unrecognized head NonTerminal: " + headNonTerminalName,
                        GrammarBuilderException.ExceptionSource.UNRECOGNIZED_NONTERMINAL
                );
            }
        }
    }

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

    // TODO: Currently this assumes the passed-in xmlDoc is a valid grammar XML document.
    // TODO: Eventually it would be nice to throw a MalformedXMLException or something if this isn't the case.
    public static Grammar buildGrammar(Document xmlDoc, Lexeme ... lexemes) {
        Element rootElement = xmlDoc.getRootElement();
        Hashtable<String, Lexeme> validLexemes = new Hashtable<>();
        Hashtable<String, Terminal> validTerminals = new Hashtable<>();
        Hashtable<String, NonTerminal> validNonTerminals = new Hashtable<>();
        Iterator rootIter = rootElement.elementIterator(ELEMENT_NAME_PRODUCTIONRULE);
        ProductionRule[] productionRules = new ProductionRule[rootElement.elements().size()];

        // Construct Lexeme Hashtable
        for (Lexeme l : lexemes) validLexemes.putIfAbsent(l.getLiteral(), l);

        // 1st iteration: discover all NonTerminals, check that each Terminal has recognized Lexeme
        doDiscoveryPhase(rootIter, validLexemes, validTerminals, validNonTerminals);

        // Get a new Iterator
        rootIter = rootElement.elementIterator(ELEMENT_NAME_PRODUCTIONRULE);

        // 2nd iteration: build all ProductionRules
        doBuildingPhase(rootIter, validTerminals, validNonTerminals, productionRules);

        return new Grammar(productionRules);
    }


    public static class GrammarBuilderException extends RuntimeException {

        private ExceptionSource exceptionSource;

        public static enum ExceptionSource {
            UNRECOGNIZED_NONTERMINAL,
            UNRECOGNIZED_TERMINAL
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
