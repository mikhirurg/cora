package repl.commands.imports;

import repl.commands.REPLCommand;

public class Include implements REPLCommand {
  private final String path;

  public Include(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.INCLUDE;
  }
}
