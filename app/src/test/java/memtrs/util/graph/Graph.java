package memtrs.util.graph;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Graph {
  private final Set<Vertice> vertices;
  private final Set<Edge> edges;

  public Graph(Set<Vertice> vertices, Set<Edge> edges) {
    this.vertices = new TreeSet<>(vertices);
    this.edges = new HashSet<>(edges);
  }

  public Graph() {
    this.vertices = new HashSet<>();
    this.edges = new HashSet<>();
  }

  public Set<Vertice> getVertices() {
    return vertices;
  }

  public Set<Edge> getEdges() {
    return edges;
  }

  public List<Edge> getNeighbours(Vertice vertice) {
    return edges.stream()
      .filter(edge -> edge.from().equals(vertice))
      .sorted(Comparator.comparing(Edge::to))
      .collect(Collectors.toList());
  }

  public void addEdge(Vertice from, Vertice to) {
    vertices.add(from);
    vertices.add(to);
    edges.add(new Edge(from, to));
  }

  public void addVertice(Vertice vertice) {
    vertices.add(vertice);
  }
}
