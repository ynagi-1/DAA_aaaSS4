package smartcity;

import org.junit.Test;
import static org.junit.Assert.*;
import smartcity.model.Graph;
import smartcity.model.Vertex;
import smartcity.graph.scc.TarjanSCC;
import smartcity.graph.scc.CondensationGraph;
import smartcity.graph.topo.KahnTopological;
import smartcity.graph.dagsp.DAGShortestPath;
import smartcity.graph.dagsp.CriticalPath;
import smartcity.util.DataGenerator;

import java.util.*;


public class IntegrationTest {

    @Test
    public void testCompleteWorkflowWithTaskJson() {

        Graph graph = new Graph(8, true);
        graph.setWeightModel("edge");
        graph.setSource(4);


        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 1, 1);  // Создаёт цикл 1->2->3->1
        graph.addEdge(4, 5, 2);
        graph.addEdge(5, 6, 5);
        graph.addEdge(6, 7, 1);


        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();


        assertEquals(6, sccs.size());


        List<Integer> cyclicComponent = null;
        for (List<Integer> scc : sccs) {
            if (scc.size() > 1) {
                cyclicComponent = scc;
                break;
            }
        }
        assertNotNull(cyclicComponent);
        assertEquals(3, cyclicComponent.size());
        assertTrue(cyclicComponent.containsAll(Arrays.asList(1, 2, 3)));
        Graph condensation = tarjan.buildCondensationGraph();
        CondensationGraph cg = tarjan.getCondensationGraphObject();

        assertNotNull(condensation);
        assertTrue(condensation.getN() > 0);

        KahnTopological topological = new KahnTopological();
        List<Integer> topoOrder = topological.topologicalSort(condensation);

        assertEquals(condensation.getN(), topoOrder.size());
        assertTrue(topological.isValidTopologicalOrder(condensation, topoOrder));


        List<Integer> taskOrder = topological.getTaskOrderFromComponents(topoOrder, sccs);
        assertEquals(graph.getN(), taskOrder.size());

       DAGShortestPath shortestPath = new DAGShortestPath();
        int sourceComponent = cg.getComponentId(graph.getSource());
        DAGShortestPath.Result spResult = shortestPath.findShortestPath(condensation, sourceComponent, -1);

        assertNotNull(spResult);
        assertNotNull(spResult.distances);

       CriticalPath criticalPath = new CriticalPath();
        List<Integer> sourceComps = cg.findSourceComponents();
        List<Integer> sinkComps = cg.findSinkComponents();

        if (!sourceComps.isEmpty() && !sinkComps.isEmpty()) {
            DAGShortestPath.Result cpResult = criticalPath.findCriticalPath(
                    condensation, sourceComps.get(0), sinkComps.get(0));
            assertNotNull(cpResult);
        }

        assertTrue("SCC count should be positive", sccs.size() > 0);
        assertTrue("Condensation graph should be DAG", topological.isDAG(condensation));
        assertTrue("Task order should contain all vertices", taskOrder.size() == graph.getN());
    }

    @Test
    public void testSmartCityScenario() {
        Graph graph = createSmartCityGraph();


        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        CondensationGraph cg = tarjan.getCondensationGraphObject();
        Graph condensation = cg.getCondensationGraph();

        KahnTopological topological = new KahnTopological();
        List<Integer> topoOrder = topological.topologicalSort(condensation);

        List<Integer> taskOrder = topological.getTaskOrderFromComponents(topoOrder, sccs);


        assertNotNull(taskOrder);
        assertEquals(graph.getN(), taskOrder.size());


        CriticalPath criticalPath = new CriticalPath();
        List<Integer> sources = cg.findSourceComponents();
        List<Integer> sinks = cg.findSinkComponents();

        if (!sources.isEmpty() && !sinks.isEmpty()) {
            DAGShortestPath.Result cpResult = criticalPath.findCriticalPath(
                    condensation, sources.get(0), sinks.get(0));
            assertNotNull(cpResult);


            if (!cpResult.path.isEmpty()) {
                double criticalLength = cpResult.distances[sinks.get(0)];
                assertTrue("Critical path should have positive length", criticalLength > 0);
            }
        }


        CondensationGraph.ComponentStatistics stats = cg.getStatistics();
        assertTrue("Total components should be positive", stats.getTotalComponents() > 0);
        assertTrue("Average component size should be positive", stats.getAverageComponentSize() > 0);
    }

    @Test
    public void testPerformanceWithDifferentGraphSizes() {
        DataGenerator generator = new DataGenerator();


        int[] sizes = {10, 20, 30};
        for (int size : sizes) {
            Graph graph = generator.generateCustomGraph(size, size * 2, true, false);

            long startTime = System.nanoTime();

            TarjanSCC tarjan = new TarjanSCC(graph);
            tarjan.findSCCs();
            tarjan.buildCondensationGraph();

            KahnTopological topological = new KahnTopological();
            topological.topologicalSort(tarjan.getCondensationGraphObject().getCondensationGraph());

            long endTime = System.nanoTime();
            long duration = endTime - startTime;


            assertTrue("Processing should complete in reasonable time for size " + size,
                    duration < 1_000_000_000L);
        }
    }

    @Test
    public void testErrorHandling() {



        Graph emptyGraph = new Graph(0, true);
        TarjanSCC tarjan = new TarjanSCC(emptyGraph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        assertEquals(0, sccs.size());


        Graph singleNodeGraph = new Graph(1, true);
        tarjan = new TarjanSCC(singleNodeGraph);
        sccs = tarjan.findSCCs();
        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());


        Graph isolatedGraph = new Graph(5, true);
        tarjan = new TarjanSCC(isolatedGraph);
        sccs = tarjan.findSCCs();
        assertEquals(5, sccs.size()); // Каждый узел - отдельная компонента
    }

    @Test
    public void testVertexIntegration() {


        Graph graph = new Graph(5, true);
        graph.setWeightModel("edge");


        Vertex streetCleaning = Vertex.createStreetCleaning(0, 2.0);
        Vertex repair = Vertex.createRepair(1, 4.0);
        Vertex maintenance = Vertex.createMaintenance(2, 1.0);
        Vertex analytics = Vertex.createAnalytics(3, 3.0);
        Vertex general = new Vertex(4, "GeneralTask", "general", 1.0, 1);


        graph.addEdge(streetCleaning.getId(), repair.getId(), 1.0);
        graph.addEdge(repair.getId(), maintenance.getId(), 1.0);
        graph.addEdge(maintenance.getId(), analytics.getId(), 1.0);


        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();


        assertEquals(5, sccs.size());

        CondensationGraph cg = tarjan.getCondensationGraphObject();
        Graph condensation = cg.getCondensationGraph();

        KahnTopological topological = new KahnTopological();
        List<Integer> topoOrder = topological.topologicalSort(condensation);


        List<Integer> taskOrder = topological.getTaskOrderFromComponents(topoOrder, sccs);


        assertTrue(taskOrder.indexOf(0) < taskOrder.indexOf(1));

        assertTrue(taskOrder.indexOf(1) < taskOrder.indexOf(2));

        assertTrue(taskOrder.indexOf(2) < taskOrder.indexOf(3));
    }

    @Test
    public void testComplexDependencies() {

        Graph graph = new Graph(6, true);



        graph.addEdge(0, 1, 2.0); // A -> B
        graph.addEdge(1, 2, 3.0); // B -> C
        graph.addEdge(2, 3, 1.0); // C -> D
        graph.addEdge(0, 4, 4.0); // A -> E
        graph.addEdge(4, 3, 2.0); // E -> D

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();


        assertEquals(5, sccs.size());

        CondensationGraph cg = tarjan.getCondensationGraphObject();


        CriticalPath criticalPath = new CriticalPath();
        List<Integer> sources = cg.findSourceComponents();
        List<Integer> sinks = cg.findSinkComponents();

        if (!sources.isEmpty() && !sinks.isEmpty()) {
            DAGShortestPath.Result cpResult = criticalPath.findCriticalPath(
                    cg.getCondensationGraph(), sources.get(0), sinks.get(0));


            assertTrue(cpResult.distances[sinks.get(0)] >= 6.0);
        }
    }

    @Test
    public void testMultipleSCCsScenario() {


        Graph graph = new Graph(8, true);


        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 0, 1.0);

        graph.addEdge(3, 4, 1.0);
        graph.addEdge(4, 3, 1.0);

        graph.addEdge(5, 6, 1.0);
        graph.addEdge(6, 7, 1.0);

        graph.addEdge(2, 3, 1.0); // SCC1 -> SCC2
        graph.addEdge(4, 5, 1.0); // SCC2 -> SCC3

        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(3, sccs.size());

        CondensationGraph cg = tarjan.getCondensationGraphObject();
        CondensationGraph.ComponentStatistics stats = cg.getStatistics();

        assertEquals(3, stats.getTotalComponents());
        assertEquals(2, stats.getCyclicComponents()); // Две циклические компоненты
        assertEquals(1, stats.getTrivialComponents()); // Одна тривиальная (цепочка считается нетривиальной)

        Graph condensation = cg.getCondensationGraph();
        assertEquals(2, condensation.getEdgeCount()); // Два ребра между компонентами
    }

    private Graph createSmartCityGraph() {
        Graph graph = new Graph(10, true);
        graph.setWeightModel("edge");
        graph.setSource(0);

        graph.addEdge(0, 1, 3.0); // Street cleaning task 1
        graph.addEdge(1, 2, 2.0); // Street cleaning task 2

        graph.addEdge(2, 3, 4.0); // Repair task 1
        graph.addEdge(2, 4, 3.0); // Repair task 2

        graph.addEdge(3, 5, 2.0); // Maintenance task 1
        graph.addEdge(4, 5, 1.0); // Maintenance task 2

        graph.addEdge(5, 6, 3.0); // Analytics task 1
        graph.addEdge(5, 7, 2.0); // Analytics task 2

        graph.addEdge(6, 8, 1.0); // Final task 1
        graph.addEdge(7, 9, 2.0); // Final task 2


        graph.addEdge(8, 9, 1.0);
        graph.addEdge(9, 8, 1.0);

        return graph;
    }
}