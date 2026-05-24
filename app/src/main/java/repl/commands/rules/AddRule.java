package repl.commands.rules;

import charlie.trs.Rule;
import repl.commands.REPLCommand;

public class AddRule implements REPLCommand {
  private final Rule rule;

  public AddRule(Rule rule) {
    this.rule = rule;
  }

  public Rule getRule() {
    return rule;
  }

  @Override
  public REPLSymbol replSymbol() {
    return REPLSymbol.RULE;
  }
}
