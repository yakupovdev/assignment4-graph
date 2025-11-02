package com.github.yakupovdev.assignment4.graph.dagsp;

import com.github.yakupovdev.assignmment4.graph.Graph;
import com.github.yakupovdev.assignmment4.graph.Metrics;
import com.github.yakupovdev.assignmment4.graph.MetricsImpl;
import com.github.yakupovdev.assignmment4.graph.dagsp.DAGShortestPath;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DAGShortestPathTest {

    @Test
    void testShortestPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 4);

        Metrics metrics = new MetricsImpl();
        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        DAGShortestPath.PathResult result = dagsp.shortestPath(0);

        assertNotNull(result);
        assertEquals(0, result.distances[0]);
        assertEquals(3, result.distances[1]);
        assertEquals(5, result.distances[2]);
        assertEquals(9, result.distances[3]);
    }

    @Test
    void testLongestPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 4);

        Metrics metrics = new MetricsImpl();
        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        DAGShortestPath.PathResult result = dagsp.longestPath(0);

        assertNotNull(result);
        assertEquals(9, result.pathLength);
        assertNotNull(result.path);
        assertTrue(result.path.size() > 0);
    }

    @Test
    void testMultiplePaths() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 3);

        Metrics metrics = new MetricsImpl();
        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        DAGShortestPath.PathResult result = dagsp.shortestPath(0);

        assertNotNull(result);
        assertEquals(0, result.distances[0]);
        assertEquals(3, result.distances[2]);
    }

    @Test
    void testSingleNode() {
        Graph graph = new Graph(1, true);

        Metrics metrics = new MetricsImpl();
        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        DAGShortestPath.PathResult result = dagsp.shortestPath(0);

        assertNotNull(result);
        assertEquals(0, result.distances[0]);
    }

    @Test
    void testPathReconstruction() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        Metrics metrics = new MetricsImpl();
        DAGShortestPath dagsp = new DAGShortestPath(graph, metrics);
        DAGShortestPath.PathResult result = dagsp.longestPath(0);

        assertNotNull(result);
        assertNotNull(result.path);
        assertEquals(0, result.path.get(0));
    }
}