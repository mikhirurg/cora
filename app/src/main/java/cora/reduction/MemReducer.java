package cora.reduction;

import charlie.terms.IntegerValue;
import charlie.terms.Term;
import charlie.terms.TheoryFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

  private static final Map<Integer, Integer> MEMORY = new HashMap<>();
  private static final Random random = new Random();

  private boolean checkIfSet(Term t) {
    return "SET".equals(t.queryRoot().queryName()) &&
      t.numberArguments() == 2 &&
      t.queryArgument(1).isValue() &&
      t.queryArgument(2).isValue() &&
      (t.queryArgument(1) instanceof IntegerValue) &&
      (t.queryArgument(2) instanceof IntegerValue);
  }

  private boolean checkIfGet(Term t) {
    return "GET".equals(t.queryRoot().queryName()) &&
      t.numberArguments() == 1 &&
      t.queryArgument(1).isValue() &&
      (t.queryArgument(1) instanceof IntegerValue);
  }

  @Override
  public boolean applicable(Term t) {
    return checkIfRead(t) || checkIfWrite(t) || checkIfSet(t) || checkIfGet(t);
  }

  @Override
  public Term apply(Term t) {
    if (checkIfRead(t)) {
      return TheoryFactory.createValue(MEMORY_CELL);
    } else if (checkIfWrite(t)) {
      MEMORY_CELL = ((IntegerValue) t.queryArgument(1)).getInt();
      return TheoryFactory.createValue(true);
    } else if (checkIfGet(t)) {
      int agr1 = ((IntegerValue) t.queryArgument(1)).getInt();
      if (MEMORY.containsKey(agr1)) {
        return TheoryFactory.createValue(MEMORY.get(agr1));
      } else {
        int randVal = random.nextInt();
        MEMORY.put(agr1, randVal);
        return TheoryFactory.createValue(randVal);
      }
    } else if (checkIfSet(t)) {
      int agr1 = ((IntegerValue) t.queryArgument(1)).getInt();
      int agr2 = ((IntegerValue) t.queryArgument(2)).getInt();
      if (agr1 >= 0) {
        MEMORY.put(agr1, agr2);
        return TheoryFactory.createValue(true);
      } else {
        return TheoryFactory.createValue(false);
      }
    }
    return null;
  }

  public String toString() {
    return "mem";
  }
}
