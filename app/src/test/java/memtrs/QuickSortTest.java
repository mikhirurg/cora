package memtrs;

import charlie.terms.Term;
import charlie.terms.TermFactory;
import charlie.trs.TRS;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuickSortTest {

  @Test
  void qSortPartitionTest1() {

  }

  @Test
  void qSortTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
        test :: list -> list
        test1 :: list -> Bool -> list
        test2 :: list -> Int -> list
        test3 :: Int -> Bool -> list
        
        test(l) -> test1(l, SET(0, 1))
        test1(l, true) -> test2(l, listToArray(l))
        test2(l, addr) -> test3(addr, qSort(addr, 0, getArrSize(addr)))
        test3(addr, true) -> arrayToList(addr)
        """
    );
    for (int i : new int[] {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {
        int[] arr = MemTRSUtil.genRandomArray(i, -100, 100);
        //System.out.println(Arrays.toString(arr));

        Term term = trs.lookupSymbol("test").apply(MemTRSUtil.arrayToListTerm(arr, trs));
        Arrays.sort(arr);

        long start = System.currentTimeMillis();
        int[] arr2 = MemTRSUtil.termToArray(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start));

        assertArrayEquals(arr, arr2);
      }
    }
  }
}
