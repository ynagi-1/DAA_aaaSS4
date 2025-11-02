package smartcity.util;

import smartcity.model.Graph;
import smartcity.model.Edge;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;

public class DataGenerator {
    private Random random;

    public DataGenerator() {
        this.random = new Random(42); // Fixed seed for reproducibility
    }

    public DataGenerator(long seed) {
        this.random = new Random(seed);
    }

    public Graph generateSmallGraph(int variant) {
        int n = 6 + random.nextInt(5); // 6-10 nodes
        Graph graph = new Graph(n, true);
        graph.setWeightModel("edge");

        switch (variant) {
            case 1:
                generateSimpleCyclic(graph);
                break;
            case 2:
                generatePureDAG(graph);
                break;
            case 3:
                generateMixedStructure(graph);
                break;
        }

        return graph;
    }

    public Graph generateMediumGraph(int variant) {
        int n = 10 + random.nextInt(11); // 10-20 nodes
        Graph graph = new Graph(n, true);
        graph.setWeightModel("edge");

        switch (variant) {
            case 1:
                generateMultipleSCCs(graph);
                break;
            case 2:
                generateSparseMixed(graph);
                break;
            case 3:
                generateDenseMixed(graph);
                break;
        }

        return graph;
    }

    public Graph generateLargeGraph(int variant) {
        int n = 20 + random.nextInt(31); // 20-50 nodes
        Graph graph = new Graph(n, true);
        graph.setWeightModel("edge");

        switch (variant) {
            case 1:
                generatePerformanceTestSparse(graph);
                break;
            case 2:
                generatePerformanceTestDense(graph);
                break;
            case 3:
                generateComplexCycles(graph);
                break;
        }

        return graph;
    }

    private void generateSimpleCyclic(Graph graph) {
        int n = graph.getN();


        if (n >= 3) {

            graph.addEdge(0, 1, randomWeight());
            graph.addEdge(1, 2, randomWeight());
            graph.addEdge(2, 0, randomWeight());
        }

        if (n >= 6) {

            graph.addEdge(3, 4, randomWeight());
            graph.addEdge(4, 5, randomWeight());
            graph.addEdge(5, 3, randomWeight());
        }


        addRandomEdges(graph, 0.2);


        graph.setSource(0);
    }

    private void generatePureDAG(Graph graph) {
        int n = graph.getN();


        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < Math.min(u + 4, n); v++) {
                if (random.nextDouble() < 0.6) {
                    graph.addEdge(u, v, randomWeight());
                }
            }
        }

        graph.setSource(0);
    }

    private void generateMixedStructure(Graph graph) {
        int n = graph.getN();


        if (n >= 3) {
            graph.addEdge(0, 1, randomWeight());
            graph.addEdge(1, 2, randomWeight());
            graph.addEdge(2, 0, randomWeight());
        }


        for (int u = 3; u < n; u++) {
            for (int v = u + 1; v < Math.min(u + 3, n); v++) {
                if (random.nextDouble() < 0.7) {
                    graph.addEdge(u, v, randomWeight());
                }
            }
        }


        addRandomEdges(graph, 0.15);

        graph.setSource(0);
    }

    private void generateMultipleSCCs(Graph graph) {
        int n = graph.getN();


        if (n >= 9) {
            createSCC(graph, 0, 2); // SCC 1 (3 nodes)
            createSCC(graph, 3, 5); // SCC 2 (3 nodes)
            createSCC(graph, 6, 8); // SCC 3 (3 nodes)


            graph.addEdge(2, 3, randomWeight());
            graph.addEdge(5, 6, randomWeight());
        } else {

            createSCC(graph, 0, 1); // SCC 1 (2 nodes)
            createSCC(graph, 2, 3); // SCC 2 (2 nodes)
            graph.addEdge(1, 2, randomWeight());
        }


        int start = (n >= 9) ? 9 : 4;
        for (int i = start; i < n; i++) {

            if (i > start && random.nextDouble() < 0.5) {
                graph.addEdge(i-1, i, randomWeight());
            }
        }


        addRandomEdges(graph, 0.1);

        graph.setSource(0);
    }

    private void generateSparseMixed(Graph graph) {
        int n = graph.getN();


        for (int i = 0; i < n * 1.5; i++) {
            int u = random.nextInt(n);
            int v = random.nextInt(n);
            if (u != v) {
                graph.addEdge(u, v, randomWeight());
            }
        }

        graph.setSource(0);
    }

    private void generateDenseMixed(Graph graph) {
        int n = graph.getN();

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && random.nextDouble() < 0.3) {
                    graph.addEdge(u, v, randomWeight());
                }
            }
        }

        graph.setSource(0);
    }

    private void generatePerformanceTestSparse(Graph graph) {
        int n = graph.getN();


        for (int u = 0; u < n; u++) {
            int edgesPerNode = 1 + random.nextInt(3);
            for (int j = 0; j < edgesPerNode; j++) {
                int v = u + 1 + random.nextInt(5);
                if (v < n) {
                    graph.addEdge(u, v, randomWeight());
                }
            }
        }

        graph.setSource(0);
    }

    private void generatePerformanceTestDense(Graph graph) {
        int n = graph.getN();


        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < Math.min(u + 10, n); v++) {
                if (random.nextDouble() < 0.7) {
                    graph.addEdge(u, v, randomWeight());
                }
            }
        }

        graph.setSource(0);
    }

    private void generateComplexCycles(Graph graph) {
        int n = graph.getN();


        for (int i = 0; i < n - 1; i++) {
            graph.addEdge(i, i + 1, randomWeight());
        }
        graph.addEdge(n - 1, 0, randomWeight()); // Big cycle

        for (int i = 0; i < n / 2; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);
            int c = random.nextInt(n);
            if (a != b && b != c && a != c) {
                graph.addEdge(a, b, randomWeight());
                graph.addEdge(b, c, randomWeight());
                graph.addEdge(c, a, randomWeight());
            }
        }

        graph.setSource(0);
    }

    private void createSCC(Graph graph, int start, int end) {

        for (int i = start; i < end; i++) {
            graph.addEdge(i, i + 1, randomWeight());
        }
        graph.addEdge(end, start, randomWeight());
    }

    private void addRandomEdges(Graph graph, double probability) {
        int n = graph.getN();
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && random.nextDouble() < probability) {
                    graph.addEdge(u, v, randomWeight());
                }
            }
        }
    }

    private double randomWeight() {
        return 1 + random.nextInt(10); // Weights 1-10
    }

    public void saveGraphToJSON(Graph graph, String filename, int source) throws IOException {

        java.nio.file.Path path = Paths.get(filename);
        java.nio.file.Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        JSONObject json = new JSONObject();
        json.put("directed", graph.isDirected());
        json.put("n", graph.getN());
        json.put("source", source);
        json.put("weight_model", graph.getWeightModel());

        JSONArray edges = new JSONArray();
        for (int u = 0; u < graph.getN(); u++) {
            for (Edge edge : graph.getEdges(u)) {
                JSONObject edgeObj = new JSONObject();
                edgeObj.put("u", edge.getFrom());
                edgeObj.put("v", edge.getTo());
                edgeObj.put("w", edge.getWeight());
                edges.put(edgeObj);
            }
        }
        json.put("edges", edges);

        Files.write(path, json.toString(2).getBytes());
    }


    public Graph generateCustomGraph(int nodes, int edges, boolean hasCycles, boolean isDense) {
        Graph graph = new Graph(nodes, true);
        graph.setWeightModel("edge");
        graph.setSource(0);

        if (hasCycles) {

            if (nodes >= 3) {
                graph.addEdge(0, 1, randomWeight());
                graph.addEdge(1, 2, randomWeight());
                graph.addEdge(2, 0, randomWeight());
            }
        }


        int edgesAdded = graph.getEdgeCount();
        double density = isDense ? 0.4 : 0.1;

        while (edgesAdded < edges) {
            int u = random.nextInt(nodes);
            int v = random.nextInt(nodes);
            if (u != v && !edgeExists(graph, u, v)) {
                graph.addEdge(u, v, randomWeight());
                edgesAdded++;
            }


            if (edgesAdded >= nodes * (nodes - 1) * 0.9) {
                break;
            }
        }

        return graph;
    }

    private boolean edgeExists(Graph graph, int u, int v) {
        for (Edge edge : graph.getEdges(u)) {
            if (edge.getTo() == v) {
                return true;
            }
        }
        return false;
    }
}