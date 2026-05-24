package repl.commands.rules;

import repl.commands.REPLCommand;

public class ListRules implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.LIST_RULES;
  }
}
