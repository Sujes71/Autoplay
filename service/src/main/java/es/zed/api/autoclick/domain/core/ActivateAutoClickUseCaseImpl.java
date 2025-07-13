package es.zed.api.autoclick.domain.core;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.sun.jna.platform.win32.WinDef;
import es.zed.api.autoclick.domain.port.inbound.ActivateAutoClickUseCase;
import es.zed.autoclick.domain.model.AutoClick;
import es.zed.autoclick.domain.model.Mode;
import es.zed.autoclick.domain.model.User32;
import es.zed.shared.Constants;
import es.zed.shared.domain.utils.RobotUtils;
import es.zed.shared.domain.utils.ScreenUtils;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivateAutoClickUseCaseImpl implements ActivateAutoClickUseCase {

	private final RobotUtils robotUtils;
	private final ScreenUtils screenUtils;

	private volatile boolean isActive;
	private final AtomicInteger remainingClicks;
	private Point savedCoordinates;
	private Point relativeCoordinates;
	private NativeKeyListener currentKeyListener;
	private NativeMouseInputListener currentMouseListener;
	private static boolean isListenerInitialized;

public ActivateAutoClickUseCaseImpl(RobotUtils robotUtils, ScreenUtils screenUtils) {
		this.robotUtils = robotUtils;
		this.screenUtils = screenUtils;
		this.isActive = false;
		this.savedCoordinates = null;
		this.relativeCoordinates = null;
		this.currentKeyListener = null;
		this.currentMouseListener = null;
		this.remainingClicks = new AtomicInteger(0);
    initializeGlobalScreenOnce();
	}

	@Override
	public void execute(AutoClick input) {
		if (input == null) {
			log.error("input is null");
			return;
		}
		initializeKeyListener(input);
		initializeMouseListener(input);
	}

	private void startClick(final AutoClick autoClick, final int count, final long interval) throws InterruptedException {
		if (!screenUtils.isSpecificWindowOpen(autoClick.getTitle())) {
			log.error("Specified window not found: {}", autoClick.getTitle());
			return;
		}
		executeSendMessage(autoClick, count, interval);
	}

	private void executeSendMessage(final AutoClick autoClick, int count, long interval) throws InterruptedException {
		User32 user32 = User32.INSTANCE;
		WinDef.HWND hwnd = user32.FindWindowA(null, autoClick.getTitle());

		if (hwnd == null) {
			log.error("Window not found: {}", autoClick.getTitle());
			return;
		}

		remainingClicks.set(count);

		while (remainingClicks.get() > 0 && isActive) {
			int lParamValue = (relativeCoordinates.y << 16) | (relativeCoordinates.x & 0xFFFF);
			WinDef.LPARAM lParam = new WinDef.LPARAM(lParamValue);

			user32.SendMessageA(hwnd, Constants.WM_LBUTTONDOWN, new WinDef.WPARAM(0), lParam);
			user32.SendMessageA(hwnd, Constants.WM_LBUTTONUP, new WinDef.WPARAM(0), lParam);

			robotUtils.sleep(interval);
			remainingClicks.decrementAndGet();
		}
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

	private void initializeKeyListener(final AutoClick autoClick) {
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
              case NativeKeyEvent.VC_F1 -> {
                isActive = true;
                if (autoClick.getMode() == Mode.KEY || autoClick.getMode() == Mode.MIX) {
                  activateAutoClick(autoClick, autoClick.getCount(),
                      autoClick.getInterval());
                }
              }
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

	private void initializeMouseListener(final AutoClick autoClick) {
		if (currentMouseListener != null) {
			log.info("Removing existing NativeMouseListener.");
			GlobalScreen.removeNativeMouseListener(currentMouseListener);
		}
		if (autoClick.getMode() == Mode.MOUSE || autoClick.getMode() == Mode.MIX) {
			currentMouseListener = new NativeMouseInputListener() {
				@Override
				public void nativeMousePressed(NativeMouseEvent e) {
					try {
						activateAutoClick(autoClick, autoClick.getMouse().getCount(), autoClick.getMouse().getInterval());
					} catch (InterruptedException ex) {
						log.error("Error during mouse press handling: {}", ex.getMessage());
					}
				}
			};
			GlobalScreen.addNativeMouseListener(currentMouseListener);
			log.info("NativeMouseInputListener registered successfully.");
		}
	}

	private void saveMouseCoordinates() {
		Point currentMousePosition = MouseInfo.getPointerInfo().getLocation();
		savedCoordinates = new Point(currentMousePosition.x, currentMousePosition.y);
		int[] relativeCoordinatesArray = getWindowCoordinates();
		relativeCoordinates = new Point(relativeCoordinatesArray[0], relativeCoordinatesArray[1]);
	}

	private void activateAutoClick(final AutoClick autoClick, final int count, final long interval) throws InterruptedException {
		if (Objects.isNull(savedCoordinates)) {
			log.error("Coordinates are not set. Press F3 to save coordinates.");
			return;
		}
		log.info("AutoClick activated!");
		if (isActive) {
			for (int i = 0; i < autoClick.getDelays().length; i++) {
				robotUtils.sleeps(autoClick.getDelays(), i);
				startClick(autoClick, count, interval);
			}
		}
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
