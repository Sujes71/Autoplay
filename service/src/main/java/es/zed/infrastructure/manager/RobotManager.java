package es.zed.infrastructure.manager;

import es.zed.domain.output.dto.ActionTimeDto;
import es.zed.infrastructure.Constants;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RobotManager {

  public final Robot robot;

  public RobotManager(Robot robot) {
    this.robot = robot;
  }

  public void executeActions(final List<ActionTimeDto> actionTimes) throws InterruptedException {
    for (ActionTimeDto actionTime : actionTimes) {
      handleKey(actionTime.getAction(), Boolean.TRUE);
      sleep(actionTime.getTime());
      handleKey(actionTime.getAction(), Boolean.FALSE);
      sleep(100);
    }
  }

  public void sleep(final long sleep) throws InterruptedException {
    Thread.sleep(sleep);
  }

  public BufferedImage captureScreenshot(Rectangle captureRect) {
    return robot.createScreenCapture(captureRect);
  }

  public void scape() {
    click(500, 600);
    click(500, 600);
  }

  public void fight() {
    click(350, 550);
    click(350, 550);
    click(350, 550);
  }

  private void handleKey(String direction, boolean press) {
    int keyCode;
    switch (direction) {
      case Constants.LEFT -> keyCode = KeyEvent.VK_LEFT;
      case Constants.RIGHT -> keyCode = KeyEvent.VK_RIGHT;
      case Constants.UP -> keyCode = KeyEvent.VK_UP;
      case Constants.DOWN -> keyCode = KeyEvent.VK_DOWN;
      case Constants.ZETA -> keyCode = KeyEvent.VK_Z;
      case Constants.ONE -> keyCode = KeyEvent.VK_1;
      case Constants.SIX -> keyCode = KeyEvent.VK_6;
      case Constants.SEVEN -> keyCode = KeyEvent.VK_7;
      default -> throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    if (press) {
      robot.keyPress(keyCode);
    } else {
      robot.keyRelease(keyCode);
    }
  }

  private void click(int x, int y) {
    robot.mouseMove(x, y);
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }

}
