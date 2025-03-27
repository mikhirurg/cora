package charlie.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class Preprocessor {

  private final String INCLUDE_KEY = "#include ";

  private final Path source;

  private final Set<Path> includedFiles;

  Preprocessor(String filename) {
    this.source = Path.of(filename);
    this.includedFiles = new HashSet<>();
  }

  private void unpack(Path source, Path dest) throws IOException {
    includedFiles.add(source);
    Files.lines(source)
      .forEach(line -> {
        if (line.startsWith(INCLUDE_KEY)) {
          String subSource = line.substring(INCLUDE_KEY.length()).trim();
          try {
            Path inclusion = source.getParent().resolve(Path.of(subSource)).normalize();
            if (!includedFiles.contains(inclusion)) {
              unpack(inclusion, dest);
            }
          } catch (IOException e) {
            throw new RuntimeException("Preprocessor is unable to resolve the file inclusion: " + subSource + ", for: " + source);
          }
        } else {
          try {
            Files.writeString(dest, line + "\n", StandardOpenOption.APPEND);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      });
  }

  public String preprocess() {
    includedFiles.clear();
    Path coraTmpFile = null;
    try {
      coraTmpFile = Files.createTempFile("cora_preprocessor_",
          source.toString().substring(source.toString().lastIndexOf(".")));
      unpack(source, coraTmpFile);
      return coraTmpFile.toAbsolutePath().toString();
    } catch (Exception e) {
      try {
        if (coraTmpFile != null) {
          Files.delete(coraTmpFile);
        }
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }

      throw new RuntimeException(e);
    }
  }
}
