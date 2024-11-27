package es.zed.infrastructure;

import java.util.List;

public class Constants {

  public static final String DEFAULT_MAPPING = "/api";

  public static final String HUNT_PATH = "/hunt";

  public static final String START_PATH = "/start";

  public static final String HUNT_STOP_PATH = HUNT_PATH + "/stop";

  public static final String NAME_PARAMETER = "/{name}";

  public static final String HUNT_START_NAME_PATH = HUNT_PATH + START_PATH + NAME_PARAMETER;

  public static final String AUTOCLICK_PATH = "/autoclick";

  public static final String AUTOCLICK_START_PATH = AUTOCLICK_PATH + START_PATH;

  public static final String LEFT = "LEFT";

  public static final String RIGHT = "RIGHT";

  public static final String UP = "UP";

  public static final String DOWN = "DOWN";

  public static final String SEVEN = "7";

  public static final String SIX = "6";

  public static final String ONE = "1";

  public static final String ZETA = "Z";

  public static final String ESCAPE = "escape";

  public static final String FIGHT = "fight";

  public static final String MOUSE_EVENT = "mouse_event";

  public static final String SEND_MESSAGE = "send_message";

  public static final int WM_LBUTTONDOWN = 0x0201;

  public static final int WM_LBUTTONUP = 0x0202;

  public static final List<int[]> FIGHT_COLOR = List.of(new int[] {49, 49, 49});

  public static final int THRESHOLD = 0;
}
