package com.byteme.frontend.parser;

import com.byteme.frontend.grammar.*;
import com.byteme.frontend.grammar.sets.LL1ParseTable;
import com.byteme.frontend.lexer.KeywordLexeme;
import com.byteme.frontend.lexer.Token;
import com.byteme.util.Tuple2;

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

            assert toptup != null;
            Symbol top = toptup.x;
            ASTNode node = toptup.y;

            if (node == null) {
                assert stack.isEmpty() : "Stack is not empty!";
                assert cursor == source.size() - 1 : "Not at end of input!";
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
        HashMap<String, String> tails = new HashMap<String, String>();
        tails.put("neparams","neparamst");
        tails.put("stmts","stmtst");
        tails.put("stmt","stmtt");
        tails.put("neexprs","neexprst");
        tails.put("pred","predt");


        Stack<ASTNode> nodes = new Stack<>();
        nodes.push(root);
        while(!nodes.isEmpty()) {
            ASTNode pnode = nodes.pop();
            if (pnode != null) {
                if (pnode.getClass() == ASTNodeNonterminal.class) {
                    if (tails.containsKey(((ASTNodeNonterminal) pnode).getProductionRule().getHeadNonTerminal().toString())) {
                        String t = ((ASTNodeNonterminal) pnode).getProductionRule().getHeadNonTerminal().toString();
                        for (ASTNode s : ((ASTNodeNonterminal) pnode).getChildren()) {
                            if (s.getClass() == ASTNodeNonterminal.class &&
                                    ((ASTNodeNonterminal) s).getProductionRule().getHeadNonTerminal().toString().equals(tails.get(t))) {
                                ArrayList<ASTNode> temp = new ArrayList<>(((ASTNodeNonterminal) s).getChildren());
                                ((ASTNodeNonterminal) pnode).removeChild(s);
                                for (ASTNode n : temp) {
//                                    if (n.getClass() != ASTNodeTerminal.class && ((ASTNodeTerminal)n).get)
                                    ((ASTNodeNonterminal) pnode).pushChild(n);
                                }
                            }
                        }
                    }
                }
                if (pnode.getClass() == ASTNodeNonterminal.class) {
                    for (ASTNode n : ((ASTNodeNonterminal) pnode).getChildren()) {
                        nodes.push(n);
                    }
                }
            }
        }
    }

    public void removeRecursion(ASTNode root) {

        Hashtable<NonTerminal, LinkedList<ProductionRule>> language = this.g.getLanguages();
        NonTerminal expr = new NonTerminal("expr");
        NonTerminal clause = new NonTerminal("clause");
        NonTerminal aexpr = new NonTerminal("aexpr");
        NonTerminal term = new NonTerminal("term");
        NonTerminal pred = new NonTerminal("pred");
        NonTerminal factor = new NonTerminal("factor");

        ProductionRule etoc = new ProductionRule(expr, clause);

        for(ASTNode c : ((ASTNodeNonterminal)root).getChildren()) {
            if (c instanceof ASTNodeNonterminal) {
                ASTNodeNonterminal ntchild = (ASTNodeNonterminal)c;

                rrHelper((ASTNodeNonterminal)root, ntchild, expr, clause, etoc);

                removeRecursion(c);

            }
        }
    }

    public void rrHelper(ASTNodeNonterminal root, ASTNodeNonterminal ntchild, NonTerminal parent, NonTerminal child, ProductionRule parr) {
        if (!root.getProductionRule().getHeadNonTerminal().equals(parent)
                && !ntchild.getProductionRule().getHeadNonTerminal().equals(parent)) {
            while(true) {
                if (ntchild.getChildren().size() == 3) {
                    ASTNodeNonterminal left = (ASTNodeNonterminal)ntchild.getChildren().remove(0);
                    ASTNode mid = ntchild.getChildren().remove(1);
                    ASTNodeNonterminal right = (ASTNodeNonterminal) ntchild.getChildren().remove(2);

                    if (left.getProductionRule().getHeadNonTerminal().equals(child)) {
                        ArrayList<ASTNode> t = new ArrayList<>();
                        t.add(left);
                        left = new ASTNodeNonterminal(parr, t);
                    }

                    if (right.getProductionRule().getHeadNonTerminal().equals(child)) {
                        ntchild.getChildren().add(0, left);
                        ntchild.getChildren().add(1, mid);
                        ntchild.getChildren().add(2, right);
                        break;
                    }

                    if (right.getChildren().size() == 1) {
                        ntchild.getChildren().add(0, left);
                        ntchild.getChildren().add(1, mid);
                        ntchild.getChildren().add(2, right.getChildren().get(0));
                        break;
                    } else {
                        assert right.getChildren().size() != 3 : String.format("The size of %s : %s is wrong", right.toString(), right.getChildren().size());
                        ArrayList<ASTNode> t = new ArrayList<>();
                        t.add(left);
                        t.add(mid);
                        t.add(right.getChildren().get(0));
                        ntchild.getChildren().add(0, new ASTNodeNonterminal(parr, t));
                        ntchild.getChildren().add(1, right.getChildren().get(1));
                        ntchild.getChildren().add(2, right.getChildren().get(2));

                    }


                } else {
                    break;
                }
            }
        }
    }

}
