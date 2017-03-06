package com.byteme.grammar;

/**
 * Created by ebuntu on 3/5/17.
 */
public class NonTerminal extends Symbol {

    /**
     * Constructs a new NonTerminal with the specified name.
     *
     * @param   name    - the (String) name of this NonTerminal
     */
    protected NonTerminal(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.name.toLowerCase();
    }
}
