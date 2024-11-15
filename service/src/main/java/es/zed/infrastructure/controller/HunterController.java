package es.zed.infrastructure.controller;

import es.zed.domain.RespModel;
import es.zed.domain.input.HuntInputPort;
import es.zed.domain.output.request.HuntRequestBody;
import es.zed.infrastructure.Constants;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.DEFAULT_MAPPING)
public class HunterController {
  private final HuntInputPort huntInputPort;

  public HunterController(HuntInputPort huntInputPort) {
    this.huntInputPort = huntInputPort;
  }

  @PostMapping(Constants.POST_HUNT_START_NAME_PATH)
  public RespModel<String> startHunt(@PathVariable String name, @RequestBody HuntRequestBody requestBody) {
    new Thread(() -> huntInputPort.startHunt(name, requestBody)).start();

    return RespModel.<String>builder()
        .data(null)
        .message("Hunt started!")
        .build();
  }

  @PostMapping(Constants.POST_HUNT_STOP_PATH)
  public RespModel<String> stopHunt() {
    huntInputPort.stopHunt();

    return RespModel.<String>builder()
        .data(null)
        .message("Hunt stopped!")
        .build();
  }

}
