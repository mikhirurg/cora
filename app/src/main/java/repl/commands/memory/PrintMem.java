package repl.commands.memory;

import repl.commands.REPLCommand;

public class PrintMem implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.PRINT_MEM;
  }
}
