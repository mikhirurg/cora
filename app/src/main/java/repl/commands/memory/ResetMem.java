package repl.commands.memory;

import repl.commands.REPLCommand;

public class ResetMem implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.RESET_MEM;
  }
}
