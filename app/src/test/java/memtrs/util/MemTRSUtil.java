package memtrs.util;

import charlie.reader.CoraInputReader;
import charlie.terms.Term;
import charlie.trs.TRS;
import cora.reduction.Reducer;

import java.util.ArrayList;
import java.util.List;

public class MemTRSUtil {

  public static Term reduceToNF(Term start, TRS trs) {
    Reducer reducer = new Reducer(trs);
    Term s = start;
    Term oldS = null;
    do {
      oldS = s;
      s = reducer.reduce(s);
    } while (s != null);
    return oldS;
  }

  public static List<Integer> termToList(Term listTerm, TRS trs) {
    List<Integer> list = new ArrayList<>();

    for (Term t = reduceToNF(listTerm, trs);
         !"nil".equals(t.toString()) && "cons".equals(t.queryRoot().queryName());
         t = t.queryArgument(2)) {
      list.add(Integer.parseInt(t.queryArgument(1).toString()));
    }

    return list;
  }

  public static void main(String[] args) {
    TRS trs = CoraInputReader.readTrsFromString(
      """
      nil :: list
      cons :: Int -> list -> list
      x::list
      """
    );

    Term listTerm = CoraInputReader.readTerm(
      "cons(1, cons(2, nil))",
      trs
    );
    List<Integer> list = termToList(listTerm, trs);
  }
}
