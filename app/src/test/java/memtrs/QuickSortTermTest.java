package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class QuickSortTermTest {

  @Test
  void qSortTermTest() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs\n\n" +
        """
        test :: list -> list
        
        test(lst) -> qSort_term(lst, 0, listLen(lst))
        """
    );

    int[] arr = MemTRSUtil.genRandomArray(20, -100, 100);
    Term term = trs.lookupSymbol("test").apply(MemTRSUtil.arrayToListTerm(arr, trs));
    Arrays.sort(arr);

    int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

    assertArrayEquals(arr, arr2);
  }

  public static void qSortTermBenchmark() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs\n\n" +
        """
        test :: list -> list
        
        test(lst) -> qSort_term(lst, 0, listLen(lst))
        """
    );

    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {
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
    qSortTermBenchmark();
  }
}
