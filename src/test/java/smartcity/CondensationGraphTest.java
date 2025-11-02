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

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        graph.addEdge(1, 3, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        tarjan.findSCCs();
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        assertNotNull(cg);

        assertEquals(3, cg.getCondensationGraph().getN());

        boolean foundSize3 = false;
        boolean foundSize2 = false;
        boolean foundSize1 = false;

        for (int i = 0; i < cg.getCondensationGraph().getN(); i++) {
            int size = cg.getComponentSize(i);
            if (size == 3) foundSize3 = true;
            if (size == 2) foundSize2 = true;
            if (size == 1) foundSize1 = true;
        }

        assertTrue("Should have component of size 3", foundSize3);
        assertTrue("Should have component of size 2", foundSize2);
        assertTrue("Should have component of size 1", foundSize1);


        assertTrue(cg.getCondensationGraph().getEdgeCount() > 0);
    }

    @Test
    public void testComponentStatistics() {
        Graph graph = new Graph(8, true);

        graph.addEdge(0, 1, 1); graph.addEdge(1, 0, 1);
        graph.addEdge(2, 3, 1); graph.addEdge(3, 4, 1); graph.addEdge(4, 2, 1);
        graph.addEdge(5, 6, 1); graph.addEdge(6, 5, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph.ComponentStatistics stats = tarjan.analyzeComponents();

        assertEquals(4, stats.getTotalComponents());
        assertEquals(1, stats.getTrivialComponents());
        assertEquals(3, stats.getCyclicComponents());
        assertEquals(3, stats.getLargestComponentSize());
    }

    @Test
    public void testSourceAndSinkComponents() {
        Graph graph = new Graph(7, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(3, 4, 1); graph.addEdge(4, 3, 1);
        graph.addEdge(5, 5, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        List<Integer> sources = cg.findSourceComponents();
        List<Integer> sinks = cg.findSinkComponents();

        assertTrue(sources.size() > 0);
        assertTrue(sinks.size() > 0);
    }

    @Test
    public void testComponentSubgraph() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 0, 1);
        graph.addEdge(0, 3, 4);
        graph.addEdge(3, 4, 5);

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        int cyclicComponentId = -1;
        for (int i = 0; i < cg.getCondensationGraph().getN(); i++) {
            if (cg.getComponentSize(i) > 1) {
                cyclicComponentId = i;
                break;
            }
        }

        assertTrue("Should find cyclic component", cyclicComponentId != -1);

        Graph subgraph = cg.getComponentSubgraph(cyclicComponentId);

        assertNotNull(subgraph);
        assertEquals(3, subgraph.getN());

        assertTrue(subgraph.getEdgeCount() >= 3);
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

    @Test
    public void testSimpleDAGCondensation() {

        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();


        assertEquals(4, cg.getCondensationGraph().getN());
        assertEquals(3, cg.getCondensationGraph().getEdgeCount());


        CondensationGraph.ComponentStatistics stats = cg.getStatistics();
        assertEquals(4, stats.getTrivialComponents());
        assertEquals(0, stats.getCyclicComponents());
    }

    @Test
    public void testSingleCycle() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();


        assertEquals(1, cg.getCondensationGraph().getN());
        assertEquals(3, cg.getComponentSize(0));

        CondensationGraph.ComponentStatistics stats = cg.getStatistics();
        assertEquals(1, stats.getTotalComponents());
        assertEquals(1, stats.getCyclicComponents());
        assertEquals(0, stats.getTrivialComponents());
    }

    @Test
    public void testIsolatedVertices() {
        Graph graph = new Graph(5, true);

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();


        assertEquals(5, cg.getCondensationGraph().getN());
        assertEquals(0, cg.getCondensationGraph().getEdgeCount());

        CondensationGraph.ComponentStatistics stats = cg.getStatistics();
        assertEquals(5, stats.getTotalComponents());
        assertEquals(5, stats.getTrivialComponents());
        assertEquals(0, stats.getCyclicComponents());
    }

    @Test
    public void testComplexStructure() {
        Graph graph = new Graph(8, true);

        graph.addEdge(0, 1, 1); graph.addEdge(1, 2, 1); graph.addEdge(2, 0, 1);

        graph.addEdge(3, 4, 1); graph.addEdge(4, 3, 1);

        graph.addEdge(5, 6, 1); graph.addEdge(6, 7, 1);

        graph.addEdge(2, 3, 1); // SCC1 -> SCC2
        graph.addEdge(4, 5, 1); // SCC2 -> SCC3

        TarjanSCC tarjan = new TarjanSCC(graph);
        CondensationGraph cg = tarjan.getCondensationGraphObject();


        assertEquals(5, cg.getCondensationGraph().getN());

        CondensationGraph.ComponentStatistics stats = cg.getStatistics();
        assertEquals(5, stats.getTotalComponents());
        assertEquals(2, stats.getCyclicComponents());
        assertEquals(3, stats.getTrivialComponents());


        assertEquals(4, cg.getCondensationGraph().getEdgeCount());
    }
}