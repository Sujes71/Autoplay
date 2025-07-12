package es.zed.autoclick.domain.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Mouse {

  private long interval;

  private int count;
}
