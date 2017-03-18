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
    private final LL1ParseTable table;
    private final Grammar g;
    private static final Terminal teof = new Terminal("end", new KeywordLexeme("end"));
    private static final Terminal tepsilon = new Terminal("", new KeywordLexeme(""));

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

                if (pr == null) {
                    System.out.println();
                }

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
            for(int i = 0; i < NNT.getChildren().size(); i++) {
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
                        i+=child.getChildren().size() - 1;
                    }
                }
            }
        }

    }

    public void removeRecursion(ASTNode root) {

        ProductionRule etoc = new ProductionRule(new NonTerminal("expr"), new NonTerminal("clause"), new NonTerminal("exprleft"));
        ArrayList<ProductionRule> rs = new ArrayList<>();
        rs.add(etoc);
//        for(ASTNode c : ((ASTNodeNonterminal)root).getChildren()) {
//            if (c instanceof ASTNodeNonterminal) {
//                ASTNodeNonterminal ntchild = (ASTNodeNonterminal)c;
//
//                rrHelper((ASTNodeNonterminal)root, ntchild, expr, clause, etoc, eltoe);
//
//                removeRecursion(c);
//
//            }
//        }
        for(ProductionRule pr : rs) {
            Stack<ASTNode> stack = new Stack<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                ASTNode curr = stack.pop();
                if (curr instanceof ASTNodeNonterminal) {
                    ASTNodeNonterminal currNT = (ASTNodeNonterminal) curr;
                    for (int i = 0; i < currNT.getChildren().size(); i++) {
                        ASTNode n = currNT.getChildren().get(i);
                        if (n instanceof ASTNodeNonterminal) {
                            if (((ASTNodeNonterminal) n).getProductionRule().equals(pr)) {
                                System.out.println(((ASTNodeNonterminal) n).getProductionRule());
                                while (true) {
                                    ASTNodeNonterminal m;

                                    int inn = 0;
                                    for (ASTNode te : ((ASTNodeNonterminal) n).getChildren()) {
                                        ASTNodeNonterminal ten = (ASTNodeNonterminal) te;
                                        System.out.println(inn++ + " ; " + ten.getProductionRule());
                                    }

                                    if (((ASTNodeNonterminal) n).getChildren().size() == 2) {
                                        m = (ASTNodeNonterminal) ((ASTNodeNonterminal) n).getChildren().get(1);
                                    } else {
                                        m = (ASTNodeNonterminal) ((ASTNodeNonterminal) n).getChildren().get(3);
                                    }
                                    if (m.getProductionRule().getDerivation().contains(tepsilon)) {
                                        ((ASTNodeNonterminal) n).removeChild(m);
                                        break;
                                    } else {
                                        m.getChildren().add(0, n);
                                        n = m;
                                    }
                                }
                                currNT.getChildren().remove(i);
                                currNT.getChildren().add(i, n);
                            }
                        }
                        currNT.getChildren().remove(i);
                        currNT.getChildren().add(i, n);
                    }

                    for (ASTNode c : currNT.getChildren()) {
                        stack.push(c);
                    }
                }

            }
        }
    }

//    public void rrHelper(ASTNodeNonterminal root, ASTNodeNonterminal ntchild, NonTerminal parent, NonTerminal child, ProductionRule parr, ProductionRule childr) {
//        if (!root.getProductionRule().equals(parr)
//                && ntchild.getProductionRule().equals(parr)) {
//            while(true) {
//                if (ntchild.getChildren().size() == 3) {
//                    ASTNodeNonterminal left = (ASTNodeNonterminal) ntchild.getChildren().remove(0);
//                    ASTNode mid = ntchild.getChildren().remove(0);
//                    ASTNodeNonterminal right = (ASTNodeNonterminal) ntchild.getChildren().remove(0);
//                    if (left.getProductionRule().getHeadNonTerminal().equals(childr)) {
//                        ArrayList<ASTNode> t = new ArrayList<>();
//                        t.add(left);
//                        left = new ASTNodeNonterminal(parr, t);
//                    }
//
//                    if (right.getProductionRule().getHeadNonTerminal().equals(childr)) {
//                        ntchild.getChildren().add(0, left);
//                        ntchild.getChildren().add(1, mid);
//                        ntchild.getChildren().add(2, right);
//                        break;
//                    }
//
//                    if (right.getChildren().size() == 1) {
//                        ntchild.getChildren().add(0, left);
//                        ntchild.getChildren().add(1, mid);
//                        ntchild.getChildren().add(2, right.getChildren().get(0));
//                        break;
//                    } else {
//                        assert right.getChildren().size() == 3 : String.format("The size of %s : %s is wrong", right.getProductionRule().toString(), right.getChildren().size());
//                        ArrayList<ASTNode> t = new ArrayList<>();
//                        t.add(left);
//                        t.add(mid);
//                        t.add(right.getChildren().get(0));
//                        ntchild.getChildren().add(0, new ASTNodeNonterminal(parr, t));
//                        ntchild.getChildren().add(1, right.getChildren().get(1));
//                        ntchild.getChildren().add(2, right.getChildren().get(2));
//
//                    }
//
//
//                }else {
//                    break;
//                }
//            }
//        }
//    }

}
