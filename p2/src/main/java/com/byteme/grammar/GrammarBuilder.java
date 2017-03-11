package com.byteme.grammar;

import org.dom4j.*;

import com.byteme.lexer.Lexeme;
import org.dom4j.io.SAXReader;

import java.io.File;

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

//    public static Grammar buildGrammar(File grammarXMLFile, Lexeme ... lexemes) {
//
//    }

    public static class GrammarBuilderException extends Exception {

        private ExceptionSource exceptionSource;

        public static enum ExceptionSource {
            SOURCEFILE_NOT_XML,
            SOURCEFILE_MALFORMED
        }

        public GrammarBuilderException(String message, ExceptionSource source) {
            super(message);

            this.exceptionSource = exceptionSource;
        }

        public ExceptionSource getExceptionSource() {
            return exceptionSource;
        }
    }

}
