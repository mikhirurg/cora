package memtrs.util.graph;

public record Edge(Vertex from, Vertex to) {
  @Override
  public String toString() {
    return from + " -> " + to;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Edge otherEdge)) {
      return false;
    }

    return this.from.equals(otherEdge.from) &&
      this.to.equals(otherEdge.to);
  }
}
