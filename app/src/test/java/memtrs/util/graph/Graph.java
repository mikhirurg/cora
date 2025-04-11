package memtrs.util.graph;

import com.sun.source.tree.Tree;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Graph {
  private final Set<Vertex> vertices;
  private final Set<Edge> edges;

  public Graph(Set<Vertex> vertices, Set<Edge> edges) {
    this.vertices = new TreeSet<>(vertices);
    this.edges = new HashSet<>(edges);
  }

  public Graph() {
    this.vertices = new TreeSet<>();
    this.edges = new HashSet<>();
  }

  public Set<Vertex> getVertices() {
    return vertices;
  }

  public Set<Edge> getEdges() {
    return edges;
  }

  public List<Edge> getNeighbours(Vertex vertex) {
    return edges.stream()
      .filter(edge -> edge.from().equals(vertex))
      .sorted(Comparator.comparing(Edge::to))
      .collect(Collectors.toList());
  }

  public void addEdge(Vertex from, Vertex to) {
    vertices.add(from);
    vertices.add(to);
    edges.add(new Edge(from, to));
  }

  public void addVertice(Vertex vertex) {
    vertices.add(vertex);
  }
}
