package repl.commands.debug;

import repl.commands.REPLCommand;

public class ShowMemory implements REPLCommand {
  private final boolean isEnabled;

  public ShowMemory(boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.SHOW_MEMORY;
  }
}
