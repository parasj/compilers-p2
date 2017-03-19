package com.byteme.frontend.parser;

import com.byteme.frontend.grammar.*;
import com.byteme.frontend.grammar.sets.LL1ParseTable;
import com.byteme.frontend.lexer.KeywordLexeme;
import com.byteme.frontend.lexer.Token;
import com.byteme.frontend.lexer.classes.CommentClassLexeme;
import com.byteme.util.Tuple2;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.*;
import java.util.stream.Collectors;

/**
 * p2
 */
public class LLParser {
    private static final Terminal teof = new Terminal("end", new KeywordLexeme("end"));
    private static final Terminal tepsilon = new Terminal("", new KeywordLexeme(""));
    private final LL1ParseTable table;
    private final Grammar g;

    public LLParser(LL1ParseTable table, Grammar g) {
        this.table = table;
        this.g = g;
    }

    public ASTNode parse(List<Token> sourceIn) {
        List<Token> source = sourceIn.stream().filter(x -> !(x.getLexeme() instanceof CommentClassLexeme)).collect(Collectors.toList());

        ASTNodeNonterminal rootNode = new ASTNodeNonterminal(getRootProductionRule(g), new ArrayList<>());
        Stack<Tuple2<Symbol, ASTNode>> stack = new Stack<>();
        stack.push(new Tuple2<>(teof, null));
        stack.push(new Tuple2<>(new NonTerminal("program"), rootNode));

        for (int cursor = 0; cursor < source.size(); ) {
            Token current = source.get(cursor);
            Tuple2<Symbol, ASTNode> toptup = stack.pop();

            assert toptup != null;
            Symbol top = toptup.x;
            ASTNode node = toptup.y;

            if (node == null) {
                assert stack.isEmpty() : "Stack is not empty!";
                assert cursor == source.size() - 1 : "Not at end of input!";
                removeTails(rootNode);
                removeRecursion(rootNode);
                return rootNode;
            }

            if (top instanceof Terminal && ((Terminal) top).getLexeme().equals(current.getLexeme())) {
                cursor++;
                ((ASTNodeTerminal) node).setToken(current);
            } else if (top instanceof NonTerminal) {
                ProductionRule pr = table.get((NonTerminal) top, new Terminal("NA", current.getLexeme()));
                ((ASTNodeNonterminal) node).setProductionRule(pr);
                ((ASTNodeNonterminal) node).setToken(current);

                // assert pr != null : String.format("PR does not exist in table for %s(%s)", top, current);
                if (pr == null) {
                    System.out.printf("line %d:%d parser error", current.getLine(), current.getLinePosition());
                    System.exit(1);
                }
                LinkedList<Symbol> derivationLL = pr.getDerivation();

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
                    ((ASTNodeNonterminal) node).pushChild(newnode);
                    stack.push(tup2);
                }
            }
        }

        removeTails(rootNode);
        removeRecursion(rootNode);

        return rootNode;
    }

    private ProductionRule getRootProductionRule(Grammar g) {
        for (ProductionRule pr : g.getProductionRules())
            if (pr.getHeadNonTerminal().getName().equals("program"))
                return pr;
        throw new NullPointerException("Missing root production rule");
    }

    public void removeTails(ASTNode root) {

        if (root instanceof ASTNodeNonterminal) {
            ASTNodeNonterminal NNT = (ASTNodeNonterminal) root;
            for (int i = 0; i < NNT.getChildren().size(); i++) {
                if (NNT.getChildren().get(i) instanceof ASTNodeNonterminal) {
                    ASTNodeNonterminal child = (ASTNodeNonterminal) NNT.getChildren().get(i);
                    removeTails(child);
                    if (child.getProductionRule().getHeadNonTerminal().toString().endsWith("tail")) {
                        if (child.getProductionRule().getDerivation().contains(tepsilon)) {
                            NNT.getProductionRule().getDerivation().remove(child.getProductionRule().getHeadNonTerminal());

                        } else {
                            NNT.getProductionRule().getDerivation().remove(child.getProductionRule().getHeadNonTerminal());
                            NNT.getProductionRule().getDerivation().addAll(child.getProductionRule().getDerivation());
                        }
                        NNT.getChildren().remove(i);
                        for (int j = 0; j < child.getChildren().size(); j++) {
                            NNT.getChildren().add(i + j, child.getChildren().get(j));
                        }
                        i += child.getChildren().size() - 1;
                    }
                }
            }
        }

    }

    public void removeRecursion(ASTNode root) {

        ProductionRule etoc = new ProductionRule(new NonTerminal("expr"), new NonTerminal("clause"), new NonTerminal("exprleft"));
        ArrayList<ProductionRule> rs = new ArrayList<>();
        rs.add(etoc);
        for(ProductionRule pr : rs) {
            Stack<ASTNode> stack = new Stack<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                ASTNode curr = stack.pop();
                if (curr instanceof ASTNodeNonterminal) {
                    ASTNodeNonterminal currNT = (ASTNodeNonterminal) curr;
                    if (currNT.getProductionRule().getHeadNonTerminal().equals(pr.getHeadNonTerminal())) {
                        if (currNT.getChildren().size() == 2 && ((ASTNodeNonterminal)currNT.getChildren().get(1)).getChildren().size() != 0) {
                            ASTNode child = currNT.getChildren().get(0);
                            ArrayList<ASTNode> list = new ArrayList<ASTNode>();
                            list.add(child);
                            currNT.getChildren().set(0, new ASTNodeNonterminal(new ProductionRule(pr.getHeadNonTerminal(), ((ASTNodeNonterminal)child).getProductionRule().getHeadNonTerminal()),
                                    list));
                        } else if (currNT.getChildren().size() == 2 && ((ASTNodeNonterminal)currNT.getChildren().get(1)).getChildren().size() == 0) {
                            ((ASTNodeNonterminal) curr).removeChild(currNT.getChildren().get(1));
                        }
                    }
                }

            }
        }
    }


}
