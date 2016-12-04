package com.shlom.solutions.quickgraph.model.database.interfaces;

public interface CascadeDelete {

    void deleteCascade();

    void deleteDependents();
}
