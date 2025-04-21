package charlie.smt;

import cora.reduction.MemReducer;

public final class Get extends IntegerExpression {
  private final IntegerExpression _addr;

  Get(IntegerExpression addr) {
    _addr = addr;
    checkSimplified();
  }

  public IntegerExpression queryAddr() {
    return _addr;
  }

  public int evaluate(Valuation val) {
    return MemReducer.GET(_addr.evaluate(val));
  }

  @Override
  public void addToSmtString(StringBuilder builder) {
    builder.append("(MEM ");
    _addr.addToSmtString(builder);
    builder.append(")");
  }

  @Override
  public int compareTo(IntegerExpression other) {
    return switch (other) {
      case IValue _ -> 1;
      case Get get -> _addr.compareTo(get.queryAddr());
      default -> -1;
    };
  }

  @Override
  public IntegerExpression simplify() {
    if (_simplified) return this;
    IntegerExpression a = _addr.simplify();
    return new Get(a);
  }

  private void checkSimplified() {
    if (_addr instanceof IValue) return;
    _simplified = true;
  }
}
