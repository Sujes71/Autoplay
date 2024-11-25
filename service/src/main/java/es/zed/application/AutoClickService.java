package es.zed.application;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.sun.jna.platform.win32.WinDef;
import es.zed.domain.input.AutoClickInputPort;
import es.zed.domain.input.User32;
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
  private NativeKeyListener currentKeyListener;
  private NativeMouseListener currentMouseListener;
  private static boolean isListenerInitialized;

  public AutoClickService(RobotManager robotManager, ScreenManager screenManager) {
    this.robotManager = robotManager;
    this.screenManager = screenManager;
    this.isActive = false;
    this.savedCoordinates = null;
    this.relativeCoordinates = null;
    this.currentKeyListener = null;
    this.currentMouseListener = null;
    this.remainingClicks = new AtomicInteger(0);

    initializeGlobalScreenOnce();
  }

  @Override
  public void start(final AutoClickRequestDto requestDto) {
    if (requestDto == null) {
      log.error("AutoClickRequestDto is null");
      return;
    }
    initializeKeyListener(requestDto);
    initializeMouseListener(requestDto);
  }

  private void startClick(final AutoClickRequestDto requestDto, final int count, final long interval) throws InterruptedException {
    if (!screenManager.isSpecificWindowOpen(requestDto.getTitle())) {
      log.error("Specified window not found: {}", requestDto.getTitle());
      return;
    }
    if (!isActive) return;
    if (Constants.MOUSE_EVENT.equals(requestDto.getMode())) {
      executeMouseEvents(requestDto, count, interval);
    } else if (Constants.SEND_MESSAGE.equals(requestDto.getMode())) {
      executeSendMessage(requestDto, count, interval);
    } else {
      log.error("Invalid mode: {}", requestDto.getMode());
    }
  }

  private void executeMouseEvents(AutoClickRequestDto requestDto, int count, long interval) throws InterruptedException {
    remainingClicks.set(count);
    while (remainingClicks.get() > 0 && isActive) {
      Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();

      robotManager.mouseMove(savedCoordinates.x, savedCoordinates.y);
      performMouseClick();
      robotManager.mouseMove(currentMousePosition.x, currentMousePosition.y);

      robotManager.sleep(interval);
      remainingClicks.decrementAndGet();
    }
  }

  private void executeSendMessage(AutoClickRequestDto requestDto, int count, long interval) throws InterruptedException {
    User32 user32 = User32.INSTANCE;
    WinDef.HWND hwnd = user32.FindWindowA(null, requestDto.getTitle());

    if (hwnd == null) {
      log.error("Window not found: {}", requestDto.getTitle());
      return;
    }

    remainingClicks.set(count);

    while (remainingClicks.get() > 0 && isActive) {
      int lParamValue = (relativeCoordinates.y << 16) | (relativeCoordinates.x & 0xFFFF);
      WinDef.LPARAM lParam = new WinDef.LPARAM(lParamValue);

      user32.SendMessageA(hwnd, Constants.WM_LBUTTONDOWN, new WinDef.WPARAM(0), lParam);
      robotManager.sleep(interval);
      user32.SendMessageA(hwnd, Constants.WM_LBUTTONUP, new WinDef.WPARAM(0), lParam);
      remainingClicks.decrementAndGet();
    }
  }

  private void performMouseClick() {
    robotManager.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    robotManager.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
  }

  private synchronized void initializeGlobalScreenOnce() {
    try {
      if (!isListenerInitialized) {
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        GlobalScreen.registerNativeHook();
        isListenerInitialized = true;
        log.info("GlobalScreen initialized successfully.");
      }
    } catch (Exception e) {
      log.error("Failed to initialize GlobalScreen: {}", e.getMessage());
    }
  }

  private void initializeKeyListener(final AutoClickRequestDto requestDto) {
    if (currentKeyListener != null) {
      log.info("Removing existing NativeKeyListener.");
      GlobalScreen.removeNativeKeyListener(currentKeyListener);
    }
    currentKeyListener = new NativeKeyListener() {
      @Override
      public void nativeKeyPressed(NativeKeyEvent e) {
        try {
          switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_F3 -> saveMouseCoordinates();
            case NativeKeyEvent.VC_F1 -> activateAutoClick(requestDto, requestDto.getCount(), requestDto.getInterval());
            case NativeKeyEvent.VC_F2 -> deactivateAutoClick();
            default -> log.error("Invalid key: {}", e.getKeyCode());
          }
        } catch (InterruptedException ex) {
          log.error("Error during key press handling: {}", ex.getMessage());
        }
      }
    };
    GlobalScreen.addNativeKeyListener(currentKeyListener);
    log.info("NativeKeyListener registered successfully.");
  }

  private void initializeMouseListener(AutoClickRequestDto requestDto) {
    if (currentMouseListener != null) {
      log.info("Removing existing NativeMouseListener.");
      GlobalScreen.removeNativeMouseListener(currentMouseListener);
    }
    currentMouseListener = new NativeMouseListener() {
      @Override
      public void nativeMouseClicked(NativeMouseEvent e) {
        try {
          if (requestDto.getMouse().isActivated()) {
            switch (e.getButton()) {
              case NativeMouseEvent.BUTTON1 -> {
                robotManager.sleep(requestDto.getMouse().getDelay());
                activateAutoClick(requestDto, requestDto.getMouse().getCount(), requestDto.getMouse().getInterval());
              }
              default -> log.error("Invalid mouse key: {}", e.getButton());
            }
          }
        } catch (InterruptedException ex) {
          log.error("Error during mouse press handling: {}", ex.getMessage());
        }
      }
    };
    GlobalScreen.addNativeMouseListener(currentMouseListener);
    log.info("NativeMouseListener registered successfully.");
  }

  private void saveMouseCoordinates() {
    Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();
    savedCoordinates = new Point(currentMousePosition.x, currentMousePosition.y);
    int[] relativeCoordinatesArray = getWindowCoordinates();
    relativeCoordinates = new Point(relativeCoordinatesArray[0], relativeCoordinatesArray[1]);
  }

  private void activateAutoClick(final AutoClickRequestDto requestDto, final int count, final long interval) throws InterruptedException {
    if (Objects.isNull(savedCoordinates)) {
      log.error("Coordinates are not set. Press F3 to save coordinates.");
      return;
    }
    isActive = true;
    log.info("AutoClick activated!");
    startClick(requestDto, count, interval);
  }

  private void deactivateAutoClick() {
    isActive = false;
    log.info("AutoClick deactivated!");
  }

  private int[] getWindowCoordinates() {
    User32 user32 = User32.INSTANCE;
    WinDef.HWND foregroundWindow = user32.GetForegroundWindow();
    if (foregroundWindow == null) {
      log.error("No foreground window found.");
      return new int[]{0, 0};
    }

    User32.RECT windowRect = new User32.RECT();
    if (!user32.GetWindowRect(foregroundWindow, windowRect)) {
      log.error("Failed to get window rect.");
      return new int[]{0, 0};
    }

    User32.POINT cursorPos = new User32.POINT();
    if (!user32.GetCursorPos(cursorPos)) {
      log.error("Failed to get cursor position.");
      return new int[]{0, 0};
    }

    int relativeX = cursorPos.x - windowRect.left;
    int relativeY = cursorPos.y - windowRect.top - 40;

    if (relativeX < 0 || relativeY < 0 || relativeX > (windowRect.right - windowRect.left) || relativeY > (windowRect.bottom - windowRect.top)) {
      log.error("Cursor is outside the window's bounds.");
      return new int[]{0, 0};
    }

    log.info("Relative Coordinates in Foreground Window: X={}, Y={}", relativeX, relativeY);
    return new int[]{relativeX, relativeY};
  }
}
