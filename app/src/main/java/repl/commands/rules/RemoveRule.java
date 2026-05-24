package repl.commands.rules;

import repl.commands.REPLCommand;

public class RemoveRule implements REPLCommand {

  private final int number;

  public RemoveRule(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.REMOVE_RULE;
  }
}
