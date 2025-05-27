package es.zed.hunt.domain.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealAction {

  private List<ActionTime> steps;
}
