package es.zed.domain.output.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MouseDto {

  private boolean activated;

  private long interval;

  private int count;

  private long delay;
}
