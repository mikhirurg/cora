package memtrs;

import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.trs.TRS;
import cora.reduction.MemReducer;
import cora.reduction.Reducer;
import memtrs.util.MemTRSUtil;
import memtrs.util.graph.Edge;
import memtrs.util.graph.Graph;
import memtrs.util.graph.Vertex;
import memtrs.util.matrix.Matrix;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloydWarshallTest {

  public static Graph graph1;
  /* Example graph:
     (1) --> (2)
      |       ^
      |       |
      |    > (5)
      |   /
      |  /
      v /
     (3) --> (4) */
  static {
    graph1 = new Graph();
    graph1.addVertice(new Vertex(0));
    graph1.addVertice(new Vertex(1));
    graph1.addVertice(new Vertex(2));
    graph1.addVertice(new Vertex(3));
    graph1.addVertice(new Vertex(4));

    graph1.addEdge(new Vertex(0), new Vertex(1));
    graph1.addEdge(new Vertex(0), new Vertex(2));
    graph1.addEdge(new Vertex(2), new Vertex(3));
    graph1.addEdge(new Vertex(2), new Vertex(4));
    graph1.addEdge(new Vertex(4), new Vertex(1));
  }

  private static Matrix floydWarshall(Graph graph) {
    int[][] dist = new int[graph.getVertices().size()][graph.getVertices().size()];

    for (int i = 0; i < graph.getVertices().size(); i++) {
      for (int j = 0; j < graph.getVertices().size(); j++) {
        dist[i][j] = graph.getVertices().size() * 100;
      }
    }

    for (Edge edge : graph.getEdges()) {
      dist[edge.from().id()][edge.to().id()] = 1;
    }

    for (Vertex vertex : graph.getVertices()) {
      dist[vertex.id()][vertex.id()] = 0;
    }

    for (int k = 0; k < graph.getVertices().size(); k++) {
      for (int i = 0; i < graph.getVertices().size(); i++) {
        for (int j = 0; j < graph.getVertices().size(); j++) {
          if (dist[i][j] > dist[i][k] + dist[k][j]) {
            dist[i][j] = dist[i][k] + dist[k][j];
          }
        }
      }
      System.out.println(Arrays.deepToString(dist));
    }

    return new Matrix(dist);
  }

  @Test
  void exampleGraph() {
    Graph graph = new Graph();
    Vertex v1 = new Vertex(0);
    Vertex v2 = new Vertex(1);
    Vertex v3 = new Vertex(2);
    Vertex v4 = new Vertex(3);
    graph.addVertice(v1);
    graph.addVertice(v2);
    graph.addVertice(v3);
    graph.addVertice(v4);

    graph.addEdge(v1, v2);
    graph.addEdge(v1, v3);
    graph.addEdge(v2, v3);
    graph.addEdge(v2, v4);
    graph.addEdge(v3, v1);
    graph.addEdge(v3, v4);
    graph.addEdge(v4, v2);

    //floydWarshall(graph);

    TRS trs = MemTRSUtil.constructTRS("#include " + MEMTRS_STDLIB_PATH + "algorithms/floyd_warshall.lctrs\n\n" +
      """
      """);

    Term graphTerm = MemTRSUtil.graphToTerm(graph1, trs);
    System.out.println(graphTerm);
    Term result =
      trs.lookupSymbol("graphToMatrix").apply(graphTerm).apply(TheoryFactory.createValue(0));

    MemReducer.resetMemory();
    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);

    int address = MemTRSUtil.termToInt(result, trs);

    System.out.println(MemReducer.MEMORY);
  }

  static void floydWarshallTest(int nodes, int edges, int iteration) {
    System.setProperty("org.graphstream.ui", "swing");

    Reducer.totalParallelSteps = 0;

    Graph graph = Graph.generateRandomGraph(nodes, edges);

    try {
      graph.saveToImage(Path.of("graph_images_floyd_warshall/n" + nodes + "_e" + edges + "_i" + iteration +
        "_out.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/floyd_warshall.lctrs\n\n" +
        """
        test :: graph -> Int
        test1 :: graph -> Bool -> Int
        
        test(g) -> test1(g, SET(0, 1))
        test1(g, true) -> floydWarshall(g)
        """
    );

    MemReducer.resetMemory();
    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);

    Term term = trs.lookupSymbol("test").apply(MemTRSUtil.graphToTerm(graph, trs));

    int address = MemTRSUtil.termToInt(term, trs);

    Matrix expected = floydWarshall(graph);
    Matrix actual = MemTRSUtil.matrixFromMem(address);

    assertEquals(expected, actual);

    System.out.println(actual);
    System.out.println("nodes: " + nodes + ", edges: " + edges + ", iteration: " + iteration +
      ", parallel steps: " + Reducer.totalParallelSteps);
  }

  static void floydWarshallParallelTest(int nodes, int edges, int iteration) {
    System.setProperty("org.graphstream.ui", "swing");

    Reducer.totalParallelSteps = 0;

    Graph graph = Graph.generateRandomGraph(nodes, edges);

    try {
      graph.saveToImage(Path.of("graph_images_floyd_warshall_par/n" + nodes + "_e" + edges + "_i" + iteration +
        "_out.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/floyd_warshall.lctrs\n\n" +
        """
        test :: graph -> Int
        
        test(g) -> parFloydWarshall(g)
        """
    );

    MemReducer.resetMemory();
    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);

    Term term = trs.lookupSymbol("test").apply(MemTRSUtil.graphToTerm(graph, trs));

    int address = MemTRSUtil.termToInt(term, trs);

    Matrix expected = floydWarshall(graph);
    Matrix actual = MemTRSUtil.matrixFromMem(address);


    System.out.println(actual);
    System.out.println("nodes: " + nodes + ", edges: " + edges + ", iteration: " + iteration +
      ", parallel steps: " + Reducer.totalParallelSteps);
    //System.out.println(CalcReducer.MEMORY);
    assertEquals(expected, actual);
  }

  @Test
  void floydWarshallTest1() {

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/floyd_warshall.lctrs\n\n" +
        """
        test :: graph -> Int
        test1 :: graph -> Bool -> Bool -> Int
        
        test(g) -> test1(g, SET(0, 2), SET(1, 0))
        test1(g, true, true) -> floydWarshall(g)
        """
    );

    Term term = MemTRSUtil
      .constructTerm("test(" +MemTRSUtil.graphToTermString(graph1) + ")", trs);

    Matrix expected = floydWarshall(graph1);
    Matrix actual = MemTRSUtil.matrixFromMem(MemTRSUtil.termToInt(term, trs));

    assertEquals(expected, actual);
  }

  @Test
  void floydWarshallParallelTest1() {

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/floyd_warshall.lctrs\n\n" +
        """
        test :: graph -> Int
        test1 :: graph -> Bool -> Bool -> Int
        
        test(g) -> test1(g, SET(0, 2), SET(1, 0))
        test1(g, true, true) -> parFloydWarshall(g)
        """
    );

    Term term = MemTRSUtil
      .constructTerm("test(" +MemTRSUtil.graphToTermString(graph1) + ")", trs);

    Matrix expected = floydWarshall(graph1);
    Matrix actual = MemTRSUtil.matrixFromMem(MemTRSUtil.termToInt(term, trs));

    System.out.println("Expected:");
    System.out.println(expected);

    System.out.println("Actual:");
    System.out.println(actual);

    assertEquals(expected, actual);
  }

  public static void main(String[] args) throws FileNotFoundException {
/*    System.setOut(new PrintStream(new FileOutputStream("floyd_warshall_not_par.txt")));
    for (int i = 1; i < 30; i++) {
      for (int j = 1; j <= i * i; j++) {
        for (int k = 0; k < 3; k++) {
          floydWarshallTest(i, j, k);
        }
      }
    }*/

    System.setOut(new PrintStream(new FileOutputStream("floyd_warshall_fix.txt")));

    //TRS trs = MemTRSUtil.constructTRS("");
    //System.out.println(MemTRSUtil.termToInt(MemTRSUtil.constructTerm("3 % 2", trs),
    // trs));
    for (int i = 1; i < 30; i++) {
      for (int j = 1; j <= i * i; j++) {
        for (int k = 0; k < 3; k++) {
          System.err.println(i + ", " + j + ", " + k);
          floydWarshallParallelTest(i, j, k);
        }
      }
    }

  }
}