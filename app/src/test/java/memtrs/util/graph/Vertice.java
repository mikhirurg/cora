package memtrs.util.graph;

public record Vertice(int id) implements Comparable<Vertice> {

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Vertice otherVertice)) {
      return false;
    }

    return id == otherVertice.id;
  }

  @Override
  public String toString() {
    return "(" + id + ")";
  }

  @Override
  public int compareTo(Vertice other) {
    return Integer.compare(id, other.id);
  }
}
