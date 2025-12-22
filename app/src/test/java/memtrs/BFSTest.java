package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import cora.reduction.MemReducer;
import cora.reduction.Reducer;
import memtrs.util.MemTRSUtil;
import memtrs.util.graph.Graph;
import org.graphstream.ui.view.Viewer;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;

public class BFSTest {

  void bfsTest1() {

  }

  static void mcBfsTest(int nodes, int edges) {
    System.setProperty("org.graphstream.ui", "swing");

    Reducer.totalParallelSteps = 0;

    Graph graph = Graph.generateRandomGraph(nodes, edges);

    try {
      graph.saveToImage(Path.of("graph_images/n" + nodes + "_e" + edges + "_out.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/bfs.lctrs\n\n" +
        """
          test :: graph -> Int
          test(g) -> mc_bfs(g, 0)
          """
    );

    MemReducer.resetMemory();
    MemReducer.SET(0, 1);

    Term term =
      trs.lookupSymbol("test").apply(MemTRSUtil.constructTerm(MemTRSUtil.graphToListTerm(graph),
        trs));

    int address = MemTRSUtil.termToInt(term, trs);
    //int[] arr = MemTRSUtil.termToArray(term, trs);

    int[] arr = new int[nodes];
    for (int k = address + 1; k < nodes + address + 1; k++) {
      arr[k - (address + 1)] = MemReducer.GET(k);
    }

    System.out.println(Arrays.toString(arr));
    System.out.println(Reducer.totalParallelSteps);
  }

  public static void main(String[] args) throws InterruptedException, InvocationTargetException {
    mcBfsTest(100, 100);
  }
}
