package smartcity;

import org.junit.Test;
import static org.junit.Assert.*;
import smartcity.model.Graph;
import smartcity.graph.scc.TarjanSCC;
import smartcity.model.Vertex;

import java.util.*;

public class SCCTest {
    @Test
    public void testSCCWithVertexInfo() {
        Graph graph = new Graph(4, true);
        graph.setVertex(0, Vertex.createStreetCleaning(0, 2.0));
        graph.setVertex(1, Vertex.createRepair(1, 4.0));
        graph.setVertex(2, Vertex.createMaintenance(2, 1.0));
        graph.setVertex(3, Vertex.createAnalytics(3, 3.0));

        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 3, 1.0);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(4, sccs.size());


        assertEquals("street_cleaning", graph.getVertexTaskType(0));
        assertEquals("repair", graph.getVertexTaskType(1));
        assertEquals(4.0, graph.getVertexDuration(1), 0.001);
    }

    @Test
    public void testSimpleCycle() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 0, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(1, sccs.size());
        assertEquals(4, sccs.get(0).size());
        assertTrue(sccs.get(0).containsAll(Arrays.asList(0, 1, 2, 3)));
    }

    @Test
    public void testMultipleSCCs() {
        Graph graph = new Graph(6, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);


        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(3, sccs.size());


        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> scc : sccs) {
            sizes.add(scc.size());
        }
        Collections.sort(sizes);
        assertEquals(Arrays.asList(1, 2, 3), sizes);
    }

    @Test
    public void testCondensationGraph() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);

        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);

        graph.addEdge(0, 2, 1);
        graph.addEdge(3, 4, 1);

        TarjanSCC tarjan = new TarjanSCC(graph);
        Graph condensation = tarjan.buildCondensationGraph();

        assertEquals(3, condensation.getN());
        assertTrue(condensation.getEdgeCount() >= 2);
    }
}