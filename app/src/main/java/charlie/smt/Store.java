package charlie.smt;

import java.util.Map;

public final class Store extends ArrayExpression {

  private ArrayExpression _array;

  private IntegerExpression _index;

  private IntegerExpression _value;

  Store(ArrayExpression array, IntegerExpression index, IntegerExpression value) {
    _array = array;
    _index = index;
    _value = value;
  }

  public ArrayExpression queryArray() {
    return _array;
  }

  public IntegerExpression queryIndex() {
    return _index;
  }

  public IntegerExpression queryValue() {
    return _value;
  }

  @Override
  public Map<Integer, Integer> evaluate(Valuation val) {
    return Map.of();
  }

  @Override
  public void addToSmtString(StringBuilder builder) {
    builder.append("(store ");
    _array.addToSmtString(builder);
    builder.append(" ");
    _index.addToSmtString(builder);
    builder.append(" ");
    _value.addToSmtString(builder);
    builder.append(")");
  }

  @Override
  public int compareTo(ArrayExpression other) {
    return 0;
  }

  @Override
  public ArrayExpression simplify() {
    return new Store(_array, _index.simplify(), _value.simplify());
  }
}
