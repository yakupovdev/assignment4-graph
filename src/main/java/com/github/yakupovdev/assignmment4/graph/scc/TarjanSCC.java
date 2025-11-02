package com.github.yakupovdev.assignmment4.graph.scc;

import com.github.yakupovdev.assignmment4.graph.Graph;
import com.github.yakupovdev.assignmment4.graph.Metrics;
import java.util.*;

/**
 * TarjanSCC finds strongly connected components using Tarjan's algorithm.
 * Records components and can build the condensation graph.
 */
public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;
    private int index;
    private final Stack<Integer> stack;
    private final int[] indices;
    private final int[] lowLink;
    private final boolean[] onStack;
    private final List<List<Integer>> components;

    /**
     * Construct TarjanSCC for a given graph and metrics collector.
     */
    public TarjanSCC(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.index = 0;
        this.stack = new Stack<>();
        int n = graph.getVertexCount();
        this.indices = new int[n];
        this.lowLink = new int[n];
        this.onStack = new boolean[n];
        this.components = new ArrayList<>();
        Arrays.fill(indices, -1);
    }

    /**
     * Find and return a list of strongly connected components.
     * Each component is a list of vertex indices.
     */
    public List<List<Integer>> findSCCs() {
        metrics.startTiming();
        for (int v = 0; v < graph.getVertexCount(); v++) {
            if (indices[v] == -1) {
                strongConnect(v);
            }
        }
        metrics.stopTiming();
        return components;
    }

    /**
     * Recursive helper implementing Tarjan's strong connect routine.
     */
    private void strongConnect(int v) {
        indices[v] = index;
        lowLink[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;
        metrics.incrementOperations();

        for (Graph.Edge e : graph.getNeighbors(v)) {
            int w = e.to;
            metrics.incrementOperations();
            if (indices[w] == -1) {
                strongConnect(w);
                lowLink[v] = Math.min(lowLink[v], lowLink[w]);
            } else if (onStack[w]) {
                lowLink[v] = Math.min(lowLink[v], indices[w]);
            }
        }

        if (lowLink[v] == indices[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != v);
            components.add(component);
        }
    }

    /**
     * Build the condensation graph (DAG) of the SCCs.
     * Returns a new directed Graph whose vertices are components.
     */
    public Graph buildCondensationGraph() {
        int[] componentId = new int[graph.getVertexCount()];
        for (int i = 0; i < components.size(); i++) {
            for (int v : components.get(i)) {
                componentId[v] = i;
            }
        }

        Graph condensation = new Graph(components.size(), true);
        Set<String> addedEdges = new HashSet<>();

        for (int u = 0; u < graph.getVertexCount(); u++) {
            for (Graph.Edge e : graph.getNeighbors(u)) {
                int v = e.to;
                int compU = componentId[u];
                int compV = componentId[v];
                if (compU != compV) {
                    String edgeKey = compU + "-" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(compU, compV, e.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }
        return condensation;
    }
}