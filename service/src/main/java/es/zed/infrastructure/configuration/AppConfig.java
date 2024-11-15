package es.zed.infrastructure.configuration;

import java.awt.AWTException;
import java.awt.Robot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  static {
    System.setProperty("java.awt.headless", "false");
  }

  @Bean
  public Robot robot() throws AWTException {
    return new Robot();
  }
}
