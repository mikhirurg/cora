package cora.reduction.disjointTree;

import java.util.HashMap;

public class DisjointTreeNode {

  public HashMap<String, DisjointTreeNode> children;
  public boolean isEnd;

  public DisjointTreeNode(HashMap<String, DisjointTreeNode> children, boolean isEnd) {
    this.children = new HashMap<>(children);
    this.isEnd = isEnd;
  }

  public DisjointTreeNode() {
    this(new HashMap<>(), false);
  }
}
