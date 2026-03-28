package memtrs;

import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.trs.TRS;
import cora.reduction.CalcReducer;
import cora.reduction.Reducer;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class QuickSortTest {

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
          test2(l, addr) -> test3(addr, qSort(addr, 0, getArrSize(addr) - 1))
          test3(addr, true) -> arrayToList(addr)
          """
    );

    int[] arr = MemTRSUtil.genRandomArray(100, -100, 100);

    Term term = trs.lookupSymbol("test").apply(MemTRSUtil.arrayToListTerm(arr, trs));
    Arrays.sort(arr);

    int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

    assertArrayEquals(arr, arr2);
  }


  public static void qSortBenchmark() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: list -> list
          test1 :: list -> Bool -> list
          test2 :: list -> Int -> list
          test3 :: Int -> Bool -> list
          
          test(l) -> test1(l, SET(0, 1))
          test1(l, true) -> test2(l, listToArray(l))
          test2(l, addr) -> test3(addr, qSort(addr, 0, getArrSize(addr) - 1))
          test3(addr, true) -> arrayToList(addr)
          """
    );
    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096}) {
      //for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        CalcReducer.resetMemory();

        int[] arr = MemTRSUtil.genRandomArray(i, -100, 100);
        System.out.println(Arrays.toString(arr));

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

  public static void qSortTimeBenchmarkMem() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: Int -> Bool
          
          test(addr) -> qSort(addr, 0, getArrSize(addr) - 1)
          """
    );
    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      //for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        CalcReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomArray(size, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
        System.out.println(Arrays.toString(arr));

        CalcReducer.SET(0, size + 2);
        CalcReducer.SET(1, size);
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

  public static void qSortJFRBenchmarkMem(int inputSize) {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: Int -> Bool
          
          test(addr) -> qSort(addr, 0, getArrSize(addr) - 1)
          """
    );

    int size = inputSize;

    int[] arr = MemTRSUtil.genRandomArray(size, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);

    CalcReducer.SET(0, size + 2);
    CalcReducer.SET(1, size);
    for (int k = 2; k < size + 2; k++) {
      CalcReducer.SET(k, arr[k - 2]);
    }

    Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(1));
    Arrays.sort(arr);

    Boolean b = MemTRSUtil.termToBool(term, trs);

    int[] arr2 = new int[size];
    for (int k = 2; k < size + 2; k++) {
      arr2[k - 2] = CalcReducer.GET(k);
    }

    assertEquals(true, b);
    assertArrayEquals(arr, arr2);
  }

  public static void qSortTotalParallelStepsMem() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: Int -> Bool
          
          test(addr) -> qSort(addr, 0, getArrSize(addr) - 1)
          """
    );
    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      //for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        Reducer.totalParallelSteps = 0;

        CalcReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomArray(size, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
        System.out.println(Arrays.toString(arr));

        CalcReducer.SET(0, size + 2);
        CalcReducer.SET(1, size);
        for (int k = 2; k < size + 2; k++) {
          CalcReducer.SET(k, arr[k - 2]);
        }

        Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(1));
        Arrays.sort(arr);

        long start = System.currentTimeMillis();
        Boolean b = MemTRSUtil.termToBool(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start) + ", " +
          "parallel rewrite steps: " + Reducer.totalParallelSteps);

        int[] arr2 = new int[size];
        for (int k = 2; k < size + 2; k++) {
          arr2[k - 2] = CalcReducer.GET(k);
        }

        assertEquals(true, b);
        assertArrayEquals(arr, arr2);
      }
    }
  }

  public static void qSortTimeBenchmarkList() {
    TRS trs = MemTRSUtil.constructTRS(
      """
        nil :: list
        cons :: Int -> list -> list
        
        quicksort :: list -> list
        quicksort(lst) -> qs(lst, nil)
        
        qs :: list -> list -> list
        qs(nil, rest) -> rest
        qs(cons(x, xs), rest) -> helper(x, xs, nil, nil, rest)
        
        helper :: Int -> list -> list -> list -> list -> list
        helper(pivot, cons(x, xs), ys, zs, rest) -> helper(pivot, xs, cons(x, ys), zs, rest) | x < pivot
        helper(pivot, cons(x, xs), ys, zs, rest) -> helper(pivot, xs, ys, cons(x, zs), rest) | x >= pivot
        helper(pivot, nil, ys, zs, rest) -> qs(ys, cons(pivot, qs(zs, rest)))
        """
    );

    Runtime runtime = Runtime.getRuntime();


    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {

        int[] arr = MemTRSUtil.genRandomArray(i, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
        System.out.println(Arrays.toString(arr));

        Term term = trs.lookupSymbol("quicksort").apply(MemTRSUtil.arrayToListTerm(arr, trs));
        Arrays.sort(arr);

        long startTime = System.currentTimeMillis();
        long startMem = runtime.totalMemory() - runtime.freeMemory();

        int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

        long endMem = runtime.totalMemory() - runtime.freeMemory();
        long endTime = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) +
          ", time delta: " + (endTime - startTime) +
          ", mem delta: " + (endMem - startMem));
        assertArrayEquals(arr, arr2);
      }
    }
  }

  public static void qSortJFRBenchmarkList(int inputSize) {
    TRS trs = MemTRSUtil.constructTRS(
      """
        nil :: list
        cons :: Int -> list -> list
        
        quicksort :: list -> list
        quicksort(lst) -> qs(lst, nil)
        
        qs :: list -> list -> list
        qs(nil, rest) -> rest
        qs(cons(x, xs), rest) -> helper(x, xs, nil, nil, rest)
        
        helper :: Int -> list -> list -> list -> list -> list
        helper(pivot, cons(x, xs), ys, zs, rest) -> helper(pivot, xs, cons(x, ys), zs, rest) | x < pivot
        helper(pivot, cons(x, xs), ys, zs, rest) -> helper(pivot, xs, ys, cons(x, zs), rest) | x >= pivot
        helper(pivot, nil, ys, zs, rest) -> qs(ys, cons(pivot, qs(zs, rest)))
        """
    );

    int[] arr = MemTRSUtil.genRandomArray(inputSize, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
    //System.out.println(Arrays.toString(arr));

    Term term = trs.lookupSymbol("quicksort").apply(MemTRSUtil.arrayToListTerm(arr, trs));
    Arrays.sort(arr);

    int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

    assertArrayEquals(arr, arr2);
  }

  public static void qSortTotalParallelStepsList() {
    TRS trs = MemTRSUtil.constructTRS(
      """
        nil :: list
        cons :: Int -> list -> list
        
        quicksort :: list -> list
        quicksort(lst) -> qs(lst, nil)
        
        qs :: list -> list -> list
        qs(nil, rest) -> rest
        qs(cons(x, xs), rest) -> helper(x, xs, nil, nil, rest)
        
        helper :: Int -> list -> list -> list -> list -> list
        helper(pivot, cons(x, xs), ys, zs, rest) -> helper(pivot, xs, cons(x, ys), zs, rest) | x < pivot
        helper(pivot, cons(x, xs), ys, zs, rest) -> helper(pivot, xs, ys, cons(x, zs), rest) | x >= pivot
        helper(pivot, nil, ys, zs, rest) -> qs(ys, cons(pivot, qs(zs, rest)))
        """
    );

    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {

        Reducer.totalParallelSteps = 0;

        int[] arr = MemTRSUtil.genRandomArray(i, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
        System.out.println(Arrays.toString(arr));

        Term term = trs.lookupSymbol("quicksort").apply(MemTRSUtil.arrayToListTerm(arr, trs));
        Arrays.sort(arr);

        long startTime = System.currentTimeMillis();

        int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

        long endTime = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) +
          ", time delta: " + (endTime - startTime) +
          ", parallel rewrite steps: " + Reducer.totalParallelSteps);
        assertArrayEquals(arr, arr2);
      }
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    // System.setOut(new PrintStream(new FileOutputStream("qsort_mem_experiments.txt")));
    // qSortBenchmark();
    // qSortBenchmark2();
    //qSortJFRBenchmarkMem(Integer.parseInt(args[0]));
    //qSortJFRBenchmarkList(Integer.parseInt(args[0]));
    //qSortTimeBenchmarkMem();
    //qSortTimeBenchmarkList();
    //qSortTotalParallelStepsMem();
    qSortTotalParallelStepsList();
  }
}
