package es.zed.shared.domain.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BoomBangOptimizerUtils {

  public void optimizeBoomBangProcesses() {
    try {
      log.info("Optimizing BoomBang processes...");

      setBoomBangHighPriority();
      setBoomBangCpuAffinity();

      log.info("BoomBang processes optimized successfully");

    } catch (Exception e) {
      log.error("Error optimizing BoomBang processes: {}", e.getMessage());
    }
  }

  private void setBoomBangHighPriority() throws IOException, InterruptedException {
    log.info("Setting BoomBang processes to high priority...");

    ProcessBuilder pb1 = new ProcessBuilder();
    pb1.command("powershell.exe", "-Command",
        "Get-Process | Where-Object {$_.ProcessName -like '*BoomBang*'} | ForEach-Object {$_.PriorityClass = 'High'}");

    Process priorityProcess1 = pb1.start();
    boolean finished1 = priorityProcess1.waitFor(30, TimeUnit.SECONDS);

    if (!finished1) {
      priorityProcess1.destroyForcibly();
    }

    log.info("BoomBang processes priority set to high");
  }

  private void setBoomBangCpuAffinity() throws IOException, InterruptedException {
    log.info("Setting BoomBang CPU affinity...");

    ProcessBuilder pb = new ProcessBuilder();
    pb.command("powershell.exe", "-Command",
        "Get-Process | Where-Object {$_.ProcessName -like '*BoomBang*'} | ForEach-Object {$_.ProcessorAffinity = 63}");

    Process affinityProcess = pb.start();
    boolean finished = affinityProcess.waitFor(30, TimeUnit.SECONDS);

    if (!finished) {
      affinityProcess.destroyForcibly();
    }

    log.info("BoomBang CPU affinity set to cores 0-5 for all BoomBang processes");
  }

  public void optimizeNetworkForBoomBang() {
    try {
      log.info("Optimizing network settings for BoomBang...");

      ProcessBuilder pb1 = new ProcessBuilder();
      pb1.command("cmd.exe", "/c", "ipconfig /flushdns");
      Process flushDns = pb1.start();
      boolean finished1 = flushDns.waitFor(15, TimeUnit.SECONDS);

      if (!finished1) {
        log.warn("DNS flush timeout, terminating process");
        flushDns.destroyForcibly();
      } else {
        log.info("DNS cache flushed successfully");
      }

      ProcessBuilder pb3 = new ProcessBuilder();
      pb3.command("cmd.exe", "/c", "netsh int tcp set global chimney=enabled");
      Process chimneyEnable = pb3.start();
      boolean finished3 = chimneyEnable.waitFor(15, TimeUnit.SECONDS);

      if (!finished3) {
        log.warn("Chimney enable timeout, terminating process");
        chimneyEnable.destroyForcibly();
      } else {
        log.info("Chimney offload enabled");
      }

      ProcessBuilder pb4 = new ProcessBuilder();
      pb4.command("cmd.exe", "/c", "netsh int tcp set global rss=enabled");
      Process rssEnable = pb4.start();
      boolean finished4 = rssEnable.waitFor(15, TimeUnit.SECONDS);

      if (!finished4) {
        log.warn("RSS enable timeout, terminating process");
        rssEnable.destroyForcibly();
      } else {
        log.info("RSS (Receive Side Scaling) enabled");
      }

      ProcessBuilder pb5 = new ProcessBuilder();
      pb5.command("cmd.exe", "/c", "netsh int tcp set global netdma=enabled");
      Process netdmaEnable = pb5.start();
      boolean finished5 = netdmaEnable.waitFor(15, TimeUnit.SECONDS);

      if (!finished5) {
        log.warn("NetDMA enable timeout, terminating process");
        netdmaEnable.destroyForcibly();
      } else {
        log.info("NetDMA enabled");
      }

      ProcessBuilder pb6 = new ProcessBuilder();
      pb6.command("cmd.exe", "/c", "netsh int udp set global uro=enabled");
      Process uroEnable = pb6.start();
      boolean finished6 = uroEnable.waitFor(15, TimeUnit.SECONDS);

      if (!finished6) {
        log.warn("URO enable timeout, terminating process");
        uroEnable.destroyForcibly();
      } else {
        log.info("URO (UDP Receive Offload) enabled");
      }

      ProcessBuilder pb7 = new ProcessBuilder();
      pb7.command("cmd.exe", "/c", "netsh int tcp set global timestamps=enabled");
      Process timestampsEnable = pb7.start();
      boolean finished7 = timestampsEnable.waitFor(15, TimeUnit.SECONDS);

      if (!finished7) {
        log.warn("TCP timestamps timeout, terminating process");
        timestampsEnable.destroyForcibly();
      } else {
        log.info("TCP timestamps enabled");
      }

      log.info("Advanced network optimization completed for BoomBang gaming");

    } catch (Exception e) {
      log.error("Error optimizing network: {}", e.getMessage(), e);
    }
  }
}