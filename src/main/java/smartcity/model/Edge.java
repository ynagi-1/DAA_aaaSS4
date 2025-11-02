package smartcity.model;

public class Edge {
    private final int from;
    private final int to;
    private final double weight;

    public Edge(int from, int to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    // Getters
    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return from + "->" + to + "(" + weight + ")";
    }
}