package smartcity;

import smartcity.model.Graph;
import smartcity.graph.scc.TarjanSCC;
import smartcity.graph.scc.CondensationGraph;
import smartcity.graph.topo.KahnTopological;
import smartcity.graph.dagsp.DAGShortestPath;
import smartcity.graph.dagsp.CriticalPath;
import smartcity.util.JSONParser;
import smartcity.util.DataGenerator;
import smartcity.util.PerformanceMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Создаем директорию data если её нет
            createDataDirectory();

            // Генерация всех датасетов
            System.out.println("=== Generating Test Datasets ===");
            generateAllDatasets();

            // Анализ предоставленного графа
            System.out.println("\n=== Analyzing Provided Graph ===");
            analyzeGraph("data/task.json");

            // Анализ всех датасетов
            System.out.println("\n=== Analyzing All Datasets ===");
            analyzeAllDatasets();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected static void createDataDirectory() throws IOException {
        if (!Files.exists(Paths.get("data"))) {
            Files.createDirectories(Paths.get("data"));
            System.out.println("Created data directory");
        }
    }

    private static void generateAllDatasets() throws IOException {
        DataGenerator generator = new DataGenerator();

        // Small datasets
        for (int i = 1; i <= 3; i++) {
            Graph graph = generator.generateSmallGraph(i);
            generator.saveGraphToJSON(graph, String.format("data/small_%d.json", i), 0);
            System.out.println("Generated small_" + i + ".json: " + graph.getN() + " nodes, " +
                    graph.getEdgeCount() + " edges");
        }

        // Medium datasets
        for (int i = 1; i <= 3; i++) {
            Graph graph = generator.generateMediumGraph(i);
            generator.saveGraphToJSON(graph, String.format("data/medium_%d.json", i), 0);
            System.out.println("Generated medium_" + i + ".json: " + graph.getN() + " nodes, " +
                    graph.getEdgeCount() + " edges");
        }

        // Large datasets
        for (int i = 1; i <= 3; i++) {
            Graph graph = generator.generateLargeGraph(i);
            generator.saveGraphToJSON(graph, String.format("data/large_%d.json", i), 0);
            System.out.println("Generated large_" + i + ".json: " + graph.getN() + " nodes, " +
                    graph.getEdgeCount() + " edges");
        }

        // Generate the specific task.json from assignment
        generateTaskJson();
    }

    public static void generateTaskJson() throws IOException {
        DataGenerator generator = new DataGenerator();

        // Create the exact graph from the assignment
        Graph graph = new Graph(8, true);
        graph.setWeightModel("edge");
        graph.setSource(4);

        // Add edges as specified in assignment
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 4);
        graph.addEdge(3, 1, 1);  // This creates a cycle 1->2->3->1
        graph.addEdge(4, 5, 2);
        graph.addEdge(5, 6, 5);
        graph.addEdge(6, 7, 1);

        generator.saveGraphToJSON(graph, "data/task.json", 4);
        System.out.println("Generated task.json: " + graph.getN() + " nodes, " +
                graph.getEdgeCount() + " edges");

        // Print graph structure for verification
        System.out.println("Graph structure:");
        System.out.println("  Cycle: 1 -> 2 -> 3 -> 1");
        System.out.println("  Path: 4 -> 5 -> 6 -> 7");
        System.out.println("  Isolated: 0");
    }

    private static void analyzeGraph(String filename) {
        try {
            JSONParser parser = new JSONParser();
            Graph graph = parser.parseGraph(filename);

            System.out.println("\n=== Analyzing " + filename + " ===");
            System.out.println("Nodes: " + graph.getN() + ", Edges: " + graph.getEdgeCount());
            System.out.println("Weight model: " + graph.getWeightModel());
            if (graph.getSource() != -1) {
                System.out.println("Source node: " + graph.getSource());
            }

            // SCC анализ
            PerformanceMetrics.start("SCC");
            TarjanSCC tarjan = new TarjanSCC(graph);
            List<List<Integer>> sccs = tarjan.findSCCs();
            PerformanceMetrics.end("SCC");

            System.out.println("\nStrongly Connected Components: " + sccs.size());
            for (int i = 0; i < sccs.size(); i++) {
                System.out.println("  SCC " + i + ": " + sccs.get(i) + " (size: " + sccs.get(i).size() + ")");
            }

            // Condensation graph
            PerformanceMetrics.start("Condensation");
            Graph condensation = tarjan.buildCondensationGraph();
            CondensationGraph cg = tarjan.getCondensationGraphObject();
            PerformanceMetrics.end("Condensation");

            System.out.println("Condensation graph: " + condensation.getN() + " components, " +
                    condensation.getEdgeCount() + " edges between components");

            // Topological sort of condensation graph
            PerformanceMetrics.start("Topological");
            KahnTopological topological = new KahnTopological();
            List<Integer> topoOrder = topological.topologicalSort(condensation);
            PerformanceMetrics.end("Topological");

            System.out.println("Topological Order of Components: " + topoOrder);

            // Get task execution order
            List<Integer> taskOrder = topological.getTaskOrderFromComponents(topoOrder, sccs);
            System.out.println("Task Execution Order: " + taskOrder);

            // Статистика компонент
            CondensationGraph.ComponentStatistics stats = cg.getStatistics();
            System.out.println("\n" + stats.toString());

            // Анализ источников и стоков
            List<Integer> sourceComps = cg.findSourceComponents();
            List<Integer> sinkComps = cg.findSinkComponents();
            System.out.println("Source components (can start first): " + sourceComps);
            System.out.println("Sink components (can finish last): " + sinkComps);

            // Shortest path from source in ORIGINAL GRAPH (если это DAG)
            if (graph.getSource() != -1 && topological.isDAG(graph)) {
                PerformanceMetrics.start("ShortestPath");
                DAGShortestPath shortestPath = new DAGShortestPath();
                DAGShortestPath.Result spResult = shortestPath.findShortestPath(graph, graph.getSource(), -1);
                PerformanceMetrics.end("ShortestPath");

                System.out.println("\nShortest distances from source " + graph.getSource() + " in original graph:");
                for (int i = 0; i < spResult.distances.length; i++) {
                    if (spResult.distances[i] != Double.POSITIVE_INFINITY) {
                        System.out.println("  to node " + i + ": " + spResult.distances[i]);
                    }
                }

                // Show one example path
                if (graph.getN() > graph.getSource() + 1) {
                    int target = graph.getN() - 1;
                    DAGShortestPath.Result pathResult = shortestPath.findShortestPath(graph, graph.getSource(), target);
                    if (!pathResult.path.isEmpty()) {
                        System.out.println("Example shortest path from " + graph.getSource() + " to " + target + ": " + pathResult.path);
                    }
                }
            } else if (graph.getSource() != -1) {
                System.out.println("\nOriginal graph contains cycles - shortest path analysis skipped for original graph");
                System.out.println("Using condensation graph (DAG) for path analysis instead");
            }

            // Shortest path in CONDENSATION GRAPH (всегда DAG)
            PerformanceMetrics.start("CondensationShortestPath");
            DAGShortestPath condensationSP = new DAGShortestPath();

            // Find source component
            int sourceComponent = cg.getComponentId(graph.getSource());
            DAGShortestPath.Result condensationResult = condensationSP.findShortestPath(condensation, sourceComponent, -1);
            PerformanceMetrics.end("CondensationShortestPath");

            System.out.println("\nShortest distances from source component " + sourceComponent + " in condensation graph:");
            for (int i = 0; i < condensationResult.distances.length; i++) {
                if (condensationResult.distances[i] != Double.POSITIVE_INFINITY) {
                    System.out.println("  to component " + i + ": " + condensationResult.distances[i] +
                            " (nodes: " + cg.getComponent(i) + ")");
                }
            }

            // Critical path (longest path) in CONDENSATION GRAPH
            PerformanceMetrics.start("CriticalPath");
            CriticalPath criticalPath = new CriticalPath();
            int startComponent = sourceComps.isEmpty() ? 0 : sourceComps.get(0);
            int endComponent = sinkComps.isEmpty() ? condensation.getN() - 1 : sinkComps.get(0);

            DAGShortestPath.Result cpResult = criticalPath.findCriticalPath(condensation, startComponent, endComponent);
            PerformanceMetrics.end("CriticalPath");

            if (!cpResult.path.isEmpty()) {
                System.out.println("\nCritical path in condensation graph:");
                System.out.println("  Length: " + cpResult.distances[endComponent]);
                System.out.println("  Path through components: " + cpResult.path);

                // Convert component path to node path
                List<Integer> nodePath = new ArrayList<>();
                for (int compId : cpResult.path) {
                    List<Integer> nodesInComponent = cg.getComponent(compId);
                    if (nodesInComponent.size() == 1) {
                        nodePath.add(nodesInComponent.get(0));
                    } else {
                        // For cyclic components, add all nodes or representative
                        nodePath.addAll(nodesInComponent);
                    }
                }
                System.out.println("  Corresponding node path: " + nodePath);
            } else {
                System.out.println("\nNo critical path found from component " + startComponent + " to " + endComponent);
            }

            // Critical path in ORIGINAL GRAPH (только если это DAG)
            if (topological.isDAG(graph)) {
                PerformanceMetrics.start("OriginalCriticalPath");
                CriticalPath originalCriticalPath = new CriticalPath();
                int startNode = 0;
                int endNode = graph.getN() - 1;
                DAGShortestPath.Result originalCPResult = originalCriticalPath.findCriticalPath(graph, startNode, endNode);
                PerformanceMetrics.end("OriginalCriticalPath");

                if (!originalCPResult.path.isEmpty()) {
                    System.out.println("\nCritical path in original graph (DAG):");
                    System.out.println("  Length: " + originalCPResult.distances[endNode]);
                    System.out.println("  Path: " + originalCPResult.path);
                }
            }

            // Performance metrics
            PerformanceMetrics.printSummary();
            PerformanceMetrics.resetAll();

        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error analyzing graph " + filename + ": " + e.getMessage());
            // e.printStackTrace(); // Убираем stack trace для чистоты вывода
        }
    }

    private static void analyzeAllDatasets() {
        String[] datasets = {
                "small_1", "small_2", "small_3",
                "medium_1", "medium_2", "medium_3",
                "large_1", "large_2", "large_3"
        };

        for (String dataset : datasets) {
            analyzeGraph("data/" + dataset + ".json");
        }
    }
}