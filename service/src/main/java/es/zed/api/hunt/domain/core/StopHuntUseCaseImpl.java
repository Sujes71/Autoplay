package es.zed.api.hunt.domain.core;

import es.zed.api.hunt.domain.port.inbound.StopHuntUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StopHuntUseCaseImpl implements StopHuntUseCase {

	@Override
	public void execute() {
		ActivateHuntUseCaseImpl.shouldEndHunt = true;
	}
}
