package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import memtrs.util.MemTRSUtil;
import memtrs.util.graph.Edge;
import memtrs.util.graph.Graph;
import memtrs.util.graph.Vertex;
import memtrs.util.matrix.Matrix;
import org.junit.jupiter.api.Test;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloydWarshallTest {

  public static Graph graph1;

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
    }

    return new Matrix(dist);
  }

  @Test
  void floydWarshallTest1() {

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/floyd_warshall.lctrs\n\n" +
        """
        test :: graph -> matrix
        test1 :: graph -> Bool -> matrix
        test2 :: Int -> matrix
        
        test(g) -> test1(g, SET(0, 1))
        test1(g, true) -> test2(floyd_warshall(g))
        test2(addr) -> matrixToTerm(addr)
        """
    );

    Term term = MemTRSUtil
      .constructTerm("test(" +MemTRSUtil.graphToListTerm(graph1) + ")", trs);

    Matrix expected = floydWarshall(graph1);
    Matrix actual = MemTRSUtil.termToMatrix(term, trs);

    assertEquals(expected, actual);
  }

  @Test
  void floydWarshallParallelTest1() {

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/floyd_warshall.lctrs\n\n" +
        """
        test :: graph -> matrix
        test1 :: graph -> Bool -> matrix
        test2 :: Int -> matrix
        
        test(g) -> test1(g, SET(0, 1))
        test1(g, true) -> test2(floyd_warshall_par(g))
        test2(addr) -> matrixToTerm(addr)
        """
    );

    Term term = MemTRSUtil
      .constructTerm("test(" +MemTRSUtil.graphToListTerm(graph1) + ")", trs);

    Matrix expected = floydWarshall(graph1);
    Matrix actual = MemTRSUtil.termToMatrix(term, trs);

    System.out.println("Expected:");
    System.out.println(expected);

    System.out.println("Actual:");
    System.out.println(actual);

    assertEquals(expected, actual);
  }
}