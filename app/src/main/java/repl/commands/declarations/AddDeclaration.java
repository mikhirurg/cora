package repl.commands.declarations;

import charlie.terms.FunctionSymbol;
import repl.commands.REPLCommand;

public class AddDeclaration implements REPLCommand {
  private final FunctionSymbol declaration;

  public AddDeclaration(FunctionSymbol declaration) {
    this.declaration = declaration;
  }

  public FunctionSymbol getDeclaration() {
    return declaration;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.DECLARE;
  }
}
