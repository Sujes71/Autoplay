package es.zed.infrastructure.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExceptionManager {

  public void handleException(Exception e) {
    log.error("ERROR: {}", e.getMessage());
    Thread.currentThread().interrupt();
  }

}
