package memtrs;

import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.trs.TRS;
import cora.config.Settings;
import cora.reduction.CalcReducer;
import cora.reduction.MemReducer;
import cora.reduction.Reducer;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class QuickSortTest {
  private static final int[] QSORT_INPUT_SIZES =
    new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};

  private static final String MEM_QSORT_TEST_TRS =
    "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
      """
        test :: Int -> Bool
        
        test(addr) -> qsortArray(addr, 0, getArrSize(addr) - 1)
        """;

  @Test
  void qSortTest1() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: list -> list
          test1 :: list -> Bool -> list
          test2 :: list -> Int -> list
          test3 :: Int -> Bool -> list
          
          test(l) -> test1(l, SET(0, 1))
          test1(l, true) -> test2(l, listToArray(l))
          test2(l, addr) -> test3(addr, qsortArray(addr, 0, getArrSize(addr) - 1))
          test3(addr, true) -> arrayToList(addr)
          """
    );

    int[] arr = MemTRSUtil.genRandomArray(100, -100, 100);

    Term term = trs.lookupSymbol("test").apply(MemTRSUtil.arrayToListTerm(arr, trs));
    Arrays.sort(arr);

    int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

    assertArrayEquals(arr, arr2);
  }

  @Test
  void qSortTest2() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs"
    );

    int[] arr = MemTRSUtil.genRandomArray(100, -100, 100);

    Term term = trs.lookupSymbol("quicksortList").apply(MemTRSUtil.arrayToListTerm(arr, trs));
    Arrays.sort(arr);

    int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

    assertArrayEquals(arr, arr2);
  }


  public static void qSortBenchmark() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: list -> list
          test1 :: list -> Bool -> list
          test2 :: list -> Int -> list
          test3 :: Int -> Bool -> list
          
          test(l) -> test1(l, SET(0, 1))
          test1(l, true) -> test2(l, listToArray(l))
          test2(l, addr) -> test3(addr, qsortArray(addr, 0, getArrSize(addr) - 1))
          test3(addr, true) -> arrayToList(addr)
          """
    );
    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096}) {
      //for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        MemReducer.resetMemory();

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
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: Int -> Bool
          
          test(addr) -> qsortArray(addr, 0, getArrSize(addr) - 1)
          """
    );
    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      //for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        MemReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomArray(size, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
        System.out.println(Arrays.toString(arr));

        MemReducer.SET(0, size + 2);
        MemReducer.SET(1, size);
        for (int k = 2; k < size + 2; k++) {
          MemReducer.SET(k, arr[k - 2]);
        }

        Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(1));
        Arrays.sort(arr);

        long start = System.currentTimeMillis();
        Boolean b = MemTRSUtil.termToBool(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start));

        int[] arr2 = new int[size];
        for (int k = 2; k < size + 2; k++) {
          arr2[k - 2] = MemReducer.GET(k);
        }

        assertEquals(true, b);
        assertArrayEquals(arr, arr2);
      }
    }
  }

  public static void qSortJFRBenchmarkMem(int inputSize) {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: Int -> Bool
          
          test(addr) -> qsortArray(addr, 0, getArrSize(addr) - 1)
          """
    );

    int size = inputSize;

    int[] arr = MemTRSUtil.genRandomArray(size, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);

    MemReducer.SET(0, size + 2);
    MemReducer.SET(1, size);
    for (int k = 2; k < size + 2; k++) {
      MemReducer.SET(k, arr[k - 2]);
    }

    Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(1));
    Arrays.sort(arr);

    Boolean b = MemTRSUtil.termToBool(term, trs);

    int[] arr2 = new int[size];
    for (int k = 2; k < size + 2; k++) {
      arr2[k - 2] = MemReducer.GET(k);
    }

    assertEquals(true, b);
    assertArrayEquals(arr, arr2);
  }

  public static void qSortTotalParallelStepsMem() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/quicksort.lctrs\n\n" +
        """
          test :: Int -> Bool
          
          test(addr) -> qsortArray(addr, 0, getArrSize(addr) - 1)
          """
    );
    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      //for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        Reducer.totalParallelSteps = 0;

        MemReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomArray(size, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);

        MemReducer.SET(0, size + 2);
        MemReducer.SET(1, size);
        for (int k = 2; k < size + 2; k++) {
          MemReducer.SET(k, arr[k - 2]);
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
          arr2[k - 2] = MemReducer.GET(k);
        }

        assertEquals(true, b);
        assertArrayEquals(arr, arr2);
      }
    }
  }

  public static void qSortTimeBenchmarkList() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs"
    );

    Runtime runtime = Runtime.getRuntime();


    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {

        int[] arr = MemTRSUtil.genRandomArray(i, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
        System.out.println(Arrays.toString(arr));

        Term term = trs.lookupSymbol("quicksortList").apply(MemTRSUtil.arrayToListTerm(arr, trs));
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

  public static void qSortParallelStepBenchmarkList() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs"
    );

    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {

        int[] arr = MemTRSUtil.genRandomArray(i, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
        System.out.println(Arrays.toString(arr));

        Term term = trs.lookupSymbol("quicksortList").apply(MemTRSUtil.arrayToListTerm(arr, trs));
        Arrays.sort(arr);

        Reducer.totalParallelSteps = 0;

        int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

        System.out.println("n: " + i + ", i: " + (j + 1) + ", steps: " + Reducer.totalParallelSteps);
        assertArrayEquals(arr, arr2);
      }
    }
  }

  public static void qSortJFRBenchmarkList(int inputSize) {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs"
    );

    int[] arr = MemTRSUtil.genRandomArray(inputSize, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
    //System.out.println(Arrays.toString(arr));

    Term term = trs.lookupSymbol("quicksortList").apply(MemTRSUtil.arrayToListTerm(arr, trs));
    Arrays.sort(arr);

    int[] arr2 = MemTRSUtil.listTermToArray(term, trs);

    assertArrayEquals(arr, arr2);
  }

  public static void qSortTotalParallelStepsList() {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs"
    );

    for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
      for (int j = 0; j < 3; j++) {

        Reducer.totalParallelSteps = 0;

        int[] arr = MemTRSUtil.genRandomArray(i, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);

        Term term = trs.lookupSymbol("quicksortList").apply(MemTRSUtil.arrayToListTerm(arr, trs));
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

  private static int[] genBenchmarkArray(int size, int iteration) {
    Random random = new Random(1337L + 31L * size + iteration);
    int[] arr = new int[size];
    for (int i = 0; i < size; i++) {
      arr[i] = random.nextInt(Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2);
    }
    return arr;
  }

  private static int[] sortedCopy(int[] arr) {
    int[] copy = Arrays.copyOf(arr, arr.length);
    Arrays.sort(copy);
    return copy;
  }

  private static void writeArrayToMemory(int[] arr) {
    MemReducer.SET(0, arr.length + 2);
    MemReducer.SET(1, arr.length);
    for (int i = 0; i < arr.length; i++) {
      MemReducer.SET(i + 2, arr[i]);
    }
  }

  private static int[] normalListTermToArray(Term normalFormList) {
    int[] result = new int[listLength(normalFormList)];
    Term t = normalFormList;
    int index = 0;
    while (!"nil".equals(t.toString())) {
      if (!"cons".equals(t.queryRoot().queryName())) {
        return null;
      }
      result[index] = Integer.parseInt(t.queryArgument(1).toString());
      t = t.queryArgument(2);
      index++;
    }
    return result;
  }

  private static int listLength(Term list) {
    int length = 0;
    Term t = list;
    while (!"nil".equals(t.toString())) {
      if (!"cons".equals(t.queryRoot().queryName())) {
        return -1;
      }
      length++;
      t = t.queryArgument(2);
    }
    return length;
  }

  private static void resetReductionCounters() {
    Reducer.totalParallelSteps = 0;
    Reducer.totalVirtualThreads.set(0);
  }

  private static double millis(long startNs, long endNs) {
    return (endNs - startNs) / 1_000_000.0;
  }

  private static void runCleanMemBenchmark(TRS trs, int[] input, int iteration) {
    int[] expected = sortedCopy(input);
    writeArrayToMemory(input);
    Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(1));

    resetReductionCounters();
    long start = System.nanoTime();
    Term normalForm = MemTRSUtil.reduceToNF(term, trs, MemTRSUtil.STRATEGY);
    long end = System.nanoTime();

    int[] actual = new int[input.length];
    for (int i = 0; i < input.length; i++) {
      actual[i] = MemReducer.GET(i + 2);
    }

    assertEquals("true", normalForm.toString());
    assertArrayEquals(expected, actual);
    System.out.printf(
      "mem,%d,%d,%.3f,%d,%d%n",
      input.length,
      iteration,
      millis(start, end),
      Reducer.totalParallelSteps,
      Reducer.totalVirtualThreads.get()
    );
  }

  private static void runCleanListBenchmark(TRS trs, int[] input, int iteration) {
    int[] expected = sortedCopy(input);
    Term term = trs.lookupSymbol("quicksort").apply(MemTRSUtil.arrayToListTerm(input, trs));

    resetReductionCounters();
    long start = System.nanoTime();
    Term normalForm = MemTRSUtil.reduceToNF(term, trs, MemTRSUtil.STRATEGY);
    long end = System.nanoTime();

    int[] actual = normalListTermToArray(normalForm);

    assertArrayEquals(expected, actual);
    System.out.printf(
      "list,%d,%d,%.3f,%d,%d%n",
      input.length,
      iteration,
      millis(start, end),
      Reducer.totalParallelSteps,
      Reducer.totalVirtualThreads.get()
    );
  }

  public static void qSortTimeBenchmarkClean() {
    TRS memTrs = MemTRSUtil.constructTRS(MEM_QSORT_TEST_TRS);
    TRS listTrs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs"
    );

    System.out.println("version,n,iteration,reduction_ms,parallel_steps,virtual_tasks");
    for (int size : QSORT_INPUT_SIZES) {
      for (int iteration = 1; iteration <= 3; iteration++) {
        int[] input = genBenchmarkArray(size, iteration);
        runCleanMemBenchmark(memTrs, input, iteration);
        runCleanListBenchmark(listTrs, input, iteration);
      }
    }
  }

  public static void qSortJFRBenchmarkMemClean(int inputSize) {
    TRS trs = MemTRSUtil.constructTRS(MEM_QSORT_TEST_TRS);
    int[] input = genBenchmarkArray(inputSize, 1);
    writeArrayToMemory(input);
    Term term = trs.lookupSymbol("test").apply(TheoryFactory.createValue(1));
    Term normalForm = MemTRSUtil.reduceToNF(term, trs, MemTRSUtil.STRATEGY);
    assertEquals("true", normalForm.toString());
  }

  public static void qSortJFRBenchmarkListClean(int inputSize) {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/quicksort_term.lctrs"
    );
    int[] input = genBenchmarkArray(inputSize, 1);
    Term term = trs.lookupSymbol("quicksort").apply(MemTRSUtil.arrayToListTerm(input, trs));
    Term normalForm = MemTRSUtil.reduceToNF(term, trs, MemTRSUtil.STRATEGY);
    assertEquals(inputSize, listLength(normalForm));
  }

  public static void main(String[] args) throws FileNotFoundException {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    if (args.length > 0) {
      switch (args[0]) {
        case "time-clean":
          qSortTimeBenchmarkClean();
          return;
        case "jfr-mem-clean":
          qSortJFRBenchmarkMemClean(Integer.parseInt(args[1]));
          return;
        case "jfr-list-clean":
          qSortJFRBenchmarkListClean(Integer.parseInt(args[1]));
          return;
        default:
          if ("list".equals(System.getenv("QSORT_PROFILE_MODE"))) {
            qSortJFRBenchmarkListClean(Integer.parseInt(args[0]));
          } else {
            qSortJFRBenchmarkMemClean(Integer.parseInt(args[0]));
          }
          return;
      }
    }

    // Warmup
    qSortTimeBenchmarkMem();

    System.setOut(new PrintStream(new FileOutputStream("final_qsort_time_mem.txt")));
    qSortTimeBenchmarkMem();

    System.setOut(new PrintStream(new FileOutputStream("final_qsort_time_list.txt")));
    qSortTimeBenchmarkList();

    System.setOut(new PrintStream(new FileOutputStream("final_qsort_parstep_mem.txt")));
    qSortTotalParallelStepsMem();

    System.setOut(new PrintStream(new FileOutputStream("final_qsort_parstep_list.txt")));
    qSortTotalParallelStepsList();

    // qSortBenchmark();
    // qSortBenchmark2();
    //qSortJFRBenchmarkMem(Integer.parseInt(args[0]));
    //qSortJFRBenchmarkList(Integer.parseInt(args[0]));
    //qSortTimeBenchmarkList();
    //qSortTotalParallelStepsMem();
    // qSortTotalParallelStepsList();
  }
}
