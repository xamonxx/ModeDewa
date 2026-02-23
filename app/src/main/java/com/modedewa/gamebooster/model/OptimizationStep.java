package com.modedewa.gamebooster.model;

/**
 * OptimizationStep — Tracks progress of each optimization step.
 */
public class OptimizationStep {
    public enum Status {
        PENDING,
        RUNNING,
        SUCCESS,
        FAILED,
        SKIPPED
    }

    public String name;
    public String description;
    public String emoji;
    public Status status;
    public String message;

    public OptimizationStep(String emoji, String name, String description) {
        this.emoji = emoji;
        this.name = name;
        this.description = description;
        this.status = Status.PENDING;
        this.message = "";
    }

    public boolean isDone() {
        return status == Status.SUCCESS || status == Status.FAILED || status == Status.SKIPPED;
    }
}
