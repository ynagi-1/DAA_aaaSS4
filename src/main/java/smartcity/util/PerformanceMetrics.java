package smartcity.util;

import java.util.*;

public class PerformanceMetrics {
    private static Map<String, Long> startTimes = new HashMap<>();
    private static Map<String, Long> endTimes = new HashMap<>();
    private static Map<String, Long> operationCounts = new HashMap<>();

    public static void start(String operation) {
        startTimes.put(operation, System.nanoTime());
    }

    public static void end(String operation) {
        endTimes.put(operation, System.nanoTime());
    }

    public static long getDuration(String operation) {
        Long start = startTimes.get(operation);
        Long end = endTimes.get(operation);
        if (start != null && end != null) {
            return end - start;
        }
        return -1;
    }

    public static void incrementCount(String operation) {
        operationCounts.put(operation, operationCounts.getOrDefault(operation, 0L) + 1);
    }

    public static void printSummary() {
        System.out.println("\n=== Performance Summary ===");
        for (String operation : startTimes.keySet()) {
            long duration = getDuration(operation);
            long count = operationCounts.getOrDefault(operation, 0L);
            System.out.printf("%s: %d ns", operation, duration);
            if (count > 0) {
                System.out.printf(" (%d operations)", count);
            }
            System.out.println();
        }
    }

    public static void resetAll() {
        startTimes.clear();
        endTimes.clear();
        operationCounts.clear();
    }
}