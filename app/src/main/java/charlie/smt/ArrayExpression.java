package charlie.smt;

import java.util.Map;

public sealed abstract class ArrayExpression implements Comparable<ArrayExpression>
  permits AVar, Store {

  protected boolean _simplified;

  protected ArrayExpression() {
    _simplified = false;
  }

  public abstract Map<Integer, Integer> evaluate(Valuation val);

  public abstract void addToSmtString(StringBuilder builder);

  public abstract int compareTo(ArrayExpression other);

  public abstract ArrayExpression simplify();

  public final boolean isSimplified() {
    return _simplified;
  }

  public ArrayExpression store(int index, int value) {
    return new Store(this, new IValue(index), new IValue(value));
  }

  public final String toString() {
    AExpPrinter printer = new AExpPrinter();
    return printer.print(this);
  }

  public final String toSmtString() {
    StringBuilder builder = new StringBuilder();
    addToSmtString(builder);
    return builder.toString();
  }

  public final boolean equals(Object other) {
    return (other instanceof ArrayExpression) && compareTo((ArrayExpression) other) == 0;
  }

}
