package com.github.yakupovdev.assignmment4.graph.topo;

import com.github.yakupovdev.assignmment4.graph.Graph;
import com.github.yakupovdev.assignmment4.graph.Metrics;
import java.util.*;

/**
 * TopologicalSort provides two ways to obtain a topological order:
 * - Kahn's algorithm (kahnSort)
 * - DFS-based ordering (dfsSort)
 */
public class TopologicalSort {
    private final Graph graph;
    private final Metrics metrics;

    /**
     * Construct TopologicalSort for a given graph and metrics collector.
     */
    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    /**
     * Kahn's algorithm for topological sorting.
     * Returns a list representing topological order or null if a cycle is detected.
     */
    public List<Integer> kahnSort() {
        metrics.startTiming();
        int n = graph.getVertexCount();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Graph.Edge e : graph.getNeighbors(u)) {
                inDegree[e.to]++;
                metrics.incrementOperations();
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementOperations();
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            metrics.incrementOperations();

            for (Graph.Edge e : graph.getNeighbors(u)) {
                int v = e.to;
                inDegree[v]--;
                metrics.incrementOperations();
                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementOperations();
                }
            }
        }

        metrics.stopTiming();
        return order.size() == n ? order : null;
    }
}