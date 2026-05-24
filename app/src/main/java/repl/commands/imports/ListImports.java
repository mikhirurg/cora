package repl.commands.imports;

import repl.commands.REPLCommand;

public class ListImports implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.LIST_IMPORTS;
  }
}
