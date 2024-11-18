package es.zed.domain.input;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinDef;
import java.util.List;

public interface User32 extends StdCallLibrary {
  User32 INSTANCE = Native.load("user32", User32.class);

  HWND FindWindowA(String className, String windowName);
  WinDef.LRESULT SendMessageA(WinDef.HWND hWnd, int Msg, WinDef.WPARAM wParam, WinDef.LPARAM lParam);
  boolean GetCursorPos(POINT lpPoint);
  WinDef.HWND GetForegroundWindow();
  boolean GetWindowRect(WinDef.HWND hWnd, RECT rect);

  class POINT extends Structure {
    public int x;
    public int y;

    @Override
    protected List<String> getFieldOrder() {
      return List.of("x", "y");
    }
  }

  class RECT extends Structure {
    public int left, top, right, bottom;

    @Override
    protected List<String> getFieldOrder() {
      return List.of("left", "top", "right", "bottom");
    }
  }
}