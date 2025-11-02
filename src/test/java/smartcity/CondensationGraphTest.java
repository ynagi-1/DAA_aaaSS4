package smartcity;

import org.junit.Test;
import static org.junit.Assert.*;
import smartcity.model.Graph;
import smartcity.graph.scc.TarjanSCC;
import smartcity.graph.scc.CondensationGraph;
import java.util.*;

public class CondensationGraphTest {

    @Test
    public void testCondensationGraphConstruction() {
        Graph graph = new Graph(6, true);
        // Two SCCs: {0,1,2} and {3,4}, plus isolated node 5
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        graph.addEdge(1, 3, 1); // Edge between SCCs

        TarjanSCC tarjan = new TarjanSCC(graph);
        tarjan.findSCCs();
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        assertNotNull(cg);
        assertEquals(3, cg.getCondensationGraph().getN()); // 3 components

        // Check component sizes
        assertEquals(3, cg.getComponentSize(0));
        assertEquals(2, cg.getComponentSize(1));
        assertEquals(1, cg.getComponentSize(2));

        // Check edges between components
        assertTrue(cg.getCondensationGraph().getEdgeCount() > 0);
    }

    @Test
    public void testComponentStatistics() {
        Graph graph = new Graph(8, true);
        // Multiple SCCs of different sizes
        graph.addEdge(0, 1, 1); graph.addEdge(1, 0, 1); // SCC size 2
        graph.addEdge(2, 3, 1); graph.addEdge(3, 4, 1); graph.addEdge(4, 2, 1); // SCC size 3
        graph.addEdge(5, 6, 1); graph.addEdge(6, 5, 1); // SCC size 2
        // Node 7 is isolated

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph.ComponentStatistics stats = tarjan.analyzeComponents();

        assertEquals(4, stats.getTotalComponents());
        assertEquals(1, stats.getTrivialComponents()); // Node 7
        assertEquals(3, stats.getCyclicComponents()); // Three cyclic SCCs
        assertEquals(3, stats.getLargestComponentSize()); // SCC {2,3,4}
    }

    @Test
    public void testSourceAndSinkComponents() {
        Graph graph = new Graph(7, true);
        // Component structure: 0 -> 1 -> 2, plus isolated 6
        graph.addEdge(0, 1, 1); // SCC 0 -> SCC 1
        graph.addEdge(1, 2, 1); // SCC 1 -> SCC 2
        graph.addEdge(3, 4, 1); graph.addEdge(4, 3, 1); // SCC 3
        graph.addEdge(5, 5, 1); // Self-loop

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        List<Integer> sources = cg.findSourceComponents();
        List<Integer> sinks = cg.findSinkComponents();

        // Sources should include components without incoming edges
        // Sinks should include components without outgoing edges
        assertTrue(sources.size() > 0);
        assertTrue(sinks.size() > 0);
    }

    @Test
    public void testComponentSubgraph() {
        Graph graph = new Graph(5, true);
        // Create a non-trivial SCC
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 0, 1);
        graph.addEdge(0, 3, 4);
        graph.addEdge(3, 4, 5);

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        // Get the cyclic component subgraph
        Graph subgraph = cg.getComponentSubgraph(0); // Assuming {0,1,2} is component 0

        assertNotNull(subgraph);
        assertEquals(3, subgraph.getN()); // Should have 3 vertices
        assertTrue(subgraph.getEdgeCount() >= 3); // Should have at least the cycle edges
    }

    @Test
    public void testVisualization() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1); // Creates cycle 1-2-3

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        String visualization = cg.visualize();
        assertNotNull(visualization);
        assertTrue(visualization.contains("Condensation Graph"));
        assertTrue(visualization.contains("Component"));
    }
}