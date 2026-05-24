package cora.reduction;

import charlie.terms.BooleanValue;
import charlie.terms.IntegerValue;
import charlie.terms.Term;
import charlie.terms.TheoryFactory;
import cora.config.Settings;

import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class MemReducer implements ReduceObject {

  public static AtomicIntegerArray MEMORY = new AtomicIntegerArray(Settings.getMemMaxSize());

  private static final Random random = new Random();

  static {
    resetMemory();
  }

  private boolean checkIfGET(Term t) {
    return "GET".equals(t.queryRoot().queryName()) &&
      t.numberArguments() == 1 &&
      t.queryArgument(1).isValue() &&
      (t.queryArgument(1) instanceof IntegerValue);
  }

  private boolean checkIfSET(Term t) {
    return "SET".equals(t.queryRoot().queryName()) &&
      t.numberArguments() == 2 &&
      t.queryArgument(1).isValue() &&
      t.queryArgument(2).isValue() &&
      (t.queryArgument(1) instanceof IntegerValue) &&
      (t.queryArgument(2) instanceof IntegerValue);
  }

  private boolean checkIfCAS(Term t) {
    return "CAS".equals(t.queryRoot().queryName()) &&
      t.numberArguments() == 3 &&
      t.queryArgument(1).isValue() &&
      t.queryArgument(2).isValue() &&
      t.queryArgument(3).isValue() &&
      (t.queryArgument(1) instanceof IntegerValue) &&
      (t.queryArgument(2) instanceof IntegerValue) &&
      (t.queryArgument(3) instanceof IntegerValue);
  }

  public static Integer GET(int addr) {
    if (addr < 0 || addr >= Settings.getMemMaxSize()) {
      return null;
    }
    return MEMORY.get(addr);
  }

  public static synchronized boolean SET(int addr, int val) {
    if (addr >= 0 && addr < Settings.getMemMaxSize()) {
      MEMORY.set(addr, val);
      return true;
    } else {
      return false;
    }
  }

  public static synchronized boolean CAS(int addr, int expected, int update) {
    if (addr >= 0 && addr < Settings.getMemMaxSize()) {
      return MEMORY.compareAndSet(addr, expected, update);
    } else {
      return false;
    }
  }

  public static void resetMemory() {
    for (int i = 0; i < Settings.getMemMaxSize(); i++) {
      int randVal = random.nextInt();
      MEMORY.set(i, randVal);
    }
    MEMORY.set(0, 2);
    MEMORY.set(1, 0);
  }

  public static void resizeMemory(int size) {
    MEMORY = new AtomicIntegerArray(size);
    Settings.setMemMaxSize(size);
    resetMemory();
  }

  @Override
  public boolean applicable(Term t) {
    return checkIfGET(t) || checkIfSET(t) || checkIfCAS(t);
  }

  @Override
  public Term apply(Term t) {
    if (checkIfGET(t)) {
      int arg1 = ((IntegerValue) t.queryArgument(1)).getInt();
      return TheoryFactory.createValue(GET(arg1));
    } else if (checkIfSET(t)) {
      int arg1 = ((IntegerValue) t.queryArgument(1)).getInt();
      int arg2 = ((IntegerValue) t.queryArgument(2)).getInt();
      return TheoryFactory.createValue(SET(arg1, arg2));
    } else if (checkIfCAS(t)) {
      int arg1 = ((IntegerValue) t.queryArgument(1)).getInt();
      int arg2 = ((IntegerValue) t.queryArgument(2)).getInt();
      int arg3 = ((IntegerValue) t.queryArgument(3)).getInt();
      return TheoryFactory.createValue(CAS(arg1, arg2, arg3));
    }
    return null;
  }

  @Override
  public String toString() {
    return "mem";
  }
}
