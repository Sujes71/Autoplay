package es.zed.domain.output.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutoClickRequestDto {

  private String title;

  private String mode;

  private long interval;

  private int count;

}
