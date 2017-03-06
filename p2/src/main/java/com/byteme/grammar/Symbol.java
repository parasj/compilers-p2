package com.byteme.grammar;

/**
 * A Symbol is the most basic component of a context-free grammar.
 *
 * A Symbol can either be a Terminal or a Non-Terminal.
 */
public abstract class Symbol {

    // declared protected for ease-of-access in subclasses
    protected final String name;

    /**
     * Constructs a new Symbol with the specified name.
     *
     * @param   name    - the (String) name of this Symbol
     */
    protected Symbol(String name) {
        this.name = name;
    }

    /**
     * Returns this Symbol's name.
     *
     * @return the Symbol's name as a String.
     */
    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
