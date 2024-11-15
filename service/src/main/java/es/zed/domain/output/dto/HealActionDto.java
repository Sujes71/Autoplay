package es.zed.domain.output.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealActionDto {

  private List<ActionTimeDto> steps;
}
