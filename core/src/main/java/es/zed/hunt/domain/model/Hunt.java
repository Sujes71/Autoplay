package es.zed.hunt.domain.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Hunt {

  private String title;

  private String mode;

  private long time;

  private Heal heal;

  private List<ActionTime> steps;

}
