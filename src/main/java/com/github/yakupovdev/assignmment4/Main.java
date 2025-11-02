package com.github.yakupovdev.assignmment4;

import com.github.yakupovdev.assignmment4.graph.Graph;
import com.github.yakupovdev.assignmment4.graph.Metrics;
import com.github.yakupovdev.assignmment4.graph.MetricsImpl;
import com.github.yakupovdev.assignmment4.graph.scc.TarjanSCC;
import com.github.yakupovdev.assignmment4.graph.topo.TopologicalSort;
import com.github.yakupovdev.assignmment4.graph.dagsp.DAGShortestPath;
import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            File dataDir = new File("data");
            File resultsDir = new File("results");
            resultsDir.mkdirs();

            PrintWriter csvWriter = new PrintWriter(new FileWriter("results/metrics.csv"));
            csvWriter.println("Dataset,Algorithm,Nodes,Edges,SCCs,ComponentSizes,Operations,TimeMs,ComponentOrder,OriginalTaskOrder,ShortestPath,ShortestPathLength,LongestPath,LongestPathLength");

            File[] jsonFiles = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles == null) {
                System.out.println("No JSON files found in data directory");
                return;
            }

            for (File file : jsonFiles) {
                processDataset(file, csvWriter);
            }

            csvWriter.close();
            System.out.println("Processing complete. Results written to results/metrics.csv");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processDataset(File file, PrintWriter csvWriter) throws IOException {
        Locale.setDefault(Locale.US);

        System.out.println("Processing: " + file.getName());

        String content = new String(Files.readAllBytes(file.toPath()));
        JsonObject json = JsonParser.parseString(content).getAsJsonObject();

        int n = json.get("n").getAsInt();
        boolean directed = json.get("directed").getAsBoolean();
        JsonArray edges = json.getAsJsonArray("edges");
        int source = json.has("source") ? json.get("source").getAsInt() : 0;

        Graph graph = new Graph(n, directed);
        for (JsonElement edgeElement : edges) {
            JsonObject edge = edgeElement.getAsJsonObject();
            int u = edge.get("u").getAsInt();
            int v = edge.get("v").getAsInt();
            int w = edge.get("w").getAsInt();
            graph.addEdge(u, v, w);
        }

        Metrics sccMetrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, sccMetrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        System.out.println("Strongly Connected Components (SCCs):");
        for (int i = 0; i < sccs.size(); i++) {
            System.out.printf("Component %d: %s%n", i, sccs.get(i));
        }

        int sourceComponent = 0;
        for (int i = 0; i < sccs.size(); i++) {
            if (sccs.get(i).contains(source)) {
                sourceComponent = i;
                break;
            }
        }

        StringBuilder componentSizes = new StringBuilder();
        for (List<Integer> scc : sccs) {
            componentSizes.append(scc.size()).append(";");
        }

        Graph condensation = tarjan.buildCondensationGraph();

        Metrics topoMetrics = new MetricsImpl();
        TopologicalSort topoSort = new TopologicalSort(condensation, topoMetrics);
        List<Integer> topoOrder = topoSort.kahnSort();

        if (topoOrder != null) {
            System.out.println("Topological Order of SCC Components: " + topoOrder);
        } else {
            System.out.println("Cycle detected: No topological ordering possible.");
        }

        StringBuilder topoOrderStr = new StringBuilder();
        StringBuilder originalTaskOrder = new StringBuilder();
        if (topoOrder != null) {
            for (int node : topoOrder) {
                topoOrderStr.append(node).append(";");
            }
            for (int compId : topoOrder) {
                for (int task : sccs.get(compId)) {
                    originalTaskOrder.append(task).append(";");
                }
            }
        }

        csvWriter.printf(Locale.US, "%s,SCC,%d,%d,%d,%s,%d,%.3f,%s,%s,%s,%s,%s,%s%n",
                file.getName(), n, edges.size(), sccs.size(),
                componentSizes.toString(),
                sccMetrics.getOperationCount(),
                sccMetrics.getElapsedMillis(),
                "", "", "", "", "", "");

        csvWriter.printf(Locale.US, "%s,TopologicalSort,%d,%d,%s,%s,%d,%.3f,%s,%s,%s,%s,%s,%s%n",
                file.getName(), condensation.getVertexCount(), edges.size(), "", "",
                topoMetrics.getOperationCount(),
                topoMetrics.getElapsedMillis(),
                topoOrderStr.toString(),
                originalTaskOrder.toString(),
                "", "", "", "");

        if (topoOrder != null && topoOrder.size() == condensation.getVertexCount()) {
            Metrics shortestMetrics = new MetricsImpl();
            DAGShortestPath dagsp = new DAGShortestPath(condensation, shortestMetrics);
            DAGShortestPath.PathResult shortest = dagsp.shortestPath(sourceComponent);

            int minDist = Integer.MAX_VALUE;
            int minTarget = -1;
            if (shortest != null) {
                for (int i = 0; i < shortest.distances.length; i++) {
                    if (shortest.distances[i] != Integer.MAX_VALUE &&
                            i != sourceComponent &&
                            shortest.distances[i] < minDist) {
                        minDist = shortest.distances[i];
                        minTarget = i;
                    }
                }
            }

            List<Integer> shortestPath = minTarget != -1 ? dagsp.reconstructPath(shortest.predecessors, minTarget) : new ArrayList<>();
            System.out.println("Shortest path from source component:");
            System.out.println(shortestPath + " with length " + minDist);

            StringBuilder shortestPathStr = new StringBuilder();
            for (int node : shortestPath) {
                shortestPathStr.append(node).append(";");
            }

            Metrics longestMetrics = new MetricsImpl();
            DAGShortestPath dagspLongest = new DAGShortestPath(condensation, longestMetrics);
            DAGShortestPath.PathResult longest = dagspLongest.longestPath(sourceComponent);

            System.out.println("Longest path from source component:");
            if (longest != null && longest.path != null) {
                System.out.println(longest.path + " with length " + longest.pathLength);
            } else {
                System.out.println("No path found.");
            }

            StringBuilder longestPathStr = new StringBuilder();
            if (longest != null && longest.path != null) {
                for (int node : longest.path) {
                    longestPathStr.append(node).append(";");
                }
            }

            csvWriter.printf(Locale.US, "%s,ShortestPath,%d,%d,%s,%s,%d,%.3f,%s,%s,%s,%d,%s,%s%n",
                    file.getName(), condensation.getVertexCount(), edges.size(), "", "",
                    shortestMetrics.getOperationCount(),
                    shortestMetrics.getElapsedMillis(),
                    "", "",
                    shortestPathStr.toString(),
                    minDist == Integer.MAX_VALUE ? -1 : minDist,
                    "", "");

            csvWriter.printf(Locale.US, "%s,LongestPath,%d,%d,%s,%s,%d,%.3f,%s,%s,%s,%s,%s,%d%n",
                    file.getName(), condensation.getVertexCount(), edges.size(), "", "",
                    longestMetrics.getOperationCount(),
                    longestMetrics.getElapsedMillis(),
                    "", "", "", "",
                    longestPathStr.toString(),
                    longest != null ? longest.pathLength : 0);
        }
    }
}
