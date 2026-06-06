package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import cora.config.Settings;
import cora.reduction.Reducer;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class QuickSortTermTest {

  @Test
  void qSortTermTest() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs\n\n" +
        """
        test :: list -> list
        
        test(lst) -> quicksortList(lst)
        """
    );

    int[] arr = MemTRSUtil.genRandomArray(20, -100, 100);
    Term term = trs.lookupSymbol("test").apply(MemTRSUtil.arrayToListTerm(arr, trs));
    Arrays.sort(arr);

    int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

    assertArrayEquals(arr, arr2);
  }

  public static void qSortTermBenchmark() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs\n\n" +
        """
        test :: list -> list
        
        test(lst) -> quicksortList(lst)
        """
    );

    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {
        if (i != 128 || j != 0) break;
        int[] arr = MemTRSUtil.genRandomArray(i, -100, 100);
        //System.out.println(Arrays.toString(arr));

        Term term = trs.lookupSymbol("test").apply(MemTRSUtil.arrayToListTerm(arr, trs));
        Arrays.sort(arr);

        long start = System.currentTimeMillis();
        int[] arr2 = MemTRSUtil.listTermToArray(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start));

        assertArrayEquals(arr, arr2);
      }
    }
  }

  public static void main(String[] args) {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);
    qSortTermBenchmark();
    System.out.println(Reducer.totalVirtualThreads);
  }
}
