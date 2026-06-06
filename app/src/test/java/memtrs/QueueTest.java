package memtrs;

import charlie.terms.Term;
import charlie.trs.TRS;
import cora.config.Settings;
import cora.reduction.MemReducer;
import memtrs.util.MemTRSUtil;

import static memtrs.util.MemTRSUtil.MEMTRS_STDLIB_PATH;

public class QueueTest {
  public static void main(String[] args) {
    Settings.setReductionMode(Settings.ReductionMode.Parallel);

    MemReducer.SET(0, 2);
    MemReducer.SET(1, 0);
    TRS trs = MemTRSUtil.constructTRS(
      "#include " + MEMTRS_STDLIB_PATH + "mem_ds/queue.lctrs\n\n" +
        """
          test :: Int -> Int
          test1 :: Int -> Int
          test2 :: Int -> Bool -> Bool -> Bool -> Int
          test3 :: Int -> Int -> Int -> Int -> Int
          id :: Int -> Int
          id(x) -> x
          test(cap) -> test1(createQueue(cap))
          test1(addr) -> test2(addr, push(addr, id(10)), push(addr, 20), push(addr, 30))
          test2(addr, true, true, true) -> test3(pop(addr), pop(addr), pop(addr), pop(addr))
          """
    );
    Term term = MemTRSUtil.constructTerm("test(10)", trs);
    MemTRSUtil.reduceToNF(term, trs, Settings.Strategy.CallByValue);
    System.out.println(MemReducer.MEMORY);
  }
}
