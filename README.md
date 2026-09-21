# TreeScope FX: Interactive Binary Tree Visualizer & Benchmarking Tool

A comprehensive JavaFX application designed for real-time visualization, manipulation, and algorithmic performance evaluation of Binary Search Trees (BST). The tool supports polymorphic data types, dynamic canvas rendering, and rigorous testing suites.

---

##  Key Features

* **Interactive Dynamic Rendering**: Visualizes binary trees on an adaptable canvas (`TreeViewPane`) with coordinate calculations and node hit-testing.
* **Polymorphic Data Model**: Extensible factory pattern (`UserFactory`, `UserType`) supporting:
  * Integers (`IntType`)
  * 2D Geometric Points (`PointType`, `Point2D`)
  * Calendar Dates (`DateType`)
* **Core Tree Operations**:
  * Insertion, deletion, search, and balance routines.
  * In-order traversal (`InOrderIterator`) and custom actions (`DoWith`).
* **Comprehensive Test & Benchmark Suite**:
  * **White-Box Testing**: Exhaustive structural coverage of tree mutations.
  * **Balance Validation**: Automated verification of tree invariants and height balancing.
  * **Performance & Memory Profiling**: Benchmark utilities (`BalancePerformanceTest`, `MemoryDemo`) for stress testing.

---

##  Architecture & Project Structure

```text
src/
├── main/
│   ├── java/org/example/kitpo_l1/
│   │   ├── BinaryTree.java          # Core tree implementation & iterators
│   │   ├── TreeViewPane.java        # Canvas rendering & interaction handling
│   │   ├── MainController.java      # JavaFX UI event handlers & logic
│   │   ├── UserType.java            # Abstract type contract
│   │   ├── UserFactory.java         # Factory for dynamic instantiation
│   │   ├── IntType.java / PointType.java / DateType.java
│   │   └── Main.java                # Application entry point
│   └── resources/
│       └── org/example/kitpo_l1/
│           └── main-view.fxml       # Layout and UI component structure
└── test/
    └── java/org/example/kitpo_l1/
        ├── BinaryTreeWhiteBoxTest.java   # Unit & white-box logic coverage
        ├── BinaryTreeBalanceTest.java    # Balance condition tests
        ├── BalancePerformanceTest.java  # Latency & throughput analysis
        └── MemoryDemo.java               # Heap & memory footprint checks
