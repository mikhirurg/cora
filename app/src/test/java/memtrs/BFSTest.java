package memtrs;

import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.trs.TRS;
import cora.reduction.CalcReducer;
import cora.reduction.MemReducer;
import cora.reduction.Reducer;
import memtrs.util.MemTRSUtil;
import memtrs.util.graph.Edge;
import memtrs.util.graph.Graph;
import memtrs.util.graph.Vertex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;

public class BFSTest {

  static int[] bfs(Graph graph, Vertex startNode) {
    Queue<Vertex> queue = new LinkedList<>();
    Set<Vertex> explored = new TreeSet<>();
    explored.add(startNode);
    queue.add(startNode);
    while (!queue.isEmpty()) {
      Vertex v = queue.poll();
      for (Edge edge : graph.getNeighbours(v)) {
        Vertex w = edge.to();
        if (!explored.contains(w)) {
          explored.add(w);
          queue.add(w);
        }
      }
    }

    int[] result = new int[graph.getVertices().size()];
    int i = 0;
    for (Vertex v : graph.getVertices()) {
      if (explored.contains(v)) {
        result[i] = 1;
      } else {
        result[i] = 0;
      }
      i++;
    }

    return result;
  }

  static void bfsTest(int nodes, int edges, int iteration) {
    System.setProperty("org.graphstream.ui", "swing");

    Reducer.totalParallelSteps = 0;

    Graph graph = Graph.generateRandomGraph(nodes, edges);

    /*try {
      graph.saveToImage(Path.of("graph_images_bfs1_not_par/n" + nodes + "_e" + edges + "_i" + iteration +
        "_out.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }*/

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/bfs_term.lctrs\n\n" +
        """
          test :: graph -> list
          test(g) -> seqBFS(g, 0)
          """
    );

    MemReducer.resetMemory();
    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);

    Term term =
      trs.lookupSymbol("test").apply(MemTRSUtil.constructTerm(MemTRSUtil.graphToTermString(graph),
        trs));

    int[] arr = MemTRSUtil.listTermToArray(term, trs);

    Assertions.assertArrayEquals(bfs(graph, new Vertex(0)), arr);

    System.out.println(Arrays.toString(arr));
    System.out.println("nodes: " + nodes + ", edges: " + edges + ", iteration: " + iteration +
      ", parallel steps: " + Reducer.totalParallelSteps);
  }

  static void mcBfsTest(int nodes, int edges, int iteration) {
    System.setProperty("org.graphstream.ui", "swing");

    Reducer.totalParallelSteps = 0;

    Graph graph = Graph.generateRandomGraph(nodes, edges);
    /*graph = new Graph();
    graph.addEdge(new Vertex(0), new Vertex(1));
    graph.addEdge(new Vertex(1), new Vertex(0));
    graph.addEdge(new Vertex(1), new Vertex(1));
    graph.addEdge(new Vertex(0), new Vertex(2));
    graph.addVertice(new Vertex(3));*/
  /*
    try {
      graph.saveToImage(Path.of("graph_images_bfs1/n" + nodes + "_e" + edges + "_i" + iteration +
        "_out.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
*/
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/bfs.lctrs\n\n" +
        """
          test :: graph -> Int
          test(g) -> parBFS(g, 0)
          """
    );

    MemReducer.resetMemory();
    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);

    Term term =
      trs.lookupSymbol("test").apply(MemTRSUtil.constructTerm(MemTRSUtil.graphToTermString(graph),
        trs));

    int address = MemTRSUtil.termToInt(term, trs);

    int[] arr = new int[nodes];
    for (int k = address + 1; k < nodes + address + 1; k++) {
      arr[k - (address + 1)] = MemReducer.GET(k);
    }

    int[] expected = bfs(graph, new Vertex(0));

    System.out.println(Arrays.toString(expected));
    System.out.println(Arrays.toString(arr));
    System.out.println("nodes: " + nodes + ", edges: " + edges + ", iteration: " + iteration +
      ", parallel steps: " + Reducer.totalParallelSteps);
    Assertions.assertArrayEquals(expected, arr);
  }

  @Test
  void termBFSTest() {
    bfsTest(3, 4, 0);
    bfsTest(4, 8, 0);
    bfsTest(6, 35, 0);
    bfsTest(8, 20, 0);
    bfsTest(8, 55, 0);
    bfsTest(11, 53, 0);
    bfsTest(12, 49, 0);
    // bfsTest(12, 112, 0);
    // bfsTest(15, 224, 0);
  }

  @Test
  void memBFSTest() {
    mcBfsTest(3, 4, 0);
    mcBfsTest(4, 8, 0);
    mcBfsTest(6, 35, 0);
    mcBfsTest(8, 20, 0);
    mcBfsTest(8, 55, 0);
    mcBfsTest(11, 53, 0);
    mcBfsTest(12, 49, 0);
    mcBfsTest(12, 112, 0);
    mcBfsTest(15, 224, 0);
  }

  @Test
  void graphDemoTest() throws FileNotFoundException {
    System.setOut(new PrintStream(new FileOutputStream("out.txt")));
    TRS trs = MemTRSUtil.constructTRS("#include " + MEMTRS_STDLIB_PATH + "algorithms/bfs.lctrs\n\n" +
      """
      """);

    Term graphTerm = MemTRSUtil.graphToTerm(FloydWarshallTest.graph1, trs);
    System.out.println(graphTerm);
    Term result =
      trs.lookupSymbol("parBFS").apply(graphTerm).apply(TheoryFactory.createValue(0));

    MemReducer.resetMemory();
    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);

    int address = MemTRSUtil.termToInt(result, trs);

    System.out.println(MemReducer.MEMORY);
  }

  public static void main(String[] args) throws FileNotFoundException {
    int[] sizes = IntStream.iterate(1, i -> i + 1).limit(30).toArray();


    System.setOut(new PrintStream(new FileOutputStream("bfs_test1_not_par_cas_trie.txt")));
    for (int i = 1; i < 30; i++) {
      for (int j = 1; j <= i * i; j++) {
        for (int k = 0; k < 3; k++) {
          System.err.println(i + ", " + j + ", " + k);
          bfsTest(i, j, k);
        }
      }
    }


    //mcBfsTest(4,4,0);

    /*
    System.setOut(new PrintStream(new FileOutputStream("bfs_test1_par_cas_1.txt")));
    for (int i = 1; i < 30; i++) {
      for (int j = 1; j <= i * i; j++) {
        for (int k = 0; k < 3; k++) {
          System.err.println(i + ", " + j + ", " + k);
          mcBfsTest(i, j, k);
        }
      }
    }*/
    //mcBfsTest(5, 5, 1);
  }
}
