package repl.commands.declarations;

import repl.commands.REPLCommand;

public class ClearDeclarations implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.CLEAR_DECLARATIONS;
  }
}
