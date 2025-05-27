package es.zed.api.hunt.domain.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.zed.api.hunt.domain.port.inbound.ActivateHuntUseCase;
import es.zed.hunt.domain.model.HealAction;
import es.zed.hunt.domain.model.Hunt;
import es.zed.shared.Constants;
import es.zed.shared.domain.utils.FileUtils;
import es.zed.shared.domain.utils.RobotUtils;
import es.zed.shared.domain.utils.ScreenUtils;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivateHuntUseCaseImpl implements ActivateHuntUseCase {

	private final RobotUtils robotUtils;
	private final ScreenUtils screenUtils;
	private final FileUtils fileUtils;
	private final ObjectMapper objectMapper;

	public static volatile boolean end;

	public ActivateHuntUseCaseImpl(RobotUtils robotUtils, ScreenUtils screenUtils, FileUtils fileUtils,
		ObjectMapper objectMapper) {
		this.robotUtils = robotUtils;
		this.screenUtils = screenUtils;
		this.fileUtils = fileUtils;
		this.objectMapper = objectMapper;
	}

	@Override
	public void execute(Hunt input) {
		try {
			end = false;
			String name = "";
			Thread actionThread = new Thread(() -> manageAction(input, Thread.currentThread()));
			actionThread.start();

			List<int[]> targetColors = fileUtils.asignShinyToFind(name);
			File ringFile = fileUtils.getRingFile();
			Rectangle captureRect = screenUtils.getCaptureRectangle(985, 240, 600, 260);
			while (!end) {
				BufferedImage screenshot = robotUtils.captureScreenshot(captureRect);
				if (screenUtils.processScreenshot(screenshot, targetColors, ringFile)) {
					actionThread.interrupt();
					break;
				}
			}
			if (!actionThread.isInterrupted()) {
				actionThread.interrupt();
			}
			log.info("Finalized!");
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	private void manageAction(final Hunt hunt, final Thread currentThread) {
		int fightCount = 0;
		while (!currentThread.isInterrupted()) {
			if (screenUtils.isSpecificWindowOpen(hunt.getTitle())) {
				try {
					if (Objects.nonNull(hunt.getSteps())) {
						robotUtils.executeActions(hunt.getSteps());
					}
					Rectangle captureRect = screenUtils.getCaptureRectangle(1000, 400, 400, 100);
					BufferedImage screenshot = robotUtils.captureScreenshot(captureRect);

					if (screenUtils.isFight(screenshot)) {
						switch (hunt.getMode()) {
							case Constants.ESCAPE -> robotUtils.scape();
							case Constants.FIGHT -> {
								robotUtils.fight();
								robotUtils.sleep(hunt.getTime());
								fightCount++;
							}
							default -> throw new IllegalArgumentException("Invalid mode: " + hunt.getMode());
						}
					}
					if (Objects.nonNull(hunt.getHeal()) && fightCount == hunt.getHeal().getCount()) {
						File file = fileUtils.getHealFile(hunt.getHeal().getCity());
						HealAction healActions = objectMapper.readValue(file, HealAction.class);
						robotUtils.executeActions(healActions.getSteps());
						fightCount = 0;
					}
				} catch (InterruptedException | IOException e) {
					log.error("ERROR: {}", e.getMessage());
					currentThread.interrupt();
				}
			}
		}
	}
}
