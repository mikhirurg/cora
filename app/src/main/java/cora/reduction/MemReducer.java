package cora.reduction;

import charlie.terms.IntegerValue;
import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import cora.config.Settings;

import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class MemReducer implements ReduceObject {

  //public static final Map<Integer, Integer> MEMORY = new ConcurrentHashMap<>();
  public static final AtomicIntegerArray MEMORY = new AtomicIntegerArray(Settings.getMemMaxSize());

  private static final Random random = new Random();

  static {
    resetMemory();
  }

  public static Integer GET(int addr) {
    if (addr < 0 || addr >= Settings.getMemMaxSize()) {
      return null;
    }
    return MEMORY.get(addr);
  }

  public static boolean SET(int addr, int val) {
    if (addr >= 0 && addr < Settings.getMemMaxSize()) {
      MEMORY.set(addr, val);
      return true;
    } else {
      return false;
    }
  }

  public static void resetMemory() {
    //MEMORY.clear();
    for (int i = 0; i < Settings.getMemMaxSize(); i++) {
      int randVal = random.nextInt();
      MEMORY.set(i, randVal);
    }
  }

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
    return checkIfSet(t) || checkIfGet(t);
  }

  @Override
  public Term apply(Term t) {
    if (checkIfGet(t)) {
      int agr1 = ((IntegerValue) t.queryArgument(1)).getInt();
      Integer value = GET(agr1);
      if (value != null) {
        return TheoryFactory.createValue(value);
      }
    } else if (checkIfSet(t)) {
      int agr1 = ((IntegerValue) t.queryArgument(1)).getInt();
      int agr2 = ((IntegerValue) t.queryArgument(2)).getInt();
      return TheoryFactory.createValue(SET(agr1, agr2));
    }
    return null;
  }

  public String toString() {
    return "mem";
  }
}
