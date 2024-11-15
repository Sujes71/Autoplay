package es.zed.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.zed.domain.input.HuntInputPort;
import es.zed.domain.output.dto.HealActionDto;
import es.zed.domain.output.request.HuntRequestBody;
import es.zed.infrastructure.Constants;
import es.zed.infrastructure.manager.ExceptionManager;
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
  private final ExceptionManager exceptionManager;
  private final ObjectMapper objectMapper;

  private boolean end;

  public HunterService(RobotManager robotManager, ScreenManager screenManager, FileManager fileManager, ExceptionManager exceptionManager,
      ObjectMapper objectMapper) {
    this.robotManager = robotManager;
    this.screenManager = screenManager;
    this.fileManager = fileManager;
    this.exceptionManager = exceptionManager;
    this.objectMapper = objectMapper;
  }

  @Override
  public void startHunt(final String name) {
    try {
      end = false;
      List<int[]> targetColors = fileManager.asignShinyToFind(name);
      File ringFile = fileManager.getRingFile();
      Rectangle captureRect = screenManager.getCaptureRectangle();

      while (!end) {
        BufferedImage screenshot = robotManager.captureScreenshot(captureRect);
        if (screenManager.processScreenshot(screenshot, targetColors, ringFile)) {
          stopHunt();
        }
      }
      log.info("Finalized!");
    } catch (Exception e) {
      log.error("Error: {}", e.getMessage());
    }
  }

  @Override
  public void manageAction(final HuntRequestBody requestBody) {
    int fightCount = 0;
    while (!end) {
      if (screenManager.isSpecificWindowOpen(requestBody.getTitle())) {
        try {
          if (Objects.nonNull(requestBody.getSteps())) {
            robotManager.executeActions(requestBody.getSteps());
          }

          Rectangle captureRect = screenManager.getCaptureRectangle();
          BufferedImage screenshot = robotManager.captureScreenshot(captureRect);

          if (screenManager.isFight(screenshot)) {
            switch (requestBody.getMode()) {
              case Constants.ESCAPE -> robotManager.scape();
              case Constants.FIGHT -> {
                robotManager.fight();
                fightCount++;
                robotManager.sleep(requestBody.getTime());
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
          exceptionManager.handleException(e);
        }
      }
    }
  }

  @Override
  public void stopHunt() {
    end = true;
  }
}