package es.zed.domain.input;

import es.zed.domain.output.request.AutoClickRequestDto;

public interface AutoClickInputPort {

  void start(final AutoClickRequestDto requestDto);
}
