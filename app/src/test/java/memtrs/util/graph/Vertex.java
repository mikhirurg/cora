package memtrs.util.graph;

public record Vertex(int id) implements Comparable<Vertex> {

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Vertex otherVertex)) {
      return false;
    }

    return id == otherVertex.id;
  }

  @Override
  public String toString() {
    return "(" + id + ")";
  }

  @Override
  public int compareTo(Vertex other) {
    return Integer.compare(id, other.id);
  }
}
