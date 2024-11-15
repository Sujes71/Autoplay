package es.zed.infrastructure;

import java.util.List;

public class Constants {

  public static final String DEFAULT_MAPPING = "/api";

  public static final String POST_HUNT_PATH = "/hunt";

  public static final String POST_HUNT_START_PATH = POST_HUNT_PATH + "/start";

  public static final String POST_HUNT_STOP_PATH = POST_HUNT_PATH + "/stop";

  public static final String NAME_PARAMETER = "/{name}";

  public static final String POST_HUNT_START_NAME_PATH = POST_HUNT_START_PATH + NAME_PARAMETER;

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

  public static final List<int[]> FIGHT_COLOR = List.of(new int[]{79, 66, 60});

  public static final int THRESHOLD = 2;
}
