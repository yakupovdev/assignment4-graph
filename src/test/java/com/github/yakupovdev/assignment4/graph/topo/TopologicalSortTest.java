package com.github.yakupovdev.assignment4.graph.topo;

import com.github.yakupovdev.assignmment4.graph.Graph;
import com.github.yakupovdev.assignmment4.graph.Metrics;
import com.github.yakupovdev.assignmment4.graph.MetricsImpl;
import com.github.yakupovdev.assignmment4.graph.topo.TopologicalSort;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        Metrics metrics = new MetricsImpl();
        TopologicalSort ts = new TopologicalSort(graph, metrics);
        List<Integer> order = ts.kahnSort();

        assertNotNull(order);
        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    void testDAGWithMultiplePaths() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        Metrics metrics = new MetricsImpl();
        TopologicalSort ts = new TopologicalSort(graph, metrics);
        List<Integer> order = ts.kahnSort();

        assertNotNull(order);
        assertEquals(5, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(3));
        assertTrue(order.indexOf(3) < order.indexOf(4));
    }

    @Test
    void testCyclicGraph() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        Metrics metrics = new MetricsImpl();
        TopologicalSort ts = new TopologicalSort(graph, metrics);
        List<Integer> order = ts.kahnSort();

        assertNull(order);
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true);

        Metrics metrics = new MetricsImpl();
        TopologicalSort ts = new TopologicalSort(graph, metrics);
        List<Integer> order = ts.kahnSort();

        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, order.get(0));
    }
}