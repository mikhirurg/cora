package termutils;

import java.util.Random;

public class TermUtils {
  private static Random random = new Random();

  public static String arrayToListTerm(int[] arr) {
    StringBuilder builder = new StringBuilder();
    for (int j : arr) {
      builder.append("cons(")
        .append(j)
        .append(", ");
    }
    builder.append("nil");
    builder.append(")".repeat(arr.length));
    return builder.toString();
  }

  public static int[] genRandomArray(int size, int min, int max) {
    int[] arr = new int[size];
    for (int i = 0; i < size; i++) {
      arr[i] = random.nextInt(min, max);
    }

    return arr;
  }

  public static void main(String[] args) {
    int[] arr = genRandomArray(5, -100, 100);
    System.out.println(arrayToListTerm(arr));
  }
}
