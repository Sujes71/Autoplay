package es.zed.infrastructure.controller;

import es.zed.domain.RespModel;
import es.zed.domain.input.AutoClickInputPort;
import es.zed.domain.output.request.AutoClickRequestDto;
import es.zed.infrastructure.Constants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.DEFAULT_MAPPING)
public class AutoClickController {

  private final AutoClickInputPort autoclickInputPort;


  public AutoClickController(AutoClickInputPort autoclickInputPort) {
    this.autoclickInputPort = autoclickInputPort;
  }

  @PostMapping(Constants.AUTOCLICK_START_PATH)
  public RespModel<String> start(@RequestBody final AutoClickRequestDto requestDto) {
    autoclickInputPort.start(requestDto);
    return RespModel.<String>builder()
        .data(null)
        .message("AutoClick started!")
        .build();
  }
}
