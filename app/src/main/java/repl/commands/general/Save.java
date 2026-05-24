package repl.commands.general;

import repl.commands.REPLCommand;

public class Save implements REPLCommand {

  private final String path;

  public Save(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.SAVE;
  }
}
