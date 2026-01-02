package charlie.smt;

import cora.reduction.MemReducer;

public final class Store extends Constraint {
  private final IntegerExpression _addr;
  private final IntegerExpression _val;

  Store(IntegerExpression addr, IntegerExpression val) {
    _addr = addr;
    _val = val;
    checkSimplified();
  }

  public IntegerExpression queryAddr() {
    return _addr;
  }

  public IntegerExpression queryVal() {
    return _val;
  }

  public boolean evaluate(Valuation val) {
    return MemReducer.SET(_addr.evaluate(val), _val.evaluate(val));
  }

  @Override
  public void addToSmtString(StringBuilder builder) {
    builder.append("(store ");
    _addr.addToSmtString(builder);
    builder.append(" ");
    _val.addToSmtString(builder);
    builder.append(")");
  }

  public int compareTo(Constraint other) {
    return switch (other) {
      case Store store -> {
        int c = _addr.compareTo(store._addr);
        if (c == 0) c = _val.compareTo(store._val);
        yield c * 2;
      }
      default -> 1;
    };
  }

  @Override
  public Constraint negate() {
    return null;
  }

  public Constraint simplify() {
    if (_simplified) return this;
    IntegerExpression a = _addr.simplify();
    IntegerExpression v = _val.simplify();
    return new Store(a, v);
  }

  @Override
  public boolean containsMemoryUpdateOperation() {
    return true;
  }

  private void checkSimplified() {
    if (_addr instanceof IValue && _val instanceof IValue) return;
    _simplified = true;
  }
}
