package es.zed.infrastructure.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class FileManager {

  public File getRingFile() {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(Objects.requireNonNull(classLoader.getResource("ring.mp3")).getFile());
  }

  public File getHealFile(String city) {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(Objects.requireNonNull(classLoader.getResource("heal/" + city + ".txt")).getFile());
  }

  public List<int[]> asignShinyToFind(String name) throws IOException {
    List<int[]> rgbList = new ArrayList<>();
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(Objects.requireNonNull(classLoader.getResource("colors.txt")).getFile());

    List<String> lines = Files.readAllLines(file.toPath());
    for (String line : lines) {
      String[] parts = line.split(":");
      if (name.equals(parts[0].trim())) {
        for (int i = 1; i < parts.length; i++) {
          String[] rgbValues = parts[i].trim().replace("(", "").replace(")", "").split(",");
          int[] rgb = new int[3];
          for (int j = 0; j < 3; j++) {
            rgb[j] = Integer.parseInt(rgbValues[j].trim());
          }
          rgbList.add(rgb);
        }
      }
    }
    return rgbList;
  }
}
