package es.zed.domain.output.request;

import es.zed.domain.output.dto.MouseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutoClickRequestDto {

  private String title;

  private String mode;

  private long interval;

  private int count;

  private MouseDto mouse;

}
