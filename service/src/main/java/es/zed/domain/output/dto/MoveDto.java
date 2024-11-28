package es.zed.domain.output.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MoveDto {

  private boolean active;

  private long interval;
}
