package memtrs.util;

import charlie.reader.CoraInputReader;
import charlie.terms.Term;
import charlie.trs.TRS;
import cora.config.Settings;
import cora.reduction.Reducer;
import memtrs.util.graph.Graph;
import memtrs.util.graph.Vertex;
import memtrs.util.matrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MemTRSUtil {

  public final static String MEMTRS_STDLIB_PATH =
    "/home/mikhirurg/Contribution/cora/memtrs/preproc/stdlib/";

  public static Settings.Strategy STRATEGY = Settings.Strategy.CallByValue;

  private static Random random = new Random();

  public static Term reduceToNF(Term start, TRS trs, Settings.Strategy strategy) {
    Settings.Strategy oldStrategy = Settings.queryRewritingStrategy();
    Settings.setStrategy(strategy);

    Reducer reducer = new Reducer(trs);
    Term s = start;
    Term oldS = null;
    do {
      oldS = s;
      s = reducer.reduce(s);
    } while (s != null);

    Settings.setStrategy(oldStrategy);
    return oldS;
  }

  public static Integer termToInt(Term intTerm, TRS trs) {
    Term t = reduceToNF(intTerm, trs, STRATEGY);
    try {
      return Integer.parseInt(t.toString());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static List<Integer> termToList(Term listTerm, TRS trs) {
    List<Integer> list = new ArrayList<>();

    Term t = reduceToNF(listTerm, trs, STRATEGY);
    while (!"nil".equals(t.toString())) {
      if ("cons".equals(t.queryRoot().queryName())) {
        list.add(Integer.parseInt(t.queryArgument(1).toString()));
        t = reduceToNF(t.queryArgument(2), trs, STRATEGY);
      } else {
        return null;
      }
    }
    return list;
  }

  public static int[] termToArray(Term listTerm, TRS trs) {
    List<Integer> result = termToList(listTerm, trs);
    if (result == null) {
      return null;
    }

    int[] arr = new int[result.size()];
    for (int i = 0; i < result.size(); i++) {
      arr[i] = result.get(i);
    }

    return arr;
  }

  public static String arrayToListTerm(int[] arr) {
    StringBuilder builder = new StringBuilder();
    for (int j : arr) {
      builder.append("cons(")
        .append(j)
        .append(", ");
    }
    builder.append("nil");
    builder.append(")".repeat(arr.length));
    return builder.toString();
  }

  public static int[] genRandomArray(int size, int min, int max) {
    int[] arr = new int[size];
    for (int i = 0; i < size; i++) {
      arr[i] = random.nextInt(min, max);
    }

    return arr;
  }

  public static Matrix termToMatrix(Term matrixTerm, TRS trs) {
    List<List<Integer>> matrixList = new ArrayList<>();
    Term t = reduceToNF(matrixTerm, trs, STRATEGY);

    while (!"rowNil".equals(t.toString())) {
      if ("row".equals(t.queryRoot().queryName())) {
        List<Integer> list = termToList(t.queryArgument(1), trs);
        if (list != null) {
          matrixList.add(list);
        } else {
          return null;
        }
        t = reduceToNF(t.queryArgument(2), trs, STRATEGY);
      } else {
        return null;
      }
    }

    int height = matrixList.size();
    int width = matrixList.getFirst().size();
    Matrix matrix = new Matrix(width, height);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        matrix.set(x, y, matrixList.get(y).get(x));
      }
    }

    return matrix;
  }

  public static Graph termToGraph(Term graphTerm, TRS trs) {
    Graph graph = new Graph();
    Term t = reduceToNF(graphTerm, trs, STRATEGY);

    int vertex = 0;
    while (!"nilG".equals(t.toString())) {
      if ("consG".equals(t.queryRoot().queryName())) {
        List<Integer> list = termToList(t.queryArgument(1), trs);
        if (list != null) {
          for (int u : list) {
            graph.addEdge(new Vertex(vertex), new Vertex(u));
          }
        } else {
          return null;
        }
        t = reduceToNF(t.queryArgument(2), trs, STRATEGY);
        vertex++;
      } else {
        return null;
      }
    }

    return graph;
  }

  public static String graphToListTerm(Graph graph) {
    StringBuilder builder = new StringBuilder();
    for (Vertex v : graph.getVertices()) {
      int[] arr = new int[graph.getNeighbours(v).size()];
      for (int i = 0; i < graph.getNeighbours(v).size(); i++) {
        arr[i] = graph.getNeighbours(v).get(i).to().id();
      }
      builder.append("consG(");
      builder.append(arrayToListTerm(arr));
      builder.append(", ");
    }
    builder.append("nilG")
      .append(")".repeat(graph.getVertices().size()));

    return builder.toString();
  }

  public static TRS constructTRS(String trsDefinition) {
    TRS trs = CoraInputReader.readTrsFromString(trsDefinition);
    return trs;
  }

  public static Term constructTerm(String termDefinition, TRS trs) {
    return CoraInputReader.readTerm(termDefinition, trs);
  }

  public static void main(String[] args) {
    TRS trs = CoraInputReader.readTrsFromString(
      """
      nil :: list
      cons :: Int -> list -> list
      x::list
      """
    );

    Term listTerm = CoraInputReader.readTerm(
      "cons(1, cons(2, x))",
      trs
    );
    List<Integer> list = termToList(listTerm, trs);
  }
}
