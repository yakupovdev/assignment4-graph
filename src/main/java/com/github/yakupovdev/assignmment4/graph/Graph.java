package com.github.yakupovdev.assignmment4.graph;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    private final boolean directed;

    public static class Edge {
        public final int to;
        public final int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        adj.get(u).add(new Edge(v, weight));
        if (!directed) {
            adj.get(v).add(new Edge(u, weight));
        }
    }

    public int getVertexCount() {
        return n;
    }

    public List<Edge> getNeighbors(int u) {
        return adj.get(u);
    }

    public boolean isDirected() {
        return directed;
    }

    public Graph getTranspose() {
        Graph transpose = new Graph(n, directed);
        for (int u = 0; u < n; u++) {
            for (Edge e : adj.get(u)) {
                transpose.addEdge(e.to, u, e.weight);
            }
        }
        return transpose;
    }
}