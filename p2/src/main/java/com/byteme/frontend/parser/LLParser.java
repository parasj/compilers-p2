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
                setParents(rootNode);
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
        setParents(rootNode);
        removeRecursion(rootNode);

        return rootNode;
    }

    private ProductionRule getRootProductionRule(Grammar g) {
        for (ProductionRule pr : g.getProductionRules())
            if (pr.getHeadNonTerminal().getName().equals("program"))
                return pr;
        throw new NullPointerException("Missing root production rule");
    }

    private void removeTails(ASTNode root) {

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

    private void setParents(ASTNode root) {
        Stack<ASTNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            ASTNode curr = stack.pop();
            if (curr instanceof  ASTNodeNonterminal) {
                for (ASTNode c : ((ASTNodeNonterminal)curr).getChildren()) {
                    c.setParent(curr);
                    if (c instanceof ASTNodeNonterminal) {
                        stack.push(c);
                    }
                }
            }
        }
    }

    private void removeRecursion(ASTNode root) {
        HashMap<NonTerminal, NonTerminal> map = new HashMap<>();
        NonTerminal expr = new NonTerminal("expr");
        NonTerminal exprleft = new NonTerminal("exprleft");
        map.put(expr,exprleft);
        NonTerminal aexpr = new NonTerminal("aexpr");
        NonTerminal aexprleft = new NonTerminal("aexprleft");
        map.put(aexpr,aexprleft);
        NonTerminal clause = new NonTerminal("clause");
        NonTerminal clauseleft = new NonTerminal("clauseleft");
        map.put(clause,clauseleft);
        NonTerminal term = new NonTerminal("term");
        NonTerminal termleft = new NonTerminal("termleft");
        map.put(term,termleft);
        for (NonTerminal nt : map.keySet()) {
            removeRecursion1(root, nt, map.get(nt));
            setParents(root);
        }

        for (NonTerminal nt : map.keySet()) {
            cleanuptails((ASTNodeNonterminal) root, nt, map.get(nt));
            setParents(root);
        }

    }

    private void removeRecursion1(ASTNode root, NonTerminal expr, NonTerminal exprleft) {

        ASTNodeNonterminal rootNT = (ASTNodeNonterminal) root;

        for (int i = 0; i < rootNT.getChildren().size(); i++) {
            ASTNode c = rootNT.getChildren().get(i);
            if (c instanceof ASTNodeNonterminal) {
                if (((ASTNodeNonterminal)c).getProductionRule().getHeadNonTerminal().equals(expr)) {
                    ASTNodeNonterminal cnt = (ASTNodeNonterminal) c;
                    for(int j = 0; j < cnt.getChildren().size(); j++) {
                        if (cnt.getChildren().get(j) instanceof ASTNodeNonterminal
                                && ((ASTNodeNonterminal)cnt.getChildren().get(j)).getProductionRule().getHeadNonTerminal().equals(exprleft)) {
                            //Find the tail
                            ASTNodeNonterminal tail = findTail((ASTNodeNonterminal) cnt.getChildren().get(j), exprleft);
                            ASTNodeNonterminal parent = (ASTNodeNonterminal) tail.getParent();
                            for (int l = 0; l < parent.getChildren().size(); l++) {
                                if (parent.getChildren().get(l).equals(tail)) {
                                    parent.getChildren().remove(l);
                                }
                            }
                            //Remove tail
                            List<ASTNode> list = new ArrayList<>();
                            ASTNodeNonterminal newTree = new ASTNodeNonterminal(parent.getProductionRule(), list);
                            if (parent.getChildren().size() == 1) {
                                return;
                            }


                            //Get the part to be added to left
                            ASTNodeNonterminal left = getLeft(parent, expr, exprleft);

                            changeHead(left, expr, exprleft);

                            //Add to left and put in original list
                            rootNT.getChildren().remove(i);
                            rootNT.getChildren().add(i, left);
                        }
                    }
                } else {
                    removeRecursion1(c, expr, exprleft);
                }
            }
        }
    }

    private ASTNodeNonterminal findTail(ASTNodeNonterminal root, NonTerminal nonTerminal) {
        //Tail ends in tepsilon
        if ((root).getProductionRule().getHeadNonTerminal().equals(nonTerminal)
                && ((root).getProductionRule().getDerivation().contains(tepsilon) )) {
            return root;
        } else if ((root).getProductionRule().getHeadNonTerminal().equals(nonTerminal)){
            for (ASTNode c : (root).getChildren()) {
                if (c instanceof ASTNodeNonterminal) {
                    if (((ASTNodeNonterminal)c).getProductionRule().getHeadNonTerminal().equals(nonTerminal)) {
                        return findTail((ASTNodeNonterminal) c, nonTerminal);
                    }
                }
            }
        }
        return null;
    }

    private ASTNodeNonterminal getLeft( ASTNodeNonterminal curr, NonTerminal ntstop, NonTerminal ntleft) {
        if (curr.getProductionRule().getHeadNonTerminal().equals(ntstop)) {
            return null;
        }


        //If parent starts with ntleft, figure out that left and add
        if (((ASTNodeNonterminal)curr.getParent()).getProductionRule().getHeadNonTerminal().equals(ntleft)) {
            ASTNodeNonterminal newTree = new ASTNodeNonterminal(curr.getProductionRule(), curr.getChildren());
            newTree.getChildren().add(0, getLeft(((ASTNodeNonterminal)curr.getParent()), ntstop, ntleft));
            return newTree;
        }

        if (((ASTNodeNonterminal)curr.getParent()).getProductionRule().getHeadNonTerminal().equals(ntstop)) {
            ASTNodeNonterminal newTree = new ASTNodeNonterminal(curr.getProductionRule(), curr.getChildren());
            ASTNodeNonterminal newp = new ASTNodeNonterminal(((ASTNodeNonterminal)(curr.getParent())).getProductionRule(), ((ASTNodeNonterminal)(curr.getParent())).getChildren());
            newp.getChildren().remove(newp.getChildren().size() - 1);
            newTree.getChildren().add(0, newp);
            return newTree;
        }

        return null;

    }


    private void changeHead(ASTNodeNonterminal root, NonTerminal newNT, NonTerminal oldNT) {
        Stack<ASTNodeNonterminal> stack = new Stack<>();
        stack.push(root);
        while(!stack.isEmpty()) {
            ASTNodeNonterminal popped = stack.pop();
            if (popped.getProductionRule().getHeadNonTerminal().equals(oldNT)) {
                popped.getProductionRule().setHeadNonTerminal(newNT);
            }
            for (ASTNode n : popped.getChildren()) {
                if (n instanceof ASTNodeNonterminal) {
                    stack.push((ASTNodeNonterminal)n);
                }
            }
        }
    }

    private void cleanuptails(ASTNodeNonterminal root, NonTerminal newNT, NonTerminal oldNT) {
        Stack<ASTNodeNonterminal> stack = new Stack<>();
        stack.push(root);
        while(!stack.isEmpty()) {
            ASTNodeNonterminal popped = stack.pop();
            for (int i = 0; i < popped.getChildren().size(); i++) {
                ASTNode n = popped.getChildren().get(i);
                if (n instanceof ASTNodeNonterminal) {
                    if ((((ASTNodeNonterminal)n).getProductionRule().getHeadNonTerminal().equals(oldNT) || ((ASTNodeNonterminal)n).getProductionRule().getHeadNonTerminal().equals(newNT))&& ((ASTNodeNonterminal)n).getProductionRule().getDerivation().contains(tepsilon)) {
                        popped.getChildren().remove(i);
                    }
                    stack.push((ASTNodeNonterminal)n);
                }
            }
        }
    }

}
