package com.github.yakupovdev.assignmment4.graph;

public class MetricsImpl implements Metrics {
    private long operationCount = 0;
    private long startTime = 0;
    private long endTime = 0;

    @Override
    public void incrementOperations() {
        operationCount++;
    }

    @Override
    public void incrementOperations(int count) {
        operationCount += count;
    }

    @Override
    public long getOperationCount() {
        return operationCount;
    }

    @Override
    public void startTiming() {
        startTime = System.nanoTime();
    }

    @Override
    public void stopTiming() {
        endTime = System.nanoTime();
    }

    @Override
    public long getElapsedNanos() {
        return endTime - startTime;
    }

    @Override
    public double getElapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    @Override
    public void reset() {
        operationCount = 0;
        startTime = 0;
        endTime = 0;
    }
}