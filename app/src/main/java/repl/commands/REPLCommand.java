package repl.commands;

import charlie.parser.CoraParser;
import charlie.parser.Parser;
import charlie.reader.CoraInputReader;
import charlie.terms.TermFactory;
import cora.config.Settings;
import repl.MemTRSREPL;
import repl.commands.debug.ShowMemory;
import repl.commands.debug.ShowReductions;
import repl.commands.declarations.AddDeclaration;
import repl.commands.declarations.ClearDeclarations;
import repl.commands.declarations.ListDeclarations;
import repl.commands.declarations.RemoveDeclaration;
import repl.commands.general.Exit;
import repl.commands.general.Help;
import repl.commands.general.Reduce;
import repl.commands.general.Save;
import repl.commands.general.ListTRS;
import repl.commands.imports.Exclude;
import repl.commands.imports.Include;
import repl.commands.imports.ListImports;
import repl.commands.memory.PrintMem;
import repl.commands.memory.ResetMem;
import repl.commands.memory.ResizeMem;
import repl.commands.rewriting_settings.ReductionMode;
import repl.commands.rewriting_settings.Strategy;
import repl.commands.rules.AddRule;
import repl.commands.rules.ClearRules;
import repl.commands.rules.ListRules;
import repl.commands.rules.RemoveRule;

public interface REPLCommand {

  enum REPLSymbol {
    // General:
    REDUCE("reduce", "\"reduce <term>\": Reduce <term> to the normal form using all the available" +
      " rules."),
    EXIT("exit", "\"exit\": Exit the MemTRS REPL."),
    TRS("trs", "\"trs\": Prints current TRS."),
    SAVE("save", "\"save <path>\": Saves current TRS to the <path>."),
    HELP("help", "\"help\": Prints information about available commands."),

    // Debug:,
    SHOW_REDUCTIONS("show_reductions", "\"show_reductions <status>\": Enables or disables the " +
      "intermediate reductions trace. Options: [<status>: on/off]."),
    SHOW_MEMORY("show_memory", "\"show_memory <status>\": Enables or disables the global memory " +
      "trace during reduction. Options: [<status>: on/off]."),

    // Memory:,
    RESET_MEM("reset_mem", "\"reset_mem\": Reset global memory."),
    PRINT_MEM("print_mem", "\"print_mem\": Prints the global memory."),
    RESIZE_MEM("resize_mem", "\"resize_mem <new_size>\": Resizes the global memory."),

    // Rewriting settings:,
    REDUCTION_MODE("reduction_mode", "\"reduction_mode <mode>\": Updates the reduction mode for " +
      "the term rewriting engine. Options: [<mode>: first/random/parallel]."),
    STRATEGY("strategy", "\"strategy <strategy>\": Updates the reduction strategy for the term " +
      "rewriting engine. Options: [<strategy>: full/innermost/cbv]."),

    // Imports:
    EXCLUDE("exclude", "\"exclude <number>\": Exclude a <number>'th script located at <path>."),
    INCLUDE("include", "\"include <path>\": Import a script located at <path>."),
    LIST_IMPORTS("list_imports", "\"list_imports\": Prints the current list of imported scripts."),

    // Declarations:
    DECLARE("declare", "\"declare <term :: type>\": Adds an extra declaration " +
      "<term :: type> to the current list of temporary declarations."),
    REMOVE_DECLARATION("remove_declaration", "\"remove_declaration <number>\": Removes the " +
      "<number>'th declaration from the list of temporary declarations."),
    LIST_DECLARATIONS("list_declarations", "\"list_declarations\": Prints the current list of " +
      "temporary declarations."),
    CLEAR_DECLARATIONS("clear_declarations", "\"clear_declarations\": Clear the list of temporary" +
      " declarations"),

    // Rules:
    RULE("rule", "\"rule <l -> r [b]>\": Adds an extra rewriting rule <l -> r [b]> to the current" +
      " set of rules."),
    REMOVE_RULE("remove_rule", "\"remove_rule <number>\": Removes the <number>'th rule from the " +
      "list of temporary rewrite rules."),
    CLEAR_RULES("clear_rules", "\"clear_rules\": Clear the list of temporary rules"),
    LIST_RULES("list_rules", "\"list_rules\": Prints the current list of temporary rewrite " +
      "rules.");

    private final String symbol;
    private final String description;

    REPLSymbol(String symbol, String description) {
      this.symbol = symbol;
      this.description = description;
    }

    public String getSymbol() {
      return symbol;
    }

    public String getDescription() {
      return description;
    }
  }

  static REPLCommand of(String input) {

    int spaceBegin = input.indexOf(' ');
    if (spaceBegin == -1) {
      spaceBegin = input.length();
    }

    String command = input.substring(0, spaceBegin).trim();
    String argument = input.substring(spaceBegin).trim();

    if (argument.isEmpty()) {
      argument = null;
    }

    // Debug:
    if (REPLSymbol.SHOW_MEMORY.symbol.equals(command)) {
      return (argument != null ? switch (argument) {
        case "on" -> new ShowMemory(true);
        case "off" -> new ShowMemory(false);
        default -> null;
      } : null);
    } else if (REPLSymbol.SHOW_REDUCTIONS.symbol.equals(command)) {
      return argument != null ?
        switch (argument) {
          case "on" -> new ShowReductions(true);
          case "off" -> new ShowReductions(false);
          default -> null;
        } : null;

      // Declarations:
    } else if (REPLSymbol.DECLARE.symbol.equals(command)) {
      if (argument != null) {
        try {
          Parser.ParserDeclaration declaration = CoraParser.readDeclaration(argument, true, null);
          return new AddDeclaration(TermFactory.createConstant(declaration.name(),
            declaration.type()));
        } catch (Exception e) {
          System.err.println(e.getMessage());
          return null;
        }
      } else {
        return null;
      }
    } else if (REPLSymbol.REMOVE_DECLARATION.symbol.equals(command)) {
      if (argument == null) return null;
      try {
        int arg = Integer.parseInt(argument);
        return new RemoveDeclaration(arg);
      } catch (NumberFormatException e) {
        return null;
      }
    } else if (REPLSymbol.LIST_DECLARATIONS.symbol.equals(command)) {
      return new ListDeclarations();
    } else if (REPLSymbol.CLEAR_DECLARATIONS.symbol.equals(command)) {
      return new ClearDeclarations();

      // General:
    } else if (REPLSymbol.EXIT.symbol.equals(command)) {
      return new Exit();
    } else if (REPLSymbol.HELP.symbol.equals(command)) {
      if (argument != null) {
        return new Help(argument);
      } else  {
        return new Help();
      }
    } else if (REPLSymbol.REDUCE.symbol.equals(command)) {
      try {
        return argument != null ?
          new Reduce(CoraInputReader.readTerm(argument,
            MemTRSREPL.currentTRS)) : null;
      } catch (Exception e) {
        System.err.println(e.getMessage());
        return null;
      }
    } else if (REPLSymbol.SAVE.symbol.equals(command)) {
      return argument != null ? new Save(argument) : null;
    } else if (REPLSymbol.TRS.symbol.equals(command)) {
      return new ListTRS();

      // Imports:
    } else if (REPLSymbol.EXCLUDE.symbol.equals(command)) {
      if (argument == null) return null;
      try {
        int arg = Integer.parseInt(argument);
        new Exclude(arg);
      } catch (NumberFormatException e) {
        return null;
      }
    } else if (REPLSymbol.INCLUDE.symbol.equals(command)) {
      return argument != null ? new Include(argument) : null;
    } else if (REPLSymbol.LIST_IMPORTS.symbol.equals(command)) {
      return new ListImports();

      // Memory:
    } else if (REPLSymbol.PRINT_MEM.symbol.equals(command)) {
      return new PrintMem();
    } else if (REPLSymbol.RESET_MEM.symbol.equals(command)) {
      return new ResetMem();
    } else if (REPLSymbol.RESIZE_MEM.symbol.equals(command)) {
      if (argument == null) return null;
      try {
        int arg = Integer.parseInt(argument);
        return new ResizeMem(arg);
      } catch (NumberFormatException e) {
        return null;
      }

      // Rewrite Settings:
    } else if (REPLSymbol.REDUCTION_MODE.symbol.equals(command)) {
      return argument != null ? switch (argument) {
        case "first" -> new ReductionMode(Settings.ReductionMode.FirstMatch);
        case "random" -> new ReductionMode(Settings.ReductionMode.Random);
        case "parallel" -> new ReductionMode(Settings.ReductionMode.Parallel);
        default -> null;
      } : null;
    } else if (REPLSymbol.STRATEGY.symbol.equals(command)) {
      return argument != null ? switch (argument) {
        case "full" -> new Strategy(Settings.Strategy.Full);
        case "innermost" -> new Strategy(Settings.Strategy.Innermost);
        case "cbv" -> new Strategy(Settings.Strategy.CallByValue);
        default -> null;
      } : null;

      // Rules:
    } else if (REPLSymbol.RULE.symbol.equals(command)) {
      try {
        return new AddRule(CoraInputReader.readRule(argument, MemTRSREPL.currentTRS));
      } catch (Exception e) {
        System.err.println(e.getMessage());
        return null;
      }
    } else if (REPLSymbol.CLEAR_RULES.symbol.equals(command)) {
      return new ClearRules();
    } else if (REPLSymbol.LIST_RULES.symbol.equals(command)) {
      return new ListRules();
    } else if (REPLSymbol.REMOVE_RULE.symbol.equals(command)) {
      if (argument == null) return null;
      try {
        int arg = Integer.parseInt(argument);
        new RemoveRule(arg);
      } catch (NumberFormatException e) {
        return null;
      }
    } else {
      return null;
    }
    return null;
  }

  REPLSymbol replSymbol();
}
