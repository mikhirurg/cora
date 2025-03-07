package cora.reduction;

import charlie.terms.IntegerValue;
import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import charlie.types.TypeFactory;

public class MemReducer implements ReduceObject {

  public static int MEMORY_CELL = 0;

  private boolean checkIfRead(Term t) {
    return "READ".equals(t.queryRoot().queryName()) && t.numberArguments() == 0;
  }

  private boolean checkIfWrite(Term t) {
    return "WRITE".equals(t.queryRoot().queryName()) &&
      t.numberArguments() == 1 &&
      t.queryArgument(1).isValue() &&
      (t.queryArgument(1) instanceof IntegerValue);
  }

  @Override
  public boolean applicable(Term t) {
    return checkIfRead(t) || checkIfWrite(t);
  }

  @Override
  public Term apply(Term t) {
    if (checkIfRead(t)) {
      return TheoryFactory.createValue(MEMORY_CELL);
    } else if (checkIfWrite(t)) {
      MEMORY_CELL = ((IntegerValue) t.queryArgument(1)).getInt();
      return TheoryFactory.createValue(true);
    }
    return null;
  }

  public String toString() {
    return "mem";
  }
}
