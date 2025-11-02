package com.github.yakupovdev.assignmment4.graph;

public interface Metrics {
    void incrementOperations();
    void incrementOperations(int count);
    long getOperationCount();
    void startTiming();
    void stopTiming();
    long getElapsedNanos();
    double getElapsedMillis();
    void reset();
}