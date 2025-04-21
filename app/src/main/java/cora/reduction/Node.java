package cora.reduction;

import charlie.terms.Term;
import charlie.terms.position.Position;
import charlie.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Node {

  private static boolean isPrefix(List<String> a, List<String> b) {
    if (a.size() > b.size()) {
      return false;
    }
    for (int i = 0; i < a.size(); i++) {
      if (!a.get(i).equals(b.get(i))) {
        return false;
      }
    }
    return true;
  }

  public static Node constructTree(List<Pair<Term, Position>> terms) {
    List<Node> children = new ArrayList<>();
    Position value = terms.getFirst().snd();
    for (int i = 1; i < terms.size(); i++) {
      List<String> childPos = List.of(terms.get(i).snd().toString().split("\\."));
      childPos = childPos.subList(0, childPos.size() - 1);
      List<String> parentPos = List.of(value.toString().split("\\."));
      parentPos = parentPos.subList(0, parentPos.size() - 1);
      if (childPos.size() == parentPos.size() + 1 && isPrefix(parentPos, childPos)) {
        children.add(constructTree(terms.subList(i, terms.size())));
      }
    }

    return new Node(children, value);
  }

  public Position value;
  public List<Node> children;

  public Node(List<Node> children, Position value) {
    this.children = new ArrayList<>(children);
    this.value = value;
  }

  private void dfs(Node node, Consumer<Node> c) {
    c.accept(node);
    for (Node child : node.children) {
      dfs(child, c);
    }
  }

  public List<Node> findLeaves() {
    List<Node> leaves = new ArrayList<>();
    dfs(this, node -> { if (node.children.isEmpty()) { leaves.add(node); } });
    return leaves;
  }

  public void removeLeaves() {
    dfs(this, node -> {
      List<Node> toRemove = new ArrayList<>();
      for (Node child : node.children) {
        if (child.children.isEmpty()) {
          toRemove.add(child);
        }
      }
      node.children.removeAll(toRemove);
    });
  }
}
