package com.byteme.frontend.grammar;

/**
 * Created by ebuntu on 3/5/17.
 */
public class NonTerminal extends Symbol {

    /**
     * Constructs a new NonTerminal with the specified name.
     *
     * @param name - the (String) name of this NonTerminal
     */
    public NonTerminal(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.name.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonTerminal nt = (NonTerminal) o;

        return this.name != null ? this.name.equals(nt.name) : nt.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
