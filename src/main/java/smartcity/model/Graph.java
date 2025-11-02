package smartcity.model;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    private final List<List<Edge>> reverseAdj;
    private final boolean directed;
    private String weightModel;
    private int source;
    private Map<Integer, Vertex> vertices;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        this.reverseAdj = new ArrayList<>();
        this.vertices = new HashMap<>();

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
            reverseAdj.add(new ArrayList<>());
            vertices.put(i, new Vertex(i));
        }
        this.source = -1;
    }

    public void addEdge(int u, int v, double weight) {
        Edge edge = new Edge(u, v, weight);
        adj.get(u).add(edge);
        reverseAdj.get(v).add(new Edge(v, u, weight));

        if (!directed) {
            adj.get(v).add(new Edge(v, u, weight));
            reverseAdj.get(u).add(new Edge(u, v, weight));
        }
    }

    public List<Edge> getEdges(int u) {
        return adj.get(u);
    }

    public List<Edge> getReverseEdges(int u) {
        return reverseAdj.get(u);
    }

    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adj) {
            count += edges.size();
        }
        return count;
    }

    // Getters and setters
    public int getN() { return n; }
    public boolean isDirected() { return directed; }
    public String getWeightModel() { return weightModel; }
    public void setWeightModel(String model) { this.weightModel = model; }
    public int getSource() { return source; }
    public void setSource(int source) { this.source = source; }
    public List<List<Edge>> getAdjacencyList() { return adj; }
    public List<List<Edge>> getReverseAdjacencyList() { return reverseAdj; }


    public void setVertex(int id, Vertex vertex) {
        if (id >= 0 && id < n) {
            vertices.put(id, vertex);
        }
    }


    public Vertex getVertex(int id) {
        return vertices.get(id);
    }


    public Map<Integer, Vertex> getVertices() {
        return Collections.unmodifiableMap(vertices);
    }


    public String getVertexTaskType(int id) {
        Vertex vertex = vertices.get(id);
        return vertex != null ? vertex.getTaskType() : "unknown";
    }


    public double getVertexDuration(int id) {
        Vertex vertex = vertices.get(id);
        return vertex != null ? vertex.getDuration() : 1.0;
    }


    public int getVertexPriority(int id) {
        Vertex vertex = vertices.get(id);
        return vertex != null ? vertex.getPriority() : 1;
    }


    public String getVertexName(int id) {
        Vertex vertex = vertices.get(id);
        return vertex != null ? vertex.getName() : "Task_" + id;
    }


    public void initializeSmartCityVertices() {
        Random random = new Random(42);
        String[] taskTypes = {"street_cleaning", "repair", "maintenance", "analytics"};
        double[] durations = {1.0, 2.0, 3.0, 4.0, 5.0};
        int[] priorities = {1, 2, 3};

        for (int i = 0; i < n; i++) {
            String taskType = taskTypes[random.nextInt(taskTypes.length)];
            double duration = durations[random.nextInt(durations.length)];
            int priority = priorities[random.nextInt(priorities.length)];

            Vertex vertex = new Vertex(i, "Task_" + i, taskType, duration, priority);
            vertices.put(i, vertex);
        }
    }


    public Map<String, Integer> getTaskTypeStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        for (Vertex vertex : vertices.values()) {
            String taskType = vertex.getTaskType();
            stats.put(taskType, stats.getOrDefault(taskType, 0) + 1);
        }
        return stats;
    }


    public double getAverageTaskDuration() {
        double totalDuration = 0;
        for (Vertex vertex : vertices.values()) {
            totalDuration += vertex.getDuration();
        }
        return vertices.isEmpty() ? 0 : totalDuration / vertices.size();
    }
}