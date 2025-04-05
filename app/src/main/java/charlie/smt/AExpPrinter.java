package charlie.smt;

public class AExpPrinter {

  private final IExpPrinter iExpPrinter;

  public AExpPrinter() {
    iExpPrinter = new IExpPrinter();
  }

  public final String print(ArrayExpression e) {
    StringBuilder builder = new StringBuilder();
    print(e, builder);
    return builder.toString();
  }

  public void print(ArrayExpression e, StringBuilder builder) {
    switch (e) {
      case AVar x : printVar(x, builder); break;
      case Store st : printStore(st, builder); break;
    }
  }

  protected final void printVar(AVar x, StringBuilder builder) {
    builder.append(x.queryName());
  }

  protected final void printStore(Store store, StringBuilder builder) {
    print(store.queryArray());
    builder.append("{")
            .append(iExpPrinter.print(store.queryIndex()))
            .append(" -> ")
            .append(iExpPrinter.print(store.queryValue()))
            .append("}");
  }
}
