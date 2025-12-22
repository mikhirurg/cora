package memtrs.util.graph;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.images.CustomResolution;
import org.graphstream.stream.file.images.FileSinkImagesFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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

  private static final Random random = new Random();

  public static Graph generateRandomGraph(int nodes, int edges) {
    if (edges > nodes * nodes) {
      throw new RuntimeException("Unable to generate a graph!");
    }

    Graph graph = new Graph();
    for (int i = 0; i < nodes; i++) {
      graph.addVertice(new Vertex(i));
    }
    int i = 0;
    while (i < edges) {
      Vertex u = new Vertex(random.nextInt(nodes));
      Vertex v = new Vertex(random.nextInt(nodes));
      if (!graph.getEdges().contains(new Edge(u, v))) {
        graph.addEdge(u, v);
        i++;
      }
    }
    return graph;
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

  public org.graphstream.graph.Graph convertToGSGraph() {
    org.graphstream.graph.Graph graph = new SingleGraph("Graph");
    for (Vertex vertex : vertices) {
      graph.addNode(vertex.toString());
    }
    for (Edge edge : edges) {
      graph.addEdge(
        edge.from().toString() + edge.to().toString(),
        edge.from().toString(),
        edge.to().toString(),
        true
      );
    }
    for (Node node : graph) {
      node.setAttribute("ui.label", node.getId());
    }

    return graph;
  }

  public void saveToImage(Path path) throws IOException {
    FileSinkImages pic = FileSinkImages.createDefault();
    pic.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
    pic.setQuality(FileSinkImages.Quality.HIGH);
    int nodeDistance = 250;
    pic.setResolution(new CustomResolution((int) Math.sqrt(vertices.size()) * nodeDistance,
      (int) Math.sqrt(vertices.size()) * nodeDistance));

    org.graphstream.graph.Graph graph = convertToGSGraph();
    graph.setAttribute("ui.stylesheet", "node { size: 40px; fill-color: blue, aquamarine; " +
      "fill-mode: " +
      "gradient-diagonal1; text-mode: normal; text-color: black; text-alignment: center; " +
      "text-style: bold; text-size: 14; } edge {" +
      " " +
      "size: 1.2px; arrow-size: 10px, 8px; " +
      "stroke-width: 10px; " +
      "}");
    pic.writeAll(graph, path.toString());
  }
}
