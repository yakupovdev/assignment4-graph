package com.github.yakupovdev.assignmment4.graph.dagsp;

import com.github.yakupovdev.assignmment4.graph.Graph;
import com.github.yakupovdev.assignmment4.graph.Metrics;
import com.github.yakupovdev.assignmment4.graph.MetricsImpl;
import com.github.yakupovdev.assignmment4.graph.topo.TopologicalSort;
import java.util.*;

/**
 * DAGShortestPath computes shortest and longest paths in a DAG.
 * Uses topological ordering to relax edges in linear time.
 */
public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;

    /**
     * PathResult holds distances, predecessors and an optional reconstructed path.
     * distances: distance from source to each vertex (Integer.MAX_VALUE if unreachable).
     */
    public static class PathResult {
        public final int[] distances;
        public final int[] predecessors;
        public final List<Integer> path;
        public final int pathLength;

        public PathResult(int[] distances, int[] predecessors, List<Integer> path, int pathLength) {
            this.distances = distances;
            this.predecessors = predecessors;
            this.path = path;
            this.pathLength = pathLength;
        }
    }

    /**
     * Create a DAGShortestPath instance for given graph and metrics collector.
     */
    public DAGShortestPath(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    /**
     * Compute the shortest path distances from source in a DAG.
     * Returns null if graph is not a DAG (cycle detected by topological sort).
     */
    public PathResult shortestPath(int source) {
        metrics.startTiming();
        int n = graph.getVertexCount();
        int[] dist = new int[n];
        int[] pred = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        TopologicalSort ts = new TopologicalSort(graph, new MetricsImpl());
        List<Integer> topoOrder = ts.kahnSort();

        if (topoOrder == null) {
            metrics.stopTiming();
            return null;
        }

        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge e : graph.getNeighbors(u)) {
                    int v = e.to;
                    metrics.incrementOperations();
                    if (dist[u] + e.weight < dist[v]) {
                        dist[v] = dist[u] + e.weight;
                        pred[v] = u;
                    }
                }
            }
        }

        metrics.stopTiming();
        return new PathResult(dist, pred, null, 0);
    }

    /**
     * Compute the longest path distances from source in a DAG.
     * Returns PathResult with reconstructed path to the farthest reachable node.
     */
    public PathResult longestPath(int source) {
        metrics.startTiming();
        int n = graph.getVertexCount();
        int[] dist = new int[n];
        int[] pred = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        TopologicalSort ts = new TopologicalSort(graph, new MetricsImpl());
        List<Integer> topoOrder = ts.kahnSort();

        if (topoOrder == null) {
            metrics.stopTiming();
            return null;
        }

        for (int u : topoOrder) {
            if (dist[u] != Integer.MIN_VALUE) {
                for (Graph.Edge e : graph.getNeighbors(u)) {
                    int v = e.to;
                    metrics.incrementOperations();
                    if (dist[u] + e.weight > dist[v]) {
                        dist[v] = dist[u] + e.weight;
                        pred[v] = u;
                    }
                }
            }
        }

        int maxDist = Integer.MIN_VALUE;
        int endNode = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                endNode = i;
            }
        }

        List<Integer> path = reconstructPath(pred, endNode);
        metrics.stopTiming();
        return new PathResult(dist, pred, path, maxDist);
    }

    /**
     * Reconstruct path from predecessors array ending at target.
     * Returns empty list if target is invalid or unreachable.
     */
    public List<Integer> reconstructPath(int[] predecessors, int target) {
        if (target == -1 || predecessors[target] == -1 && target != 0) {
            return new ArrayList<>();
        }

        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = predecessors[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}