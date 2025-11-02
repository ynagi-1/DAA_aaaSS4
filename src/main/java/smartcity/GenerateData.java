package smartcity;

import smartcity.util.DataGenerator;
import java.io.IOException;

public class GenerateData {
    public static void main(String[] args) {
        try {

            Main.createDataDirectory();

            DataGenerator generator = new DataGenerator();

            System.out.println("Generating all dataset files...");

            for (int i = 1; i <= 3; i++) {
                generator.saveGraphToJSON(generator.generateSmallGraph(i),
                        String.format("data/small_%d.json", i), 0);
            }

            for (int i = 1; i <= 3; i++) {
                generator.saveGraphToJSON(generator.generateMediumGraph(i),
                        String.format("data/medium_%d.json", i), 0);
            }

            for (int i = 1; i <= 3; i++) {
                generator.saveGraphToJSON(generator.generateLargeGraph(i),
                        String.format("data/large_%d.json", i), 0);
            }

            Main.generateTaskJson();

            System.out.println("All dataset files generated successfully in data/ directory");

        } catch (Exception e) {
            System.err.println("Error generating data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}