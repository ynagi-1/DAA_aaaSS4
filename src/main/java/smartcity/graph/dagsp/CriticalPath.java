package smartcity.graph.dagsp;

import smartcity.model.Graph;
import smartcity.model.Edge;
import java.util.*;

public class CriticalPath {
    private DAGShortestPath shortestPath;

    public CriticalPath() {
        this.shortestPath = new DAGShortestPath();
    }

    public DAGShortestPath.Result findCriticalPath(Graph graph, int source, int target) {

        Graph negatedGraph = createNegatedGraph(graph);


        DAGShortestPath.Result result = shortestPath.findShortestPath(negatedGraph, source, target);


        for (int i = 0; i < result.distances.length; i++) {
            if (result.distances[i] != Double.POSITIVE_INFINITY) {
                result.distances[i] = -result.distances[i];
            }
        }

        return result;
    }

    private Graph createNegatedGraph(Graph original) {
        Graph negated = new Graph(original.getN(), original.isDirected());
        negated.setWeightModel(original.getWeightModel());

        for (int u = 0; u < original.getN(); u++) {
            for (var edge : original.getEdges(u)) {
                negated.addEdge(edge.getFrom(), edge.getTo(), -edge.getWeight());
            }
        }
        return negated;
    }

    public DAGShortestPath getShortestPath() {
        return shortestPath;
    }
}