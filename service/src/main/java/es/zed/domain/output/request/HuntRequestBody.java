package es.zed.domain.output.request;

import es.zed.domain.output.dto.ActionTimeDto;
import es.zed.domain.output.dto.HealDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HuntRequestBody {

  private String title;

  private String mode;

  private long time;

  private HealDto heal;

  private List<ActionTimeDto> steps;

}
