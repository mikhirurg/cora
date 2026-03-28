package memtrs;

import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.trs.TRS;
import cora.reduction.CalcReducer;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeapSortTest {

  @Test
  void heapSortTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/heapsort.lctrs\n\n" +
        """
          test :: Int -> Bool
          
          test(addr) -> heapSort(addr)
          """
    );
    //for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
    for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        CalcReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomArray(size, -100, 100);

        CalcReducer.SET(0, size + 3);
        CalcReducer.SET(1, 0);
        CalcReducer.SET(2, size);
        for (int k = 2; k < size + 2; k++) {
          CalcReducer.SET(k, arr[k - 2]);
        }

        Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(1));
        Arrays.sort(arr);

        long start = System.currentTimeMillis();
        Boolean b = MemTRSUtil.termToBool(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start));

        int[] arr2 = new int[size];
        for (int k = 2; k < size + 2; k++) {
          arr2[k - 2] = CalcReducer.GET(k);
        }

        assertEquals(true, b);
        assertArrayEquals(arr, arr2);
      }
    }
  }
}
