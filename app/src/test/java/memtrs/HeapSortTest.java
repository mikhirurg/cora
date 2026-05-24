package memtrs;

import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.trs.TRS;
import cora.reduction.MemReducer;
import cora.reduction.Reducer;
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
    int j = 0;
    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
    //for (int i = 1; i < 2000; i += 10) {
      //for (int j = 0; j < 3; j++) {

        Reducer.totalParallelSteps = 0;
        MemReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomArray(size, -100, 100);

        int arrAddr = 2;

        MemReducer.SET(0, arrAddr + size + 1);
        MemReducer.SET(1, 0);
        MemReducer.SET(arrAddr, size);

        for (int k = 0; k < size; k++) {
          MemReducer.SET(arrAddr + k + 1, arr[k]);
        }

        Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(arrAddr));
        Arrays.sort(arr);

        long start = System.currentTimeMillis();
        Boolean b = MemTRSUtil.termToBool(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start) +
          ", parallel steps: " + Reducer.totalParallelSteps);

        int[] arr2 = new int[size];
        for (int k = 0; k < size; k++) {
          arr2[k] = MemReducer.GET(arrAddr + k + 1);
        }

        assertEquals(true, b);
        assertArrayEquals(arr, arr2);
     // }
    }
  }
}
