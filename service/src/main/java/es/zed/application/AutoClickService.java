package es.zed.application;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import es.zed.domain.input.AutoClickInputPort;
import es.zed.domain.output.request.AutoClickRequestDto;
import es.zed.infrastructure.Constants;
import es.zed.infrastructure.manager.RobotManager;
import es.zed.infrastructure.manager.ScreenManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AutoClickService implements AutoClickInputPort {

  private final RobotManager robotManager;
  private final ScreenManager screenManager;

  private volatile boolean isActive;
  private final AtomicInteger remainingClicks;
  private Point savedCoordinates;
  private Point relativeCoordinates;

  public AutoClickService(RobotManager robotManager, ScreenManager screenManager) {
    this.robotManager = robotManager;
    this.screenManager = screenManager;
    this.isActive = false;
    this.savedCoordinates = null;
    this.relativeCoordinates = null;
    this.remainingClicks = new AtomicInteger(0);
  }

  @Override
  public void start(final AutoClickRequestDto requestDto) {
    if (requestDto == null) {
      log.error("AutoClickRequestDto is null");
      return;
    }
    initializeKeyListener(requestDto);
  }

  private void startClick(final AutoClickRequestDto requestDto) throws InterruptedException {
    if (!isActive) return;

    if (Constants.MOUSE_EVENT.equals(requestDto.getMode())) {
      executeMouseEvents(requestDto);
    } else {
      log.error("Invalid mode: {}", requestDto.getMode());
    }
  }

  private void executeMouseEvents(AutoClickRequestDto requestDto) throws InterruptedException {
    if (!screenManager.isSpecificWindowOpen(requestDto.getTitle())) {
      log.error("Specified window not found: {}", requestDto.getTitle());
      return;
    }

    remainingClicks.set(requestDto.getCount());
    while (remainingClicks.get() > 0 && isActive) {
      Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();

      robotManager.mouseMove(savedCoordinates.x, savedCoordinates.y);
      performMouseClick();
      robotManager.mouseMove(currentMousePosition.x, currentMousePosition.y);

      robotManager.sleep(requestDto.getInterval());
      remainingClicks.decrementAndGet();
    }
  }

  private void performMouseClick() {
    robotManager.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    robotManager.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }

  private void initializeKeyListener(final AutoClickRequestDto requestDto) {
    try {
      Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
      GlobalScreen.registerNativeHook();

      GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
          try {
            switch (e.getKeyCode()) {
              case NativeKeyEvent.VC_F3 -> saveMouseCoordinates();
              case NativeKeyEvent.VC_F1 -> activateAutoClick(requestDto);
              case NativeKeyEvent.VC_F2 -> deactivateAutoClick();
              default -> log.error("Invalid key: {}", e.getKeyCode());
            }
          } catch (InterruptedException ex) {
            log.error("Error during key press handling: {}", ex.getMessage());
          }
        }
      });
    } catch (Exception e) {
      log.error("Failed to initialize key listener: {}", e.getMessage());
    }
  }

  private void saveMouseCoordinates() {
    User32 user32 = User32.INSTANCE;
    Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();
    savedCoordinates = new Point(currentMousePosition.x, currentMousePosition.y);

    WinDef.HWND hwnd = user32.GetForegroundWindow();
    if (hwnd != null) {
      WinDef.RECT windowRect = new WinDef.RECT();
      if (user32.GetWindowRect(hwnd, windowRect)) {
        int relativeX = currentMousePosition.x - windowRect.left;
        int relativeY = currentMousePosition.y - windowRect.top;
        relativeCoordinates = new Point(relativeX, relativeY);

        log.info("Coordinates saved: Absolute X={}, Y={} | Relative X={}, Y={}",
            savedCoordinates.x, savedCoordinates.y, relativeX, relativeY);
      }
    } else {
      log.error("No active window found.");
    }
  }

  private void activateAutoClick(final AutoClickRequestDto requestDto) throws InterruptedException {
    if (Objects.isNull(savedCoordinates) || Objects.isNull(relativeCoordinates)) {
      log.error("Coordinates are not set. Press F3 to save coordinates.");
      return;
    }
    isActive = true;
    log.info("AutoClick activated!");
    startClick(requestDto);
  }

  private void deactivateAutoClick() {
    isActive = false;
    log.info("AutoClick deactivated!");
  }
}