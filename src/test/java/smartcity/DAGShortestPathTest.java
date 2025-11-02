package smartcity;

import org.junit.Test;
import static org.junit.Assert.*;
import smartcity.model.Graph;
import smartcity.graph.dagsp.DAGShortestPath;
import smartcity.graph.dagsp.CriticalPath;
import java.util.Arrays;
import java.util.List;

public class DAGShortestPathTest {

    @Test
    public void testShortestPath() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 6);
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 4);
        graph.addEdge(3, 5, 2);
        graph.addEdge(4, 5, 1);

        DAGShortestPath shortestPath = new DAGShortestPath();
        DAGShortestPath.Result result = shortestPath.findShortestPath(graph, 0, 5);


        assertEquals(7.0, result.distances[5], 0.001);


        assertEquals(Arrays.asList(0, 2, 3, 5), result.path);
    }

    @Test
    public void testCriticalPath() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 5);

        CriticalPath criticalPath = new CriticalPath();
        DAGShortestPath.Result result = criticalPath.findCriticalPath(graph, 0, 4);


        assertEquals(12.0, result.distances[4], 0.001);
    }

    @Test
    public void testSimpleChain() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 1);

        DAGShortestPath shortestPath = new DAGShortestPath();
        DAGShortestPath.Result result = shortestPath.findShortestPath(graph, 0, 3);


        assertEquals(6.0, result.distances[3], 0.001);
        assertEquals(Arrays.asList(0, 1, 2, 3), result.path);
    }

    @Test
    public void testMultiplePaths() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 5);
        graph.addEdge(3, 4, 3);

        DAGShortestPath shortestPath = new DAGShortestPath();
        DAGShortestPath.Result result = shortestPath.findShortestPath(graph, 0, 4);


        assertEquals(9.0, result.distances[4], 0.001);
    }

    @Test
    public void testUnreachableNode() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);


        DAGShortestPath shortestPath = new DAGShortestPath();
        DAGShortestPath.Result result = shortestPath.findShortestPath(graph, 0, 3);

        assertEquals(Double.POSITIVE_INFINITY, result.distances[3], 0.001);
        assertTrue(result.path.isEmpty());
    }

    @Test
    public void testSingleNode() {
        Graph graph = new Graph(1, true);

        DAGShortestPath shortestPath = new DAGShortestPath();
        DAGShortestPath.Result result = shortestPath.findShortestPath(graph, 0, 0);

        assertEquals(0.0, result.distances[0], 0.001);
        assertEquals(List.of(0), result.path);
    }

    @Test
    public void testCriticalPathWithMultipleOptions() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 5);
        graph.addEdge(3, 5, 2);
        graph.addEdge(4, 5, 1);

        CriticalPath criticalPath = new CriticalPath();
        DAGShortestPath.Result result = criticalPath.findCriticalPath(graph, 0, 5);


        assertEquals(11.0, result.distances[5], 0.001);
    }

    @Test
    public void testNegativeWeightsInCriticalPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, -2);
        graph.addEdge(2, 3, 4);

        CriticalPath criticalPath = new CriticalPath();
        DAGShortestPath.Result result = criticalPath.findCriticalPath(graph, 0, 3);


        assertEquals(7.0, result.distances[3], 0.001);
    }

    @Test
    public void testAllDistances() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 2);
        graph.addEdge(1, 3, 6);
        graph.addEdge(2, 3, 3);

        DAGShortestPath shortestPath = new DAGShortestPath();
        DAGShortestPath.Result result = shortestPath.findShortestPath(graph, 0, -1);

        assertEquals(0.0, result.distances[0], 0.001);
        assertEquals(1.0, result.distances[1], 0.001);
        assertEquals(3.0, result.distances[2], 0.001);
        assertEquals(6.0, result.distances[3], 0.001);
    }

    @Test
    public void testComplexCriticalPath() {
        Graph graph = new Graph(7, true);

        graph.addEdge(0, 1, 3);  // Task A
        graph.addEdge(0, 2, 2);  // Task B
        graph.addEdge(1, 3, 4);  // Task C
        graph.addEdge(2, 3, 1);  // Task D
        graph.addEdge(3, 4, 5);  // Task E
        graph.addEdge(3, 5, 2);  // Task F
        graph.addEdge(4, 6, 3);  // Task G
        graph.addEdge(5, 6, 4);  // Task H

        CriticalPath criticalPath = new CriticalPath();
        DAGShortestPath.Result result = criticalPath.findCriticalPath(graph, 0, 6);


        assertEquals(15.0, result.distances[6], 0.001);
    }
}