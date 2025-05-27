package es.zed.autoclick.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Move {

  private boolean active;

  private long interval;
}
