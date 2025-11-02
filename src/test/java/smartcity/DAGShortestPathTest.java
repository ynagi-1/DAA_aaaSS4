package smartcity;

import org.junit.Test;
import static org.junit.Assert.*;
import smartcity.model.Graph;
import smartcity.graph.dagsp.DAGShortestPath;
import smartcity.graph.dagsp.CriticalPath;

import java.util.Arrays;

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

        assertEquals(8.0, result.distances[5], 0.001);
        assertEquals(5, result.path.size());
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

        assertEquals(12.0, result.distances[4], 0.001); // 0->1->3->4 = 3+4+5=12
    }
}