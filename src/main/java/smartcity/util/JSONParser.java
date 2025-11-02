package smartcity.util;

import smartcity.model.Graph;
import smartcity.model.Edge;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class JSONParser {
    public Graph parseGraph(String filename) throws IOException {
        if (!Files.exists(Paths.get(filename))) {
            throw new IOException("File not found: " + filename);
        }

        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject json = new JSONObject(content);

        int n = json.getInt("n");
        boolean directed = json.getBoolean("directed");
        Graph graph = new Graph(n, directed);

        if (json.has("weight_model")) {
            graph.setWeightModel(json.getString("weight_model"));
        } else {
            graph.setWeightModel("edge");
        }

        if (json.has("source")) {
            graph.setSource(json.getInt("source"));
        }

        JSONArray edges = json.getJSONArray("edges");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            int u = edge.getInt("u");
            int v = edge.getInt("v");
            double w = edge.getDouble("w");

            // Validate vertex indices
            if (u < 0 || u >= n || v < 0 || v >= n) {
                throw new IllegalArgumentException("Invalid vertex index in edge: " + u + "->" + v);
            }

            graph.addEdge(u, v, w);
        }

        return graph;
    }
}