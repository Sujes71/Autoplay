package es.zed.autoclick.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutoClick {

	private String title;

	private boolean activated;

	private String mode;

	private long interval;

	private int count;

	private Mouse mouse;

	private Move move;
}
