package es.zed.infrastructure.manager;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import es.zed.infrastructure.Constants;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScreenManager {

  private final FileManager fileManager;

  public ScreenManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  public boolean processScreenshot(BufferedImage screenshot, List<int[]> targetColors, File ringFile) throws IOException {
    for (int x = 0; x < screenshot.getWidth(); x++) {
      for (int y = 0; y < screenshot.getHeight(); y++) {
        int pixelColor = screenshot.getRGB(x, y);
        int[] rgb = {(pixelColor >> 16) & 0xff, (pixelColor >> 8) & 0xff, pixelColor & 0xff};
        if (isColorMatch(rgb, targetColors, Constants.THRESHOLD)) {
          log.info("Color found!");
          Desktop.getDesktop().open(ringFile);
          return true;
        }
      }
    }
    return false;
  }

  public boolean isFight(BufferedImage screenshot) {
    for (int x = 0; x < screenshot.getWidth(); x++) {
      for (int y = 0; y < screenshot.getHeight(); y++) {
        int pixelColor = screenshot.getRGB(x, y);
        int[] rgb = {(pixelColor >> 16) & 0xff, (pixelColor >> 8) & 0xff, pixelColor & 0xff};
        if (isColorMatch(rgb, Constants.FIGHT_COLOR, Constants.THRESHOLD)) {
          log.info("Fight detected!");
          return true;
        }
      }
    }
    return false;
  }

  public boolean isColorMatch(int[] rgb, List<int[]> targetColors, int threshold) {
    for (int[] targetColor : targetColors) {
      if (withinThreshold(rgb, targetColor, threshold)) {
        return true;
      }
    }
    return false;
  }

  private boolean withinThreshold(int[] color1, int[] color2, int threshold) {
    int match = 0;
    for (int i = 0; i < 3; i++) {
      if (Math.abs(color1[i] - color2[i]) <= threshold) {
        match++;
      }
    }
    return match == 3;
  }

  public boolean isSpecificWindowOpen(String windowTitle) {
    User32 user32 = User32.INSTANCE;
    WinDef.HWND hwnd = user32.GetForegroundWindow();
    char[] buffer = new char[1024];
    user32.GetWindowText(hwnd, buffer, 1024);
    String title = Native.toString(buffer);

    return title.contains(windowTitle);
  }

  public Rectangle getCaptureRectangle(int x, int y, int width, int height) {
    return new Rectangle(x, y, width, height);
  }

}
