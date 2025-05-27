package es.zed.api.hunt.rest;

import static es.zed.shared.rest.Routing.DEFAULT_MAPPING;
import static es.zed.shared.rest.Routing.HUNT_START_NAME_PATH;
import static es.zed.shared.rest.Routing.HUNT_STOP_PATH;

import es.zed.api.hunt.domain.port.inbound.ActivateHuntUseCase;
import es.zed.api.hunt.domain.port.inbound.StopHuntUseCase;
import es.zed.hunt.domain.model.Hunt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DEFAULT_MAPPING)
public class HuntController {
  private final ActivateHuntUseCase activateHuntUseCase;
  private final StopHuntUseCase stopHuntUseCase;

  public HuntController(ActivateHuntUseCase activateHuntUseCase, StopHuntUseCase stopHuntUseCase) {
    this.activateHuntUseCase = activateHuntUseCase;
	  this.stopHuntUseCase = stopHuntUseCase;
  }

  @PostMapping(HUNT_START_NAME_PATH)
  public void start(@PathVariable final String name, @RequestBody final Hunt hunt) {
    new Thread(() -> activateHuntUseCase.execute(hunt)).start();
  }

  @PostMapping(HUNT_STOP_PATH)
  public void stop() {
    stopHuntUseCase.execute();
  }
}