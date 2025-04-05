package charlie.smt;

import charlie.exceptions.SmtEvaluationException;

import java.util.Map;

public final class AVar extends ArrayExpression {

  private int _index;

  private String _name;

  AVar(int i) {
    _index = i;
    _name = "a" + _index;
    _simplified = true;
  }

  AVar(int i, String name) {
    _index = i;
    _name = "[" + name + "]";
    _simplified = true;
  }

  public int queryIndex() {
    return _index;
  }

  public String queryName() {
    return _name;
  }

  @Override
  public Map<Integer, Integer> evaluate(Valuation val) {
    if (val == null) throw new SmtEvaluationException("a" + _index + " (" + _name + ")");
    return val.queryArrayAssignment(_index);
  }

  @Override
  public void addToSmtString(StringBuilder builder) {
    builder.append("a" + _index);
  }

  // TODO: requires revision
  @Override
  public int compareTo(ArrayExpression o) {
    return switch (o) {
      case AVar x -> _index - x._index;
      case Store _ -> -1;
    };
  }

  @Override
  public ArrayExpression simplify() {
    return this;
  }

  // TODO: how is this calculated?
  public int hashCode() {
    return 17 * _index;
  }
}
