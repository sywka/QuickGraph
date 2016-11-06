package com.shlom.solutions.quickgraph.etc;

public class ProgressParams {

    private int progress;
    private int total;
    private String description;

    public ProgressParams(int progress, int total, String description) {
        this.progress = progress;
        this.total = total;
        this.description = description;
    }

    public int getProgress() {
        return progress;
    }

    public ProgressParams setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public ProgressParams setTotal(int total) {
        this.total = total;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProgressParams setDescription(String description) {
        this.description = description;
        return this;
    }
}
