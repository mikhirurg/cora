package memtrs.util.matrix;

public class Matrix {
  private final int width;
  private final int height;
  private final int[][] matrix;

  public Matrix(int width, int height) {
    this.width = width;
    this.height = height;
    this.matrix = new int[height][width];
  }

  public Matrix(int[][] matrix) {
    this.height = matrix.length;
    this.width = matrix[0].length;
    this.matrix = matrix;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int get(int x, int y) {
    return matrix[y][x];
  }

  public void set(int x, int y, int val) {
    matrix[y][x] = val;
  }
}
