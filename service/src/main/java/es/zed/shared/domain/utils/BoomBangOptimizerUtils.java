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

      cleanupDuplicateProcesses();
      setBoomBangHighPriority();
      setBoomBangCpuAffinity();

      log.info("BoomBang processes optimized successfully");

    } catch (Exception e) {
      log.error("Error optimizing BoomBang processes: {}", e.getMessage());
    }
  }

  public void cleanupDuplicateProcesses() throws IOException, InterruptedException {
    log.info("Cleaning up duplicate BoomBang processes...");

    // Procesar BoomBangLauncher.exe
    Process listProcess1 = Runtime.getRuntime().exec(
        "wmic process where \"name='BoomBangLauncher.exe'\" get ProcessId,PageFileUsage"
    );
    listProcess1.waitFor();

    String complexCommand1 = "for /f \"skip=1 tokens=1,2\" %i in ('wmic process where \"name='BoomBangLauncher.exe'\" get ProcessId,PageFileUsage /format:csv ^| sort /r /k 3') do @if not \"%k\"==\"\" (taskkill /f /pid %j >nul 2>&1)";

    ProcessBuilder pb1 = new ProcessBuilder();
    pb1.command("cmd.exe", "/c", complexCommand1);

    Process cleanupProcess1 = pb1.start();
    boolean finished1 = cleanupProcess1.waitFor(30, TimeUnit.SECONDS);

    if (!finished1) {
      cleanupProcess1.destroyForcibly();
    }

    // Procesar BoomBang.exe también
    Process listProcess2 = Runtime.getRuntime().exec(
        "wmic process where \"name='BoomBang.exe'\" get ProcessId,PageFileUsage"
    );
    listProcess2.waitFor();

    String complexCommand2 = "for /f \"skip=1 tokens=1,2\" %i in ('wmic process where \"name='BoomBang.exe'\" get ProcessId,PageFileUsage /format:csv ^| sort /r /k 3') do @if not \"%k\"==\"\" (taskkill /f /pid %j >nul 2>&1)";

    ProcessBuilder pb2 = new ProcessBuilder();
    pb2.command("cmd.exe", "/c", complexCommand2);

    Process cleanupProcess2 = pb2.start();
    boolean finished2 = cleanupProcess2.waitFor(30, TimeUnit.SECONDS);

    if (!finished2) {
      cleanupProcess2.destroyForcibly();
    }

    log.info("Duplicate processes cleaned up for both BoomBangLauncher.exe and BoomBang.exe");
  }

  private void setBoomBangHighPriority() throws IOException, InterruptedException {
    log.info("Setting BoomBang processes to high priority...");

    // Usar PowerShell directo (requiere que Java se ejecute como admin)
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

    // Usar PowerShell directo para cambiar afinidad a todos los procesos BoomBang
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

      // Flush DNS con timeout
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

      // TCP Auto-tuning con timeout
      ProcessBuilder pb2 = new ProcessBuilder();
      pb2.command("cmd.exe", "/c", "netsh int tcp set global autotuninglevel=normal");
      Process tcpOptimize = pb2.start();
      boolean finished2 = tcpOptimize.waitFor(15, TimeUnit.SECONDS);

      if (!finished2) {
        log.warn("TCP optimize timeout, terminating process");
        tcpOptimize.destroyForcibly();
      } else {
        log.info("TCP auto-tuning set to normal");
      }

      // Chimney offload con timeout
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

      // RSS (Receive Side Scaling) - mejora rendimiento en multi-core
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

      // NetDMA - optimiza transferencias de red
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

      // URO (UDP Receive Offload) - optimiza UDP para gaming
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

      // TCP Window Scaling - mejora throughput
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