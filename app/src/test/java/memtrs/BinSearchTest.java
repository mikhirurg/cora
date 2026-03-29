package memtrs;

import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.trs.TRS;
import cora.reduction.MemReducer;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinSearchTest {

  @Test
  void binSearchTest() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/binsearch.lctrs\n\n" +
        """
          test :: list -> Int -> Int
          test1 :: list -> Int -> Bool -> Bool -> Int
          test2 :: list -> Int -> Int -> Int
          
          test(l, x) -> test1(l, x, SET(0, 2), SET(1, 0))
          test1(l, x, true, true) -> test2(l, x, listToArray(l))
          test2(l, x, addr) -> binsearch(addr, x)
          """
    );

    int size = 100;

    int[] arr = MemTRSUtil.genRandomUniqueArray(size, -100, 100);
    Arrays.sort(arr);

    int index = MemTRSUtil.random.nextInt(0, size);

    Term term = trs.lookupSymbol("test")
      .apply(MemTRSUtil.arrayToListTerm(arr, trs))
      .apply(TheoryFactory.createValue(arr[index]));

    Integer result = MemTRSUtil.termToInt(term, trs);

    assertEquals(index, result);
  }

  @Test
  void binSearchTermTest() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/binsearch_term.lctrs\n\n" +
        """
          test :: list -> Int -> Int
          
          test(l, x) -> binsearch_term(l, x)
          """
    );

    int size = 10;

    int[] arr = MemTRSUtil.genRandomUniqueArray(size, -100, 100);
    Arrays.sort(arr);

    int index = MemTRSUtil.random.nextInt(0, size);

    Term term = trs.lookupSymbol("test")
      .apply(MemTRSUtil.arrayToListTerm(arr, trs))
      .apply(TheoryFactory.createValue(arr[index]));

    Integer result = MemTRSUtil.termToInt(term, trs);

    assertEquals(index, result);
  }

  public static void binSearchBenchmark() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/binsearch.lctrs\n\n" +
        """
          test :: list -> Int -> Int
          test1 :: list -> Int -> Bool -> Int
          test2 :: list -> Int -> Int -> Int
          
          test(l, x) -> test1(l, x, SET(0, 1))
          test1(l, x, true) -> test2(l, x, listToArray(l))
          test2(l, x, addr) -> binsearch(addr, x)
          """
    );

    //for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
    for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        MemReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomUniqueArray(size, -1000, 1000);
        Arrays.sort(arr);

        int index = MemTRSUtil.random.nextInt(0, size);

        Term term = trs.lookupSymbol("test")
          .apply(MemTRSUtil.arrayToListTerm(arr, trs))
          .apply(TheoryFactory.createValue(arr[index]));

        long start = System.currentTimeMillis();
        Integer result = MemTRSUtil.termToInt(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start));

        assertEquals(index, result);
      }
    }
  }

  public static void binSearchBenchmarkOptimal() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "algorithms/binsearch.lctrs\n\n" +
        """
          test :: Int -> Int -> Int
          
          test(addr, x) -> binsearch(addr, x)
          """
    );

    //for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
    for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        MemReducer.resetMemory();

        int size = i;

        int[] arr = MemTRSUtil.genRandomUniqueArray(size, -1000, 1000);
        Arrays.sort(arr);

        MemReducer.SET(0, size + 2);
        MemReducer.SET(1, size);
        for (int k = 2; k < size + 2; k++) {
          MemReducer.SET(k, arr[k - 2]);
        }

        int index = MemTRSUtil.random.nextInt(0, size);

        Term term = trs.lookupSymbol("test")
          .apply(TheoryFactory.createValue(1))
          .apply(TheoryFactory.createValue(arr[index]));

        long start = System.currentTimeMillis();
        Integer result = MemTRSUtil.termToInt(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start));

        assertEquals(index, result);
      }
    }
  }

  public static void binSearchTermBenchmark() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "term_algorithms/binsearch_term.lctrs\n\n" +
        """
          test :: list -> Int -> Int
          
          test(l, x) -> binsearch_term(l, x)
          """
    );

    //for (int i : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
    for (int i = 1; i < 2000; i += 10) {
      for (int j = 0; j < 3; j++) {

        int size = i;

        int[] arr = MemTRSUtil.genRandomUniqueArray(size, -1000, 1000);
        Arrays.sort(arr);

        int index = MemTRSUtil.random.nextInt(0, size);

        Term term = trs.lookupSymbol("test")
          .apply(MemTRSUtil.arrayToListTerm(arr, trs))
          .apply(TheoryFactory.createValue(arr[index]));

        long start = System.currentTimeMillis();
        Integer result = MemTRSUtil.termToInt(term, trs);
        long end = System.currentTimeMillis();
        System.out.println("n: " + i + ", i: " + (j + 1) + ", delta: " + (end - start));

        assertEquals(index, result);
      }
    }
  }

  public static void main(String[] args) {
    //binSearchBenchmarkOptimal();
    binSearchBenchmark();
    // binSearchTermBenchmark();
  }
}
