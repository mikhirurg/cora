package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;

public class ArrayTest {

  @Test
  void createArrayTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: Int -> list
        test1 :: Int -> Bool -> list
        test2 :: Int -> list
        test(size) -> test1(size, SET(0, 1))
        test1(size, true) -> test2(createArray(size))
        test2(addr) -> arrayToList(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test(3)", trs);

    assertEquals(List.of(0, 0, 0), MemTRSUtil.termToList(term, trs));
  }

  @Test
  void getArrayTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: Int
        test1 :: Bool -> Int
        test2 :: Int -> Int
        test3 :: Int -> Int
        test4 :: Int -> Bool -> Int
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createArray(5))
        test2(addr) -> test3(createArray(4))
        test3(addr) -> test4(addr, setArr(addr, 1, 5))
        test4(addr, true) -> getArr(addr, 1)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(5, MemTRSUtil.termToInt(term, trs));
  }

  @Test
  void setArrayTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: list
        test1 :: Bool -> list
        test2 :: Int -> list
        test3 :: Int -> list
        test4 :: Int -> Bool -> Bool -> Bool -> Bool -> list
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createArray(5))
        test2(addr) -> test3(createArray(4))
        test3(addr) -> test4(addr, setArr(addr, 0, 1), setArr(addr, 1, 2), setArr(addr, 2, 3), setArr(addr, 3, 4))
        test4(addr, true, true, true, true) -> arrayToList(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(List.of(1, 2, 3, 4), MemTRSUtil.termToList(term, trs));
  }

  @Test
  void getArraySizeTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: Int
        test1 :: Bool -> Int
        test2 :: Int -> Int
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createArray(11))
        test2(addr) -> getArrSize(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(11, MemTRSUtil.termToInt(term, trs));
  }

  @Test
  void fillArrayTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: list
        test1 :: Bool -> list
        test2 :: Int -> list
        test3 :: Int -> Bool -> list
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createArray(5))
        test2(addr) -> test3(addr, fillArray(addr, 4))
        test3(addr, true) -> arrayToList(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(List.of(4, 4, 4, 4, 4), MemTRSUtil.termToList(term, trs));
  }

  @Test
  void swapArrayTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: list
        test1 :: Bool -> list
        test2 :: Int -> list
        test3 :: Int -> list
        test4 :: Int -> Bool -> Bool -> Bool -> Bool -> list
        test5 :: Int -> Bool -> list
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createArray(5))
        test2(addr) -> test3(createArray(4))
        test3(addr) -> test4(addr, setArr(addr, 0, 1), setArr(addr, 1, 2), setArr(addr, 2, 3), setArr(addr, 3, 4))
        test4(addr, true, true, true, true) -> test5(addr, swapArr(addr, 0, 3))
        test5(addr, true) -> arrayToList(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(List.of(4, 2, 3, 1), MemTRSUtil.termToList(term, trs));
  }

  @Test
  void arrayToListTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: Int -> list
        test1 :: Int -> Bool -> list
        test2 :: Int -> list
        test(size) -> test1(size, SET(0, 1))
        test1(size, true) -> test2(createArray(size))
        test2(addr) -> arrayToList(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test(3)", trs);

    assertEquals(List.of(0, 0, 0), MemTRSUtil.termToList(term, trs));
  }

  @Test
  void listToArrayTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
        test :: list -> list
        test1 :: list -> Bool -> list
        test2 :: Int -> list
        test(l) -> test1(l, SET(0, 1))
        test1(l, true) -> test2(listToArray(l))
        test2(addr) -> arrayToList(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test(cons(1, cons(2, cons(3, nil))))", trs);

    assertEquals(List.of(1, 2, 3), MemTRSUtil.termToList(term, trs));
  }
}
