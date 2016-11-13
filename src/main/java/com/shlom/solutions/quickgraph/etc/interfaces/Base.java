package com.shlom.solutions.quickgraph.etc.interfaces;

public abstract class Base {

    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public final boolean equals(Object o) {
        return super.equals(o);
    }

    protected final void finalize() throws Throwable {
        super.finalize();
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final String toString() {
        return super.toString();
    }
}
