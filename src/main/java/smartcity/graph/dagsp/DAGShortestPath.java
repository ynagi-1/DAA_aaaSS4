package smartcity.graph.dagsp;

import smartcity.model.Graph;
import smartcity.model.Edge;
import smartcity.graph.topo.KahnTopological;
import java.util.*;

public class DAGShortestPath implements Metrics {
    private long dfsCount;
    private long edgeRelaxations;
    private long kahnOperations;

    public static class Result {
        public double[] distances;
        public int[] predecessors;
        public List<Integer> path;

        public Result(double[] distances, int[] predecessors, List<Integer> path) {
            this.distances = distances;
            this.predecessors = predecessors;
            this.path = path;
        }
    }

    public Result findShortestPath(Graph graph, int source, int target) {
        reset();


        KahnTopological topological = new KahnTopological();
        List<Integer> topoOrder = topological.topologicalSort(graph);


        this.dfsCount += topological.getDFSCount();
        this.edgeRelaxations += topological.getEdgeRelaxations();
        this.kahnOperations += topological.getKahnOperations();

        double[] dist = new double[graph.getN()];
        int[] pred = new int[graph.getN()];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(pred, -1);
        dist[source] = 0;



        for (int u : topoOrder) {
            incrementDFSCount();
            if (dist[u] != Double.POSITIVE_INFINITY) {
                for (var edge : graph.getEdges(u)) {
                    incrementEdgeRelaxation();
                    int v = edge.getTo();
                    double weight = edge.getWeight();
                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        pred[v] = u;
                    }
                }
            }
        }


        List<Integer> path = Collections.emptyList();
        if (target != -1 && target < graph.getN() && dist[target] != Double.POSITIVE_INFINITY) {
            path = reconstructPath(pred, source, target);
        }

        return new Result(dist, pred, path);
    }

    private List<Integer> reconstructPath(int[] pred, int source, int target) {
        List<Integer> path = new ArrayList<>();
        if (pred[target] == -1 && target != source) {
            return path;
        }

        for (int v = target; v != -1; v = pred[v]) {
            path.add(v);
            if (v == source) break;
        }
        Collections.reverse(path);


        if (path.isEmpty() || path.get(0) != source) {
            return Collections.emptyList();
        }

        return path;
    }


    @Override
    public void incrementDFSCount() { dfsCount++; }
    @Override
    public void incrementEdgeRelaxation() { edgeRelaxations++; }
    @Override
    public void incrementKahnOperation() { kahnOperations++; }
    @Override
    public long getDFSCount() { return dfsCount; }
    @Override
    public long getEdgeRelaxations() { return edgeRelaxations; }
    @Override
    public long getKahnOperations() { return kahnOperations; }
    @Override
    public void reset() {
        dfsCount = 0;
        edgeRelaxations = 0;
        kahnOperations = 0;
    }
}