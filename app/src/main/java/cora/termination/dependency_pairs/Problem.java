package cora.termination.dependency_pairs;

import cora.data.digraph.Digraph;
import cora.exceptions.IllegalArgumentError;
import cora.exceptions.NullInitialisationError;
import cora.rewriting.TRS;
import cora.terms.FunctionSymbol;
import cora.types.Type;
import cora.types.TypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Problem {
  private final List<DP> _dps;
  private Digraph _graph;
  private TRS _trs;

  public Problem (@NotNull List<DP> dps, @NotNull TRS trs, @NotNull Digraph graph) {
    if (dps == null || graph == null) throw new NullInitialisationError (
      "Problem",
      // update this message to a proper one
      "for god's sake don't create objects with null arguments!"
    );

    if (dps.size() == graph.getNumberOfVertices()) {
      _dps = dps;
      _graph = graph;
      _trs = trs;
    } else {
      throw new IllegalArgumentError (
        "Problem",
        "Problem",
        "error initializing new DP problem.\n" +
          "The number of DPs in the problem " +
          STR."is \{dps.size()} while their graph has \{graph.getNumberOfVertices()} vertices"
      );
    }
  }

  public Problem(@NotNull List<DP> dps, @NotNull TRS trs) {
    if (dps == null) throw new NullInitialisationError (
      "Problem",
      "trying to create a DP problem with null reference dps."
    );
    _dps = dps;
    _trs = trs;
  }

  public List<DP> getDPList() {
    return _dps;
  }

  public Optional<Digraph> getGraph() {
    return Optional.ofNullable(_graph);
  }

  public TRS getTRS() {
    return _trs;
  }

  public DP removeDP(int dpIndex) {
    if (dpIndex < 0 || dpIndex > _dps.size())
      throw new IllegalArgumentError (
        "Problem",
        "removeDP",
        "argument index is out of bounds."
      );
    DP removedDP = _dps.get(dpIndex);

    _dps.remove(dpIndex);
    getGraph().ifPresent(g -> g.removeVertex(dpIndex));

    return removedDP;
  }

  public Set<FunctionSymbol> getSharpHeads() {
    Set<FunctionSymbol> allFns = new TreeSet<>();
    Type dpSort = TypeFactory.createSort("DP_SORT");

    for (DP dp : _dps) {
      dp.lhs().storeFunctionSymbols(allFns);
      dp.rhs().storeFunctionSymbols(allFns);
    }

    return allFns
      .stream()
      .filter(f -> f.queryType().queryOutputType().equals(dpSort))
      .collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return _dps.toString();
  }
}
