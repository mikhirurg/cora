package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import cora.reduction.MemReducer;
import memtrs.util.MemTRSUtil;
import org.junit.jupiter.api.Test;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtomicCounterTest {

  @Test
  void atomicCounterTest() {
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/array.lctrs\n\n" +
        """
          s :: Bool -> Bool -> Unit
          inc :: Int -> Bool
          inc1 :: Int -> Bool -> Bool
          inc2 :: Int -> Bool -> Bool
          inc(addr) -> inc1(addr, CAS(addr, 0, 1))
          inc1(addr, true) -> inc2(addr, SET(addr + 1, GET(addr + 1) + 1))
          inc1(addr, false) -> inc(addr)
          inc2(addr, true) -> SET(addr, 0)
          """
    );

    Term term = MemTRSUtil.constructTerm("s(inc(0), inc(0))", trs);

    MemReducer.SET(0, 0);
    MemReducer.SET(1, 0);
    MemTRSUtil.reduceToNF(term, trs, MemTRSUtil.STRATEGY);

    assertEquals(2, MemReducer.GET(1));
  }
}
