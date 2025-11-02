package com.github.yakupovdev.assignment4.graph.scc;

import com.github.yakupovdev.assignmment4.graph.Graph;
import com.github.yakupovdev.assignmment4.graph.Metrics;
import com.github.yakupovdev.assignmment4.graph.MetricsImpl;
import com.github.yakupovdev.assignmment4.graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TarjanSCCTest {

    @Test
    void testSimpleCycle() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        Metrics metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
    }

    @Test
    void testMultipleSCCs() {
        Graph graph = new Graph(8, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 3, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(6, 7, 1);

        Metrics metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertTrue(sccs.size() >= 3);
    }

    @Test
    void testDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        Metrics metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(4, sccs.size());
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true);

        Metrics metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());
    }

    @Test
    void testCondensationGraph() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        Metrics metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        tarjan.findSCCs();
        Graph condensation = tarjan.buildCondensationGraph();

        assertTrue(condensation.getVertexCount() <= 5);
    }
}