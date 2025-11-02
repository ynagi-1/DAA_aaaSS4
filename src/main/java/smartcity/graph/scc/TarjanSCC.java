package smartcity.graph.scc;

import smartcity.model.Graph;
import smartcity.model.Edge;
import smartcity.graph.dagsp.Metrics;
import java.util.*;

public class TarjanSCC implements Metrics {
    private int index;
    private int[] indices;
    private int[] lowlinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;
    private int[] componentId;
    private Graph graph;
    private CondensationGraph condensationGraph;

    private long dfsCount;
    private long edgeRelaxations;
    private long kahnOperations;

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.index = 0;
        this.indices = new int[graph.getN()];
        this.lowlinks = new int[graph.getN()];
        this.onStack = new boolean[graph.getN()];
        this.stack = new Stack<>();
        this.sccs = new ArrayList<>();
        this.componentId = new int[graph.getN()];
        Arrays.fill(indices, -1);
        Arrays.fill(componentId, -1);
    }

    public List<List<Integer>> findSCCs() {
        reset();
        for (int v = 0; v < graph.getN(); v++) {
            if (indices[v] == -1) {
                strongConnect(v);
            }
        }
        return sccs;
    }

    private void strongConnect(int v) {
        incrementDFSCount();
        indices[v] = index;
        lowlinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        for (var edge : graph.getEdges(v)) {
            incrementEdgeRelaxation();
            int w = edge.getTo();
            if (indices[w] == -1) {
                strongConnect(w);
                lowlinks[v] = Math.min(lowlinks[v], lowlinks[w]);
            } else if (onStack[w]) {
                lowlinks[v] = Math.min(lowlinks[v], indices[w]);
            }
        }

        if (lowlinks[v] == indices[v]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                scc.add(w);
                componentId[w] = sccs.size();
            } while (w != v);
            sccs.add(scc);
        }
    }


    public Graph buildCondensationGraph() {
        findSCCs(); // Убедимся, что SCC найдены
        this.condensationGraph = new CondensationGraph(graph, sccs, componentId);
        return condensationGraph.getCondensationGraph();
    }


    public CondensationGraph getCondensationGraphObject() {
        if (condensationGraph == null) {
            buildCondensationGraph();
        }
        return condensationGraph;
    }


    public CondensationGraph.ComponentStatistics analyzeComponents() {
        if (condensationGraph == null) {
            buildCondensationGraph();
        }
        return condensationGraph.getStatistics();
    }

    public int[] getComponentId() {
        return componentId;
    }

    // Metrics implementation
    @Override
    public void incrementDFSCount() {
        dfsCount++;
    }
    @Override
    public void incrementEdgeRelaxation() {
        edgeRelaxations++;
    }
    @Override
    public void incrementKahnOperation() {
        kahnOperations++;
    }
    @Override
    public long getDFSCount() {
        return dfsCount;
    }
    @Override
    public long getEdgeRelaxations() {
        return edgeRelaxations;
    }
    @Override
    public long getKahnOperations() {
        return kahnOperations;
    }
    @Override
    public void reset() {
        dfsCount = 0;
        edgeRelaxations = 0;
        kahnOperations = 0;
    }
}