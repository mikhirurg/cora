package repl;

import charlie.parser.Parser;
import charlie.reader.CoraInputReader;
import charlie.terms.FunctionSymbol;
import charlie.terms.Term;
import charlie.terms.TermFactory;
import charlie.trs.Alphabet;
import charlie.trs.Rule;
import charlie.trs.TRS;
import charlie.trs.TrsFactory;
import cora.config.Settings;
import cora.reduction.MemReducer;
import cora.reduction.Reducer;
import repl.commands.declarations.AddDeclaration;
import repl.commands.declarations.ClearDeclarations;
import repl.commands.declarations.ListDeclarations;
import repl.commands.declarations.RemoveDeclaration;
import repl.commands.general.Help;
import repl.commands.general.ListTRS;
import repl.commands.general.Save;
import repl.commands.imports.ListImports;
import repl.commands.memory.PrintMem;
import repl.commands.memory.ResizeMem;
import repl.commands.rules.AddRule;
import repl.commands.imports.Exclude;
import repl.commands.general.Exit;
import repl.commands.imports.Include;
import repl.commands.REPLCommand;
import repl.commands.general.Reduce;
import repl.commands.rewriting_settings.ReductionMode;
import repl.commands.memory.ResetMem;
import repl.commands.rules.ClearRules;
import repl.commands.rewriting_settings.Strategy;
import repl.commands.debug.ShowMemory;
import repl.commands.debug.ShowReductions;
import repl.commands.rules.ListRules;
import repl.commands.rules.RemoveRule;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MemTRSREPL {

  public static TRS currentTRS;
  public static List<String> includeList;
  public static List<Rule> ruleList;
  public static List<FunctionSymbol> declarationsList;

  public static Term reduceToNF(Term start, TRS trs, Settings.Strategy strategy) {
    Settings.Strategy oldStrategy = Settings.queryRewritingStrategy();
    Settings.setStrategy(strategy);

    Reducer reducer = new Reducer(trs);
    Term s = start;
    Term oldS = null;
    do {
      oldS = s;
      s = reducer.reduce(s);
    } while (s != null);

    Settings.setStrategy(oldStrategy);
    return oldS;
  }

  private static boolean refreshTRS() {
    TRS oldTRS = currentTRS;

    try {
      currentTRS =
        CoraInputReader.readTrsFromString(includeList.stream()
          .map(include -> "#include " + include)
          .collect(Collectors.joining("\n")));

      Set<Rule> rules = new HashSet<>(currentTRS.queryRules());
      rules.addAll(ruleList);

      Set<FunctionSymbol> declarations = new HashSet<>(declarationsList);
      declarations.addAll(currentTRS.queryAlphabet().getSymbols());

      currentTRS = TrsFactory.createTrs(new Alphabet(declarations), rules.stream().toList(), TrsFactory.LCTRS);

      return true;
    } catch (Exception e) {
      currentTRS = oldTRS;
      System.out.println("Unable to construct new TRS! Rolling back to the previous version!");
      return false;
    }
  }

  public static void main() throws FileNotFoundException {
    Scanner in = new Scanner(System.in);

    MemReducer.resizeMemory(10);

    System.out.println("Welcome to MemTRS REPL v0.1");
    System.out.println("Memory size: " + Settings.getMemMaxSize());

    includeList = new ArrayList<>();
    includeList.add("/home/mikhirurg/Contribution/cora/memtrs/stdlib/kernel.lctrs");

    ruleList = new ArrayList<>();
    declarationsList = new ArrayList<>();

    currentTRS = TrsFactory.createTrs(new Alphabet(declarationsList), ruleList, TrsFactory.LCTRS);
    refreshTRS();

    REPLCommand parsedCommand;
    do {
      parsedCommand = REPLCommand.of(in.nextLine());
      if (parsedCommand == null) {
        System.out.println("Unknown command! Try again!");
        continue;
      }
      switch (parsedCommand) {
        // Debug
        case ShowMemory c -> {
          Settings.setShowIntermediateMemory(c.isEnabled());
          System.out.println("OK");
        }
        case ShowReductions c -> {
          Settings.setShowIntermediateReductions(c.isEnabled());
          System.out.println("OK");
        }

        // Declarations
        case AddDeclaration c -> {
          if (!declarationsList.contains(c.getDeclaration())) {
            declarationsList.add(c.getDeclaration());
            if (refreshTRS()) {
              System.out.println("OK");
            }
          } else {
            System.out.println("This function symbol declaration already exists!");
          }
        }
        case RemoveDeclaration c -> {
          if (c.getNumber() >= 1 && c.getNumber() <= declarationsList.size()) {
            declarationsList.remove(c.getNumber() - 1);
            if (refreshTRS()) {
              System.out.println("OK");
            }
          } else {
            System.out.println("Unable remove the function symbol declaration #" + c.getNumber() + "!");
          }
        }
        case ListDeclarations _ -> {
          System.out.println("Temporary symbol declarations:");
          for (int i = 1; i <= declarationsList.size(); i++) {
            System.out.println("#" + i + " " + declarationsList.get(i - 1).queryName() + " :: " + declarationsList.get(i - 1).queryType());
          }
        }
        case ClearDeclarations _ -> {
          System.out.println("OK");
          declarationsList.clear();
        }

        // General
        case Exit _ -> {
          System.out.println("Bye!");
          return;
        }
        case Help c -> {
          if (c.getCommand() != null) {
            Arrays.stream(REPLCommand.REPLSymbol.values())
              .filter(symb -> symb.getSymbol().equals(c.getCommand()))
              .findFirst()
              .ifPresentOrElse(symb -> System.out.println(symb.getDescription()),
                () -> System.out.println("Unable to find help for command \"" + c.getCommand() + "\"."));
          } else {
            Arrays.stream(REPLCommand.REPLSymbol.values())
              .forEach(command -> System.out.println(command.getDescription()));
          }
        }
        case ListTRS _ -> {
          System.out.println(currentTRS.toString());
        }
        case Reduce c -> {
          Term result = reduceToNF(c.getTerm(), currentTRS, Settings.queryRewritingStrategy());
          System.out.println("Reduced to: " + result);
        }
        case Save c -> {
          try (var fw = new FileWriter(c.getPath())) {
            fw.append(currentTRS.toString());
          } catch (IOException e) {
            System.out.println("Unable to save MemTRS to file \"" + c.getPath() + "\"!");
          } finally {
            System.out.println("OK");
          }
        }

        // Imports
        case Exclude c -> {
          if (c.getNumber() >= 1 && c.getNumber() <= includeList.size()) {
            includeList.remove(c.getNumber());
            if (refreshTRS()) {
              System.out.println("OK");
            }
          } else {
            System.out.println("Unable exclude the input script #" + c.getNumber() + "!");
          }
        }
        case Include command -> {
          if (!includeList.contains(command.getPath())) {
            includeList.add(command.getPath());
            if (refreshTRS()) {
              System.out.println("OK");
            }
          } else {
            System.out.println("This script is already imported!");
          }
        }
        case ListImports _ -> {
          System.out.println("External script imports:");
          for (int i = 1; i <= includeList.size(); i++) {
            System.out.println("#" + i + " " + includeList.get(i - 1));
          }
        }

        // Memory
        case PrintMem _ -> {
          System.out.println("Memory:");
          System.out.println(MemReducer.MEMORY);
        }
        case ResetMem _ -> {
          MemReducer.resetMemory();
          System.out.println("OK");
        }
        case ResizeMem c -> {
          if (c.getSize() < 2) {
            System.out.println("Memory size is too small!");
          } else {
            MemReducer.resizeMemory(c.getSize());
            System.out.println("OK");
          }
        }

        // Rewriting settings:
        case ReductionMode c -> {
          Settings.setReductionMode(c.getReductionMode());
          System.out.println("OK");
        }
        case Strategy c -> {
          Settings.setStrategy(c.getStrategy());
          System.out.println("OK");
        }

        // Rules
        case AddRule c -> {
          if (!ruleList.contains(c.getRule())) {
            ruleList.add(c.getRule());
            if (refreshTRS()) {
              System.out.println("OK");
            }
          } else {
            System.out.println("This rule already exists!");
          }
        }
        case ClearRules _ -> {
          ruleList.clear();
          if (refreshTRS()) {
            System.out.println("OK");
          }
        }
        case RemoveRule c -> {
          if (c.getNumber() >= 1 && c.getNumber() <= ruleList.size()) {
            ruleList.remove(c.getNumber());
            if (refreshTRS()) {
              System.out.println("OK");
            }
          } else {
            System.out.println("Unable exclude the rule #" + c.getNumber() + "!");
          }
        }
        case ListRules _ -> {
          System.out.println("Temporary rule declarations:");
          for (int i = 1; i <= ruleList.size(); i++) {
            System.out.println("#" + i + " " + ruleList.get(i - 1));
          }
        }
        default -> throw new IllegalStateException("Unexpected value: " + parsedCommand);
      }
    } while (true);
  }
}
