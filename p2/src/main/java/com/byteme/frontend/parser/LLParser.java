package com.byteme.frontend.parser;

import com.byteme.frontend.grammar.*;
import com.byteme.frontend.grammar.sets.LL1ParseTable;
import com.byteme.frontend.lexer.KeywordLexeme;
import com.byteme.frontend.lexer.Token;
import com.byteme.util.Tuple2;
import sun.jvm.hotspot.debugger.cdbg.Sym;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * p2
 */
public class LLParser {
    private final LL1ParseTable table;
    private final Grammar g;
    private static final Terminal teof = new Terminal("end", new KeywordLexeme("end"));

    public LLParser(LL1ParseTable table, Grammar g) {
        this.table = table;
        this.g = g;
    }

    public ASTNode parse(List<Token> source) {
        ASTNodeNonterminal rootNode = new ASTNodeNonterminal(getRootProductionRule(g), new ArrayList<>());
        Stack<Tuple2<Symbol, ASTNode>> stack = new Stack<>();
        stack.push(new Tuple2<>(teof, null));
        stack.push(new Tuple2<>(new NonTerminal("program"), rootNode));

        for (int cursor = 0; cursor < source.size(); ) {
            Token current = source.get(cursor);
            Tuple2<Symbol, ASTNode> toptup = stack.pop();
            Symbol top = toptup.x;
            ASTNode node = toptup.y;


            if (top instanceof Terminal && ((Terminal) top).getLexeme().equals(current.getLexeme())) {
                // consume terminal
                cursor++;
                ((ASTNodeTerminal) node).setToken(current);
            } else if (top instanceof NonTerminal) {
                ProductionRule pr = table.get((NonTerminal) top, new Terminal(current.getValue(), current.getLexeme()));
                ((ASTNodeNonterminal) node).setProductionRule(pr);
                ((ASTNodeNonterminal) node).setToken(current);

                assert pr != null : String.format("PR does not exist in table for %s(%s)", top, current);
                LinkedList<Symbol> derivationLL = pr.getDerivation();

                assert derivationLL != null : String.format("Derivation does not exist for pr[%s] %s(%s)", pr, top, current);
                ArrayList<Symbol> derivation = new ArrayList<>(derivationLL);
                for (int i = derivation.size() - 1; i >= 0; i--) { // reverse?
                    Symbol s = derivation.get(i);
                    ASTNode newnode;

                    if (s instanceof Terminal) {
                        newnode = new ASTNodeTerminal(null);
                    } else {
                        newnode = new ASTNodeNonterminal(null, new ArrayList<>());
                    }

                    Tuple2<Symbol, ASTNode> tup2 = new Tuple2<>(s, newnode);
                    stack.push(tup2);
                }
            }
        }

        return rootNode;
    }

    private ProductionRule getRootProductionRule(Grammar g) {
        for (ProductionRule pr : g.getProductionRules())
            if (pr.getHeadNonTerminal().getName().equals("program"))
                return pr;
        throw new NullPointerException("Missing root production rule");
    }
}