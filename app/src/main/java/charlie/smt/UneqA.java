package charlie.smt;

public final class UneqA extends Constraint {
  private ArrayExpression _left;
  private ArrayExpression _right;

  UneqA(ArrayExpression left, ArrayExpression right) {
    if (left.compareTo(right) >= 0) {
      _left = left;
      _right = right;
    } else {
      _left = right;
      _right = left;
    }

    if (_left.equals(_right)) {
      _simplified = false;
    } else {
      _simplified = (_left instanceof AVar || _right instanceof AVar);
    }
  }

  public ArrayExpression queryLeft() {
    return _left;
  }

  public ArrayExpression queryRight() {
    return _right;
  }

  @Override
  public boolean evaluate(Valuation val) {
    return !_left.evaluate(val).equals(_right.evaluate(val));
  }

  @Override
  public void addToSmtString(StringBuilder builder) {
    builder.append("(distinct ");
    _left.addToSmtString(builder);
    builder.append(" ");
    _right.addToSmtString(builder);
    builder.append(")");
  }

  // TODO: requires revision
  @Override
  public int compareTo(Constraint other) {
    return switch (other) {
      case UneqA una -> {
        int c = _left.compareTo(una._left);
        if (c == 0) c = _right.compareTo(una._right);
        yield c * 2;
      }
      case EqA eqa -> {
        int c = _left.compareTo(eqa.queryLeft());
        if (c == 0) c = _right.compareTo(eqa.queryRight());
        if (c == 0) yield 1;
        else yield c * 2;
      }
      default -> 1;
    };
  }

  @Override
  public Constraint negate() {
    return new EqA(_left, _right);
  }

  @Override
  public Constraint simplify() {
    if (_left.equals(_right)) return new Falsehood();
    return this;
  }

  // TODO: requires revision
  public int hashCode() {
    return 17 * (_left.hashCode() + 31 * _right.hashCode()) + 9;
  }
}
