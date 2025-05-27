package es.zed.api.autoclick.rest;

import static es.zed.shared.rest.Routing.AUTOCLICK_START_PATH;
import static es.zed.shared.rest.Routing.DEFAULT_MAPPING;

import es.zed.api.autoclick.domain.port.inbound.ActivateAutoClickUseCase;
import es.zed.autoclick.domain.model.AutoClick;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DEFAULT_MAPPING)
public class AutoClickController {

	private final ActivateAutoClickUseCase activateAutoClickUseCase;


	public AutoClickController(ActivateAutoClickUseCase activateAutoClickUseCase) {
		this.activateAutoClickUseCase = activateAutoClickUseCase;
	}

	@PostMapping(AUTOCLICK_START_PATH)
	public void start(@RequestBody final AutoClick autoClick) {
		activateAutoClickUseCase.execute(autoClick);
	}
}
