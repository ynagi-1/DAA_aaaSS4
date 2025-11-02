package smartcity.graph.dagsp;

public interface Metrics {
    void incrementDFSCount();
    void incrementEdgeRelaxation();
    void incrementKahnOperation();
    long getDFSCount();
    long getEdgeRelaxations();
    long getKahnOperations();
    void reset();
}