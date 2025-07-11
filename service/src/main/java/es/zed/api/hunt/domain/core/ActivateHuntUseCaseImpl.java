package es.zed.api.hunt.domain.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.zed.api.hunt.domain.port.inbound.ActivateHuntUseCase;
import es.zed.hunt.domain.model.HealAction;
import es.zed.hunt.domain.model.Hunt;
import es.zed.shared.Constants;
import es.zed.shared.domain.utils.FileUtils;
import es.zed.shared.domain.utils.RobotUtils;
import es.zed.shared.domain.utils.ScreenUtils;
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
public class ActivateHuntUseCaseImpl implements ActivateHuntUseCase {

  private final RobotUtils robotUtils;
  private final ScreenUtils screenUtils;
  private final FileUtils fileUtils;
  private final ObjectMapper objectMapper;

  public static volatile boolean shouldEndHunt;

  public ActivateHuntUseCaseImpl(RobotUtils robotUtils, ScreenUtils screenUtils, FileUtils fileUtils,
      ObjectMapper objectMapper) {
    this.robotUtils = robotUtils;
    this.screenUtils = screenUtils;
    this.fileUtils = fileUtils;
    this.objectMapper = objectMapper;
  }

  @Override
  public void execute(Hunt input) {
    shouldEndHunt = false;
    int fightCount = 0;

    log.info("Starting hunt for: {}", input.getName());

    while (!shouldEndHunt) {
      if (screenUtils.isSpecificWindowOpen(input.getTitle())) {
        try {
          if (Objects.nonNull(input.getSteps())) {
            robotUtils.executeActions(input.getSteps());
          }

          if (isBattleEncounterActive()) {
            checkForShinyTarget(input.getName());

            switch (input.getMode()) {
              case Constants.ESCAPE -> {
                log.debug("Escaping from battle encounter");
                robotUtils.scape();
              }
              case Constants.FIGHT -> {
                log.debug("Engaging in battle encounter");
                executeFightSequence(input.getTime());
                fightCount++;
              }
              default -> throw new IllegalArgumentException("Invalid battle mode: " + input.getMode());
            }
          }

          if (Objects.nonNull(input.getHeal()) && fightCount == input.getHeal().getCount()) {
            log.info("Fight count threshold reached, performing healing sequence");
            executeHealingSequence(input.getHeal().getCity());
            fightCount = 0;
          }

        } catch (IOException | InterruptedException e) {
          log.error("Error during hunt execution: {}", e.getMessage());
          break;
        }
      }
    }

    log.info("Hunt completed successfully for: {}", input.getName());
  }

  /**
   * Checks if a battle encounter is currently active on screen
   * @return true if battle UI is detected, false otherwise
   */
  private boolean isBattleEncounterActive() {
    Rectangle battleDetectionArea = screenUtils.getCaptureRectangle(359, 130, 500, 100);
    BufferedImage battleScreenshot = robotUtils.captureScreenshot(battleDetectionArea);
    return screenUtils.isFight(battleScreenshot);
  }

  /**
   * Scans the screen for shiny target indicators and terminates hunt if found
   * @param targetName the name of the target being hunted
   * @throws IOException if there's an error reading target configuration files
   */
  private void checkForShinyTarget(String targetName) throws IOException {
    List<int[]> targetColors = fileUtils.asignShinyToFind(targetName);
    File ringIndicatorFile = fileUtils.getRingFile();

    Rectangle shinyDetectionArea = screenUtils.getCaptureRectangle(985, 240, 600, 260);
    BufferedImage screenshot = robotUtils.captureScreenshot(shinyDetectionArea);

    if (screenUtils.processScreenshot(screenshot, targetColors, ringIndicatorFile)) {
      log.info("Shiny target found! Terminating hunt for: {}", targetName);
      shouldEndHunt = true;
    }
  }

  /**
   * Executes the fight sequence including battle actions and post-fight delay
   * @param sleepDuration time to wait after completing fight actions
   * @throws InterruptedException if the sleep operation is interrupted
   */
  private void executeFightSequence(final Long sleepDuration) throws InterruptedException {
    robotUtils.fight();
    robotUtils.sleep(sleepDuration);
  }

  /**
   * Executes the healing sequence by loading and performing heal actions
   * @param cityName the city where healing should be performed
   * @throws InterruptedException if action execution is interrupted
   * @throws IOException if there's an error reading the heal configuration file
   */
  private void executeHealingSequence(final String cityName) throws InterruptedException, IOException {
    File healConfigFile = fileUtils.getHealFile(cityName);
    HealAction healActions = objectMapper.readValue(healConfigFile, HealAction.class);
    robotUtils.executeActions(healActions.getSteps());
  }
}