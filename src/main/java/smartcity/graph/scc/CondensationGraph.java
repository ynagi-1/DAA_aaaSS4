package smartcity.graph.scc;

import smartcity.model.Graph;
import smartcity.model.Edge;
import java.util.*;


public class CondensationGraph {
    private final Graph originalGraph;
    private final List<List<Integer>> components;
    private final int[] componentId;
    private Graph condensationGraph;

    public CondensationGraph(Graph originalGraph, List<List<Integer>> components, int[] componentId) {
        this.originalGraph = originalGraph;
        this.components = components;
        this.componentId = componentId;
        buildCondensationGraph();
    }


    private void buildCondensationGraph() {
        int numComponents = components.size();
        this.condensationGraph = new Graph(numComponents, true);
        this.condensationGraph.setWeightModel("component");


        Set<String> addedEdges = new HashSet<>();


        for (int u = 0; u < originalGraph.getN(); u++) {
            int compU = componentId[u];

            for (Edge edge : originalGraph.getEdges(u)) {
                int v = edge.getTo();
                int compV = componentId[v];

                // Добавляем ребро только если компоненты разные и ребро ещё не добавлено
                if (compU != compV) {
                    String edgeKey = compU + "->" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensationGraph.addEdge(compU, compV, calculateComponentEdgeWeight(compU, compV));
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }
    }


    private double calculateComponentEdgeWeight(int compU, int compV) {
        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;
        double sumWeight = 0;
        int edgeCount = 0;

        for (int u : components.get(compU)) {

            for (Edge edge : originalGraph.getEdges(u)) {
                if (componentId[edge.getTo()] == compV) {
                    double weight = edge.getWeight();
                    minWeight = Math.min(minWeight, weight);
                    maxWeight = Math.max(maxWeight, weight);
                    sumWeight += weight;
                    edgeCount++;
                }
            }
        }


        return edgeCount > 0 ? minWeight : 0;
    }


    public Graph getCondensationGraph() {
        return condensationGraph;
    }


    public List<Integer> getComponent(int componentId) {
        return components.get(componentId);
    }


    public int getComponentId(int vertex) {
        return componentId[vertex];
    }


    public List<List<Integer>> getComponents() {
        return components;
    }


    public int getComponentSize(int componentId) {
        return components.get(componentId).size();
    }


    public boolean isTrivialComponent(int componentId) {
        return components.get(componentId).size() == 1;
    }


    public boolean isCyclicComponent(int componentId) {
        return components.get(componentId).size() > 1;
    }


    public List<List<Integer>> getCyclicComponents() {
        List<List<Integer>> cyclicComponents = new ArrayList<>();
        for (List<Integer> component : components) {
            if (component.size() > 1) {
                cyclicComponents.add(component);
            }
        }
        return cyclicComponents;
    }


    public List<List<Integer>> getTrivialComponents() {
        List<List<Integer>> trivialComponents = new ArrayList<>();
        for (List<Integer> component : components) {
            if (component.size() == 1) {
                trivialComponents.add(component);
            }
        }
        return trivialComponents;
    }


    public List<Integer> findSourceComponents() {
        int n = condensationGraph.getN();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Edge edge : condensationGraph.getEdges(u)) {
                inDegree[edge.getTo()]++;
            }
        }

        List<Integer> sources = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                sources.add(i);
            }
        }
        return sources;
    }


    public List<Integer> findSinkComponents() {
        List<Integer> sinks = new ArrayList<>();
        for (int i = 0; i < condensationGraph.getN(); i++) {
            if (condensationGraph.getEdges(i).isEmpty()) {
                sinks.add(i);
            }
        }
        return sinks;
    }


    public ComponentStatistics getStatistics() {
        return new ComponentStatistics(this);
    }


    public String visualize() {
        StringBuilder sb = new StringBuilder();
        sb.append("Condensation Graph (DAG):\n");
        sb.append("Components: ").append(components.size()).append("\n");

        for (int i = 0; i < condensationGraph.getN(); i++) {
            sb.append("Component ").append(i).append(": ");
            sb.append(components.get(i)).append(" ");
            sb.append("(size: ").append(components.get(i).size()).append(")");

            List<Edge> edges = condensationGraph.getEdges(i);
            if (!edges.isEmpty()) {
                sb.append(" -> ");
                for (Edge edge : edges) {
                    sb.append(edge.getTo()).append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }


    public Graph getComponentSubgraph(int componentId) {
        List<Integer> vertices = components.get(componentId);
        Graph subgraph = new Graph(vertices.size(), true);
        subgraph.setWeightModel(originalGraph.getWeightModel());


        Map<Integer, Integer> localIndex = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            localIndex.put(vertices.get(i), i);
        }

        // Добавляем рёбра внутри компоненты
        for (int globalU : vertices) {
            for (Edge edge : originalGraph.getEdges(globalU)) {
                int globalV = edge.getTo();
                if (localIndex.containsKey(globalV)) {
                    int localU = localIndex.get(globalU);
                    int localV = localIndex.get(globalV);
                    subgraph.addEdge(localU, localV, edge.getWeight());
                }
            }
        }

        return subgraph;
    }


    public Map<Integer, List<Integer>> getComponentConnectivity() {
        Map<Integer, List<Integer>> connectivity = new HashMap<>();

        for (int compU = 0; compU < condensationGraph.getN(); compU++) {
            List<Integer> neighbors = new ArrayList<>();
            for (Edge edge : condensationGraph.getEdges(compU)) {
                neighbors.add(edge.getTo());
            }
            connectivity.put(compU, neighbors);
        }

        return connectivity;
    }


    public static class ComponentStatistics {
        private final int totalComponents;
        private final int trivialComponents;
        private final int cyclicComponents;
        private final int largestComponentSize;
        private final double averageComponentSize;

        public ComponentStatistics(CondensationGraph cg) {
            this.totalComponents = cg.components.size();
            this.trivialComponents = cg.getTrivialComponents().size();
            this.cyclicComponents = cg.getCyclicComponents().size();


            int maxSize = 0;
            int totalVertices = 0;
            for (List<Integer> component : cg.components) {
                maxSize = Math.max(maxSize, component.size());
                totalVertices += component.size();
            }
            this.largestComponentSize = maxSize;
            this.averageComponentSize = totalComponents > 0 ? (double) totalVertices / totalComponents : 0;
        }

        // Getters
        public int getTotalComponents() { return totalComponents; }
        public int getTrivialComponents() { return trivialComponents; }
        public int getCyclicComponents() { return cyclicComponents; }
        public int getLargestComponentSize() { return largestComponentSize; }
        public double getAverageComponentSize() { return averageComponentSize; }

        @Override
        public String toString() {
            return String.format(
                    "Component Statistics:\n" +
                            "  Total components: %d\n" +
                            "  Trivial components (size=1): %d\n" +
                            "  Cyclic components (size>1): %d\n" +
                            "  Largest component size: %d\n" +
                            "  Average component size: %.2f",
                    totalComponents, trivialComponents, cyclicComponents,
                    largestComponentSize, averageComponentSize
            );
        }
    }
}