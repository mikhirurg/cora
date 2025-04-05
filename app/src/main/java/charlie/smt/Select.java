package charlie.smt;

public final class Select extends IntegerExpression {

  private ArrayExpression _array;

  private IntegerExpression _index;

  Select(ArrayExpression array, IntegerExpression index) {
    _array = array;
    _index = index;

    // TODO: requires revision
    _simplified = (array instanceof AVar && index instanceof IValue);
  }

  public ArrayExpression queryArray() {
    return _array;
  }

  public IntegerExpression queryIndex() {
    return _index;
  }

  @Override
  public int evaluate(Valuation val) {
    return _array.evaluate(val).get(_index.evaluate());
  }

  @Override
  public void addToSmtString(StringBuilder builder) {
    builder.append("(select ");
    _array.addToSmtString(builder);
    builder.append(" ");
    _index.addToSmtString(builder);
    builder.append(")");
  }

  // TODO requires revision
  @Override
  public int compareTo(IntegerExpression other) {
    return switch (other) {
      case IValue _ -> 1;
      case Select _ -> 0;
      default -> -1;
    };
  }

  @Override
  public IntegerExpression simplify() {
    return new Select(_array, _index.simplify());
  }
}
