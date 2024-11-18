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

  public static final List<int[]> FIGHT_COLOR = List.of(new int[]{79, 66, 60}, new int[]{143, 111, 71},
      new int[]{133, 102, 65}, new int[] {123, 94, 60}, new int[] {118, 86, 48}, new int[] {107, 73, 42},
      new int[]{104, 74, 43}, new int[]{78, 69, 57}, new int[]{99, 82, 77}, new int[]{112, 94, 73},
      new int[]{133, 111, 71});

  public static final int THRESHOLD = 2;
}
