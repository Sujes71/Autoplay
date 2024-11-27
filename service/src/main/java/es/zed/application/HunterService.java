package es.zed.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.zed.domain.input.HuntInputPort;
import es.zed.domain.output.dto.HealActionDto;
import es.zed.domain.output.request.HuntRequestBody;
import es.zed.infrastructure.Constants;
import es.zed.infrastructure.manager.FileManager;
import es.zed.infrastructure.manager.RobotManager;
import es.zed.infrastructure.manager.ScreenManager;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HunterService implements HuntInputPort {

  private final RobotManager robotManager;
  private final ScreenManager screenManager;
  private final FileManager fileManager;
  private final ObjectMapper objectMapper;

  private volatile boolean end;

  public HunterService(RobotManager robotManager, ScreenManager screenManager, FileManager fileManager,
      ObjectMapper objectMapper) {
    this.robotManager = robotManager;
    this.screenManager = screenManager;
    this.fileManager = fileManager;
    this.objectMapper = objectMapper;
  }

  @Override
  public void start(final String name, final HuntRequestBody requestBody) {
    try {
      end = false;
      Thread actionThread = new Thread(() -> manageAction(requestBody, Thread.currentThread()));
      actionThread.start();

      List<int[]> targetColors = fileManager.asignShinyToFind(name);
      File ringFile = fileManager.getRingFile();
      Rectangle captureRect = screenManager.getCaptureRectangle(985, 240, 600, 260);
      while (!end) {
        BufferedImage screenshot = robotManager.captureScreenshot(captureRect);
        if (screenManager.processScreenshot(screenshot, targetColors, ringFile)) {
          actionThread.interrupt();
          break;
        }
      }
      if (!actionThread.isInterrupted()) {
        actionThread.interrupt();
      }
      log.info("Finalized!");
    } catch (Exception e) {
      log.error("Error: {}", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  private void manageAction(final HuntRequestBody requestBody, final Thread currentThread) {
    int fightCount = 0;
    while (!currentThread.isInterrupted()) {
      if (screenManager.isSpecificWindowOpen(requestBody.getTitle())) {
        try {
          if (Objects.nonNull(requestBody.getSteps())) {
            robotManager.executeActions(requestBody.getSteps());
          }
          Rectangle captureRect = screenManager.getCaptureRectangle(1000, 400, 400, 100);
          BufferedImage screenshot = robotManager.captureScreenshot(captureRect);

          if (screenManager.isFight(screenshot)) {
            switch (requestBody.getMode()) {
              case Constants.ESCAPE -> robotManager.scape();
              case Constants.FIGHT -> {
                robotManager.fight();
                robotManager.sleep(requestBody.getTime());
                fightCount++;
              }
              default -> throw new IllegalArgumentException("Invalid mode: " + requestBody.getMode());
            }
          }
          if (Objects.nonNull(requestBody.getHeal()) && fightCount == requestBody.getHeal().getCount()) {
            File file = fileManager.getHealFile(requestBody.getHeal().getCity());
            HealActionDto healActions = objectMapper.readValue(file, HealActionDto.class);
            robotManager.executeActions(healActions.getSteps());
            fightCount = 0;
          }
        } catch (InterruptedException | IOException e) {
          log.error("ERROR: {}", e.getMessage());
          currentThread.interrupt();
        }
      }
    }
  }

  @Override
  public void stop() {
    end = true;
  }
}