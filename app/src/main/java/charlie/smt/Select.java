package charlie.smt;

import cora.reduction.MemReducer;

public final class Select extends IntegerExpression {
  private final IntegerExpression _addr;

  Select(IntegerExpression addr) {
    _addr = addr;
    checkSimplified();
  }

  public IntegerExpression queryAddr() {
    return _addr;
  }

  public int evaluate(Valuation val) {
    //return MemReducer.GET(_addr.evaluate(val));
    Integer result = null;
    if (val != null) {
      result = val.queryArrayAssignment(_addr.evaluate(val));
    }
    if (result == null) {
      result = MemReducer.GET(_addr.evaluate(val));
    }
    return result;
  }

  @Override
  public void addToSmtString(StringBuilder builder) {
    builder.append("(select MEM ");
    _addr.addToSmtString(builder);
    builder.append(")");
  }

  @Override
  public int compareTo(IntegerExpression other) {
    return switch (other) {
      case IValue _ -> 1;
      case Select select -> _addr.compareTo(select.queryAddr());
      default -> -1;
    };
  }

  @Override
  public IntegerExpression simplify() {
    if (_simplified) return this;
    IntegerExpression a = _addr.simplify();
    return new Select(a);
  }

  @Override
  public boolean containsMemoryUpdateOperation() {
    return false;
  }

  private void checkSimplified() {
    if (_addr instanceof IValue) return;
    _simplified = true;
  }
}
