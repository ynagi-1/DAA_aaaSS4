package smartcity.graph.topo;

import smartcity.model.Graph;
import smartcity.graph.dagsp.Metrics;
import java.util.*;


public class KahnTopological implements Metrics {
    private long dfsCount;
    private long edgeRelaxations;
    private long kahnOperations;

    public List<Integer> topologicalSort(Graph graph) {
        reset();
        return kahnTopologicalSort(graph);
    }

    public List<Integer> kahnTopologicalSort(Graph graph) {
        int n = graph.getN();
        int[] inDegree = new int[n];


        calculateInDegrees(graph, inDegree);


        Queue<Integer> queue = initializeQueue(inDegree);


        List<Integer> result = processVertices(graph, inDegree, queue);


        validateAcyclicGraph(result, n);

        return result;
    }

    private void calculateInDegrees(Graph graph, int[] inDegree) {
        for (int u = 0; u < graph.getN(); u++) {
            for (var edge : graph.getEdges(u)) {
                incrementEdgeRelaxation();
                inDegree[edge.getTo()]++;
            }
        }
    }

    private Queue<Integer> initializeQueue(int[] inDegree) {
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < inDegree.length; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        return queue;
    }

    private List<Integer> processVertices(Graph graph, int[] inDegree, Queue<Integer> queue) {
        List<Integer> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            incrementKahnOperation();
            int u = queue.poll();
            result.add(u);

            processNeighbors(graph, u, inDegree, queue);
        }

        return result;
    }

    private void processNeighbors(Graph graph, int u, int[] inDegree, Queue<Integer> queue) {
        for (var edge : graph.getEdges(u)) {
            incrementEdgeRelaxation();
            int v = edge.getTo();
            inDegree[v]--;
            if (inDegree[v] == 0) {
                queue.offer(v);
            }
        }
    }

    private void validateAcyclicGraph(List<Integer> result, int totalVertices) {
        if (result.size() != totalVertices) {
            throw new IllegalArgumentException(
                    "Graph contains cycles. Processed " + result.size() +
                            " of " + totalVertices + " vertices. " +
                            "Remaining vertices form cycles."
            );
        }
    }


    public List<Integer> getTaskOrderFromComponents(List<Integer> componentOrder,
                                                    List<List<Integer>> components) {
        List<Integer> taskOrder = new ArrayList<>();
        for (int compId : componentOrder) {
            List<Integer> componentTasks = components.get(compId);


            if (componentTasks.size() == 1) {
                taskOrder.add(componentTasks.get(0));
            } else {

                taskOrder.addAll(componentTasks);
            }
        }
        return taskOrder;
    }


    public List<Integer> topologicalSortWithPriority(Graph graph, boolean preferHighDegree) {
        int n = graph.getN();
        int[] inDegree = new int[n];
        calculateInDegrees(graph, inDegree);


        PriorityQueue<Integer> queue = new PriorityQueue<>(
                (a, b) -> preferHighDegree ?
                        Integer.compare(getOutDegree(graph, b), getOutDegree(graph, a)) :
                        Integer.compare(getOutDegree(graph, a), getOutDegree(graph, b))
        );

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.add(u);

            for (var edge : graph.getEdges(u)) {
                int v = edge.getTo();
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        validateAcyclicGraph(result, n);
        return result;
    }

    private int getOutDegree(Graph graph, int vertex) {
        return graph.getEdges(vertex).size();
    }


    public List<List<Integer>> findAllTopologicalSorts(Graph graph) {
        List<List<Integer>> allSorts = new ArrayList<>();
        int n = graph.getN();

        int[] inDegree = new int[n];
        calculateInDegrees(graph, inDegree);

        boolean[] visited = new boolean[n];
        List<Integer> currentSort = new ArrayList<>();

        findAllTopologicalSortsUtil(graph, inDegree, visited, currentSort, allSorts);

        return allSorts;
    }

    private void findAllTopologicalSortsUtil(Graph graph, int[] inDegree,
                                             boolean[] visited, List<Integer> currentSort,
                                             List<List<Integer>> allSorts) {
        boolean found = false;

        for (int i = 0; i < graph.getN(); i++) {
            if (!visited[i] && inDegree[i] == 0) {

                visited[i] = true;
                currentSort.add(i);


                for (var edge : graph.getEdges(i)) {
                    inDegree[edge.getTo()]--;
                }


                findAllTopologicalSortsUtil(graph, inDegree, visited, currentSort, allSorts);


                visited[i] = false;
                currentSort.remove(currentSort.size() - 1);
                for (var edge : graph.getEdges(i)) {
                    inDegree[edge.getTo()]++;
                }

                found = true;
            }
        }


        if (!found) {
            allSorts.add(new ArrayList<>(currentSort));
        }
    }


    public boolean isDAG(Graph graph) {
        try {
            topologicalSort(graph);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public boolean isValidTopologicalOrder(Graph graph, List<Integer> order) {
        int n = graph.getN();
        int[] position = new int[n];

        // Record position of each vertex
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i) < 0 || order.get(i) >= n) {
                return false; // Invalid vertex index
            }
            position[order.get(i)] = i;
        }


        for (int u = 0; u < n; u++) {
            for (var edge : graph.getEdges(u)) {
                int v = edge.getTo();
                if (position[u] >= position[v]) {
                    return false;
                }
            }
        }

        return true;
    }

    // Metrics implementation
    @Override
    public void incrementDFSCount() { dfsCount++; }
    @Override
    public void incrementEdgeRelaxation() { edgeRelaxations++; }
    @Override
    public void incrementKahnOperation() { kahnOperations++; }
    @Override
    public long getDFSCount() { return dfsCount; }
    @Override
    public long getEdgeRelaxations() { return edgeRelaxations; }
    @Override
    public long getKahnOperations() { return kahnOperations; }
    @Override
    public void reset() {
        dfsCount = 0;
        edgeRelaxations = 0;
        kahnOperations = 0;
    }
}