# Smart City Scheduling System

## Overview

This project implements graph algorithms for scheduling city-service tasks (street cleaning, repairs, camera/sensor maintenance) and internal analytics subtasks. The system handles cyclic dependencies through Strongly Connected Components (SCC) detection and provides optimal scheduling for acyclic components using topological ordering and dynamic programming.

## Algorithms Implemented

### 1. Strongly Connected Components (SCC)
- **Algorithm**: Tarjan's Algorithm
- **Purpose**: Detect cyclic dependencies in task graphs
- **Output**: List of SCCs and their sizes
- **Condensation**: Build DAG of components

### 2. Topological Sorting
- **Algorithm**: Kahn's Algorithm
- **Purpose**: Determine execution order for acyclic components
- **Output**: Valid order of components and derived task order

### 3. Shortest/Longest Paths in DAGs
- **Weight Model**: **Edge weights** (representing task dependencies/durations)
- **Algorithms**:
    - Single-source shortest paths using dynamic programming
    - Longest path (critical path) via weight negation
- **Output**: Critical path length, shortest distances, optimal path reconstruction

## Project Structure

```
src/
├── main/java/smartcity/
│   ├── graph/
│   │   ├── scc/
│   │   │   ├── TarjanSCC.java
│   │   │   └── CondensationGraph.java
│   │   ├── topo/
│   │   │   ├── TopologicalSort.java
│   │   │   └── KahnTopological.java
│   │   └── dagsp/
│   │       ├── DAGShortestPath.java
│   │       ├── CriticalPath.java
│   │       └── Metrics.java
│   ├── model/
│   │   ├── Graph.java
│   │   ├── Vertex.java
│   │   ├── Edge.java
│   │   └── SCCResult.java
│   └── util/
│       ├── JSONParser.java
│       ├── DataGenerator.java
│       └── PerformanceMetrics.java
├── test/java/smartcity/
│   ├── SCCTest.java
│   ├── TopologicalSortTest.java
│   ├── DAGShortestPathTest.java
│   └── IntegrationTest.java
data/
├── task.json
├── small_1.json, small_2.json, small_3.json
├── medium_1.json, medium_2.json, medium_3.json
└── large_1.json, large_2.json, large_3.json
```

## Dataset Summary

| Category | Nodes | Description | Variants |
|----------|-------|-------------|----------|
| Small | 6-10 | Simple cases, 1-2 cycles or pure DAG | 3 |
| Medium | 10-20 | Mixed structures, several SCCs | 3 |
| Large | 20-50 | Performance and timing tests | 3 |

### Generated Datasets

1. **task.json** (8 nodes, 7 edges)
    - Cycle: 1→2→3→1
    - Path: 4→5→6→7
    - Isolated: 0

2. **Small Graphs** (6-10 nodes)
    - small_1.json: 6 nodes, 11 edges (cyclic)
    - small_2.json: 9 nodes, 12 edges (DAG)
    - small_3.json: 9 nodes, 18 edges (mixed)

3. **Medium Graphs** (10-20 nodes)
    - medium_1.json: 13 nodes, 28 edges (multiple SCCs)
    - medium_2.json: 17 nodes, 26 edges (sparse mixed)
    - medium_3.json: 20 nodes, 106 edges (dense cyclic)

4. **Large Graphs** (20-50 nodes)
    - large_1.json: 31 nodes, 59 edges (performance sparse)
    - large_2.json: 26 nodes, 135 edges (performance dense)
    - large_3.json: 49 nodes, 109 edges (complex cycles)

## Weight Model

**Edge Weights** are used to represent:
- Task dependencies and durations
- Resource transfer costs
- Priority constraints

All algorithms operate on edge weights rather than node durations for consistency and flexibility.

## Build & Run

### Prerequisites
- Java 11 or higher
- Maven 3.6+



## Performance Metrics

The system tracks the following metrics:

### Operation Counters
- **SCC**: DFS visits, edge relaxations
- **Topological Sort**: Kahn operations, edge relaxations
- **Shortest Path**: Edge relaxations, vertex visits

### Timing
- Execution time measured via `System.nanoTime()`
- Separate timing for each algorithm phase

### Example Output
```
=== Performance Summary ===
SCC: 78500 ns (450 operations)
Topological: 64300 ns (320 operations) 
ShortestPath: 85600 ns (280 relaxations)
Condensation: 425000 ns
CriticalPath: 127100 ns
```

## Algorithm Analysis

### SCC Detection (Tarjan's Algorithm)
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)
- **Bottlenecks**: Recursive DFS stack, component tracking
- **Optimizations**: Iterative DFS for large graphs

### Topological Sort (Kahn's Algorithm)
- **Time Complexity**: O(V + E)
- **Space Complexity**: O(V)
- **Advantages**: Naturally detects cycles, efficient for sparse graphs

### DAG Shortest Path
- **Time Complexity**: O(V + E)
- **Method**: Dynamic programming over topological order
- **Critical Path**: Via weight negation and shortest path

## Results Analysis

### Structure Impact on Performance

| Graph Type | SCC Time | Topo Time | SP Time | Notes |
|------------|----------|-----------|---------|--------|
| Pure DAG | Fast | Fast | Fast | Linear progression |
| Few Large SCCs | Medium | Fast | Medium | Condensation effective |
| Many Small SCCs | Fast | Medium | Fast | High component count |
| Dense Cyclic | Slow | N/A | N/A | Requires condensation |

### Key Findings

1. **SCC Performance**: Linear with graph size, slightly higher for cyclic graphs
2. **Topological Sort**: Most efficient for DAGs, fails gracefully on cyclic graphs
3. **Path Algorithms**: Efficient due to topological ordering advantage
4. **Memory Usage**: Proportional to graph size with minimal overhead

## Practical Recommendations

### When to Use Each Method

1. **SCC + Condensation**
    - Use when dependencies may contain cycles
    - Essential for real-world task scheduling
    - Provides component-level analysis

2. **Topological Sort**
    - Use for dependency resolution in build systems
    - Ideal for task scheduling without cycles
    - Efficient cycle detection

3. **DAG Shortest Path**
    - Use for critical path analysis
    - Optimal for project scheduling
    - Efficient due to topological ordering

### Implementation Guidelines

1. **For Small Graphs** (< 50 nodes): All algorithms perform well
2. **For Medium Graphs** (50-1000 nodes): Monitor memory for SCC
3. **For Large Graphs** (> 1000 nodes): Consider iterative DFS variants

## Code Quality Features

- **Modular Design**: Separate packages for each algorithm family
- **Comprehensive Testing**: JUnit tests covering edge cases
- **Documentation**: Javadoc for all public classes and methods
- **Error Handling**: Graceful failure with informative messages
- **Metrics Tracking**: Unified interface for performance monitoring

## Reproducibility

All datasets are generated with fixed random seeds ensuring consistent results. The project builds from a clean clone with standard Maven dependencies.

## Conclusion

This implementation successfully demonstrates the integration of SCC detection, topological sorting, and DAG path algorithms for practical scheduling problems. The system efficiently handles both cyclic and acyclic dependencies, providing optimal scheduling solutions for smart city task management.

The modular design and comprehensive testing ensure reliability, while performance metrics provide insights into algorithm behavior across different graph structures.