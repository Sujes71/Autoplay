package es.zed.autoclick.domain.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutoClick {

	private String title;

	private Mode mode;

	private long interval;

	private Mouse mouse;

  private List<DelayClick> delayClicks;

  private SpeedMode SpeedMode;
}
