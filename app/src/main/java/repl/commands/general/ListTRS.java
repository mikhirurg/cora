package repl.commands.general;

import repl.commands.REPLCommand;

public class ListTRS implements REPLCommand {
  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.TRS;
  }
}
