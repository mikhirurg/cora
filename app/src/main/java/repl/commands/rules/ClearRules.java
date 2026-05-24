package repl.commands.rules;

import repl.commands.REPLCommand;

public class ClearRules implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.CLEAR_RULES;
  }
}
