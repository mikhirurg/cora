package repl.commands.declarations;

import repl.commands.REPLCommand;

public class ListDeclarations implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.LIST_DECLARATIONS;
  }
}
