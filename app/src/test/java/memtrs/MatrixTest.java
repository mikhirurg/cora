package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import cora.reduction.MemReducer;
import memtrs.util.MemTRSUtil;
import memtrs.util.matrix.Matrix;
import org.junit.jupiter.api.Test;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixTest {

  @Test
  void createMatrixTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: matrix
        test1 :: Bool -> matrix
        test2 :: Int -> matrix
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createMatrix(3, 4))
        test2(addr) -> matrixToTerm(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(
      new Matrix(new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}}),
      MemTRSUtil.termToMatrix(term, trs)
    );
  }

  @Test
  void getMatrixWidthTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: Int
        test1 :: Bool -> Int
        test2 :: Int -> Int
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createMatrix(3, 4))
        test2(addr) -> getMatrixWidth(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(
      3,
      MemTRSUtil.termToInt(term, trs)
    );
  }

  @Test
  void getMatrixHeightTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: Int
        test1 :: Bool -> Int
        test2 :: Int -> Int
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createMatrix(3, 4))
        test2(addr) -> getMatrixHeight(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(
      4,
      MemTRSUtil.termToInt(term, trs)
    );
  }

  @Test
  void getMatrixTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: Int
        test1 :: Bool -> Int
        test2 :: Int -> Int
        test3 :: Int -> Int
        test4 :: Int -> Int -> Int
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createMatrix(3, 4))
        test2(addr) -> test3(createMatrix(2, 2))
        test3(addr) -> test4(addr, createMatrix(5, 5))
        test4(oldAddr, addr) -> getMatrix(oldAddr, 0, 0)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(
      0,
      MemTRSUtil.termToInt(term, trs)
    );
  }

  @Test
  void setMatrixTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: matrix
        test1 :: Bool -> matrix
        test2 :: Int -> matrix
        test3 :: Int -> Bool -> matrix
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createMatrix(3, 4))
        test2(addr) -> test3(addr, setMatrix(addr, 2, 3, 5))
        test3(addr, true) -> matrixToTerm(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(
      new Matrix(new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 5}}),
      MemTRSUtil.termToMatrix(term, trs)
    );
  }

  @Test
  void fillMatrixTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: matrix
        test1 :: Bool -> matrix
        test2 :: Int -> matrix
        test3 :: Int -> Bool -> matrix
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createMatrix(3, 4))
        test2(addr) -> test3(addr, fillMatrix(addr, 5))
        test3(addr, true) -> matrixToTerm(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(
      new Matrix(new int[][]{{5, 5, 5}, {5, 5, 5}, {5, 5, 5}, {5, 5, 5}}),
      MemTRSUtil.termToMatrix(term, trs)
    );
  }

  @Test
  void matrixToTermTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: matrix
        test1 :: Bool -> matrix
        test2 :: Int -> matrix
        test3 :: Int -> Bool -> matrix
        
        test -> test1(SET(0, 1))
        test1(true) -> test2(createMatrix(3, 3))
        test2(addr) -> test3(addr, fillMatrix(addr, 1))
        test3(addr, true) -> matrixToTerm(addr)
        """
    );

    Term term = MemTRSUtil.constructTerm("test", trs);

    assertEquals(
      new Matrix(new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}),
      MemTRSUtil.termToMatrix(term, trs)
    );
  }

  @Test
  void graphToMatrixTest1() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/matrix.lctrs\n\n" +
        """
        test :: graph -> matrix
        
        test(g) -> matrixToTerm(graphToMatrix(g, 0))
        """
    );

    Term term = MemTRSUtil.constructTerm(
"""
        test(consG(                    /* 1 */
                   cons(1, cons(2, nil)),
                 consG(nil,                /* 2 */
                 consG(                    /* 3 */
                   cons(3, cons(4, nil)),
                 consG(nil,                /* 4 */
                 consG(                    /* 5 */
                   cons(1, nil),
                 nilG))))))
        """,
      trs);

    MemReducer.resetMemory();
    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);

    assertEquals(
      new Matrix(new int[][]{ {0, 1, 1, 0, 0},
                              {0, 0, 0, 0, 0},
                              {0, 0, 0, 1, 1},
                              {0, 0, 0, 0, 0},
                              {0, 1, 0, 0, 0}
      }),
      MemTRSUtil.termToMatrix(term, trs)
    );

    System.out.println(MemReducer.MEMORY.toString());
  }
}
