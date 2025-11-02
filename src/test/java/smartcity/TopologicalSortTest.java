package smartcity;

import org.junit.Test;
import static org.junit.Assert.*;
import smartcity.model.Graph;
import smartcity.graph.topo.TopologicalSort;
import smartcity.graph.topo.KahnTopological;
import java.util.*;

public class TopologicalSortTest {

    @Test
    public void testSimpleDAG() {
        Graph graph = new Graph(6, true);
        graph.addEdge(5, 2, 1);
        graph.addEdge(5, 0, 1);
        graph.addEdge(4, 0, 1);
        graph.addEdge(4, 1, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1);

        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.topologicalSort(graph);

        assertEquals(6, order.size());
        assertTrue(topo.isValidTopologicalOrder(graph, order));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCyclicGraph() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1); // Cycle

        TopologicalSort topo = new TopologicalSort();
        topo.topologicalSort(graph);
    }

    @Test
    public void testKahnAlgorithm() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        KahnTopological kahn = new KahnTopological();
        List<Integer> order = kahn.topologicalSort(graph);

        assertEquals(4, order.size());
        assertTrue(isValidTopologicalOrder(graph, order));


        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));

        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testEmptyGraph() {
        Graph graph = new Graph(5, true);

        TopologicalSort topo = new TopologicalSort();
        List<Integer> order = topo.topologicalSort(graph);

        assertEquals(5, order.size());

        assertTrue(topo.isValidTopologicalOrder(graph, order));
    }

    @Test
    public void testSinglePath() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        KahnTopological kahn = new KahnTopological();
        List<Integer> order = kahn.topologicalSort(graph);

        assertEquals(5, order.size());
        assertEquals(Arrays.asList(0, 1, 2, 3, 4), order);
    }

    @Test
    public void testFindSourcesAndSinks() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);

        TopologicalSort topo = new TopologicalSort();
        List<Integer> sources = topo.findSources(graph);
        List<Integer> sinks = topo.findSinks(graph);

        assertEquals(Arrays.asList(0), sources);
        assertEquals(Arrays.asList(4), sinks);
    }

    @Test
    public void testVertexLevels() {
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(3, 5, 1);

        TopologicalSort topo = new TopologicalSort();
        int[] levels = topo.getVertexLevels(graph);

        assertEquals(0, levels[0]); // Source
        assertEquals(1, levels[1]); // Level 1
        assertEquals(1, levels[2]); // Level 1
        assertEquals(2, levels[3]); // Level 2
        assertEquals(3, levels[4]); // Level 3
        assertEquals(3, levels[5]); // Level 3
    }

    @Test
    public void testMultipleValidOrders() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        TopologicalSort topo = new TopologicalSort();
        List<List<Integer>> allOrders = topo.findAllTopologicalOrders(graph);


        assertTrue(allOrders.size() > 1);


        for (List<Integer> order : allOrders) {
            assertTrue(topo.isValidTopologicalOrder(graph, order));
        }
    }

    private boolean isValidTopologicalOrder(Graph graph, List<Integer> order) {

        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            position.put(order.get(i), i);
        }


        for (int u = 0; u < graph.getN(); u++) {
            for (var edge : graph.getEdges(u)) {
                int v = edge.getTo();
                if (position.get(u) >= position.get(v)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Test
    public void testAlgorithmComparison() {
        Graph graph = new Graph(8, true);
        // Create a medium-sized DAG
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(1, 4, 1);
        graph.addEdge(2, 4, 1);
        graph.addEdge(2, 5, 1);
        graph.addEdge(3, 6, 1);
        graph.addEdge(4, 6, 1);
        graph.addEdge(5, 7, 1);
        graph.addEdge(6, 7, 1);

        TopologicalSort topo = new TopologicalSort();

        List<Integer> dfsOrder = topo.dfsTopologicalSort(graph);
        topo.reset();
        List<Integer> kahnOrder = topo.kahnTopologicalSort(graph);

        assertTrue(topo.isValidTopologicalOrder(graph, dfsOrder));
        assertTrue(topo.isValidTopologicalOrder(graph, kahnOrder));
        assertEquals(graph.getN(), dfsOrder.size());
        assertEquals(graph.getN(), kahnOrder.size());
    }
}