package repl.commands.imports;

import repl.commands.REPLCommand;

public class Exclude implements REPLCommand {
  private final int number;

  public Exclude(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.EXCLUDE;
  }
}
