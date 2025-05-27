package es.zed.shared.rest;

public class Routing {

  public static final String DEFAULT_MAPPING = "/api";

  public static final String HUNT_PATH = "/hunt";

  public static final String START_PATH = "/start";

  public static final String HUNT_STOP_PATH = HUNT_PATH + "/stop";

  public static final String NAME_PARAMETER = "/{name}";

  public static final String HUNT_START_NAME_PATH = HUNT_PATH + START_PATH + NAME_PARAMETER;

  public static final String AUTOCLICK_PATH = "/autoclick";

  public static final String AUTOCLICK_START_PATH = AUTOCLICK_PATH + START_PATH;
}