package es.zed.autoclick.domain.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutoClick {

	private String title;

	private Mode mode;

	private long interval;

	private int count;

	private Mouse mouse;

  private Map<Integer, Integer> delays = new HashMap<Integer, Integer>() {{
    put(0, null);
  }};

  private SpeedMode SpeedMode;
}
