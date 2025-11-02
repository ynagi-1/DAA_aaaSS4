package smartcity.model;

import java.util.*;

public class SCCResult {
    private final List<List<Integer>> components;
    private final int[] componentId;
    private final Graph condensationGraph;

    public SCCResult(List<List<Integer>> components, int[] componentId, Graph condensationGraph) {
        this.components = components;
        this.componentId = componentId;
        this.condensationGraph = condensationGraph;
    }

    // Getters
    public List<List<Integer>> getComponents() { return components; }
    public int[] getComponentId() { return componentId; }
    public Graph getCondensationGraph() { return condensationGraph; }

    public int getComponentCount() {
        return components.size();
    }

    public List<Integer> getComponent(int id) {
        return components.get(id);
    }

    public int getComponentId(int vertex) {
        return componentId[vertex];
    }
}