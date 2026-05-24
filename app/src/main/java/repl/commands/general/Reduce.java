package repl.commands.general;

import charlie.terms.Term;
import repl.commands.REPLCommand;

public class Reduce implements REPLCommand {
  private final Term term;

  public Reduce(Term term) {
    this.term = term;
  }

  public Term getTerm() {
    return term;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.REDUCE;
  }
}
