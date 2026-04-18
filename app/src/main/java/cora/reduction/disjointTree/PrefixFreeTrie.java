package cora.reduction.disjointTree;

import charlie.terms.position.Position;

public class PrefixFreeTrie {

  private final DisjointTreeNode root;

  public PrefixFreeTrie() {
    this.root = new DisjointTreeNode();
  }

  private boolean canInsert(String[] elems) {
    DisjointTreeNode node = root;

    if (elems.length == 0) {
      return !root.isEnd && root.children.isEmpty();
    }

    for (String elem : elems) {
      if (node.isEnd) return false;
      DisjointTreeNode next = node.children.get(elem);
      if (next == null) {
        return true;
      }
      node = next;
    }

    return !node.isEnd && node.children.isEmpty();
  }

  private void doInsert(String[] elems) {
    DisjointTreeNode node = root;

    if (elems.length == 0) {
      root.isEnd = true;
      return;
    }

    for (String elem : elems) {
      node.children.putIfAbsent(elem, new DisjointTreeNode());
      node = node.children.get(elem);
    }

    node.isEnd = true;
  }

  public boolean insert(Position position) {
    String sequence = position.toString();
    int lastDot = sequence.lastIndexOf('.');

    if (lastDot != -1) {
      sequence = sequence.substring(0, lastDot);
    } else {
      sequence = "";
    }

    String[] elems = sequence.isEmpty() ? new String[0] : sequence.split("\\.");

    if (!canInsert(elems)) return false;

    doInsert(elems);
    return true;
  }
}
