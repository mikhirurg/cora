package repl.commands.general;

import repl.commands.REPLCommand;

public class Exit implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.EXIT;
  }
}
