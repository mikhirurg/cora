package repl.commands.memory;

import repl.commands.REPLCommand;

public class ResizeMem implements REPLCommand {
  private final int size;

  public ResizeMem(int size) {
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.RESIZE_MEM;
  }
}
