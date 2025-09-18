package es.zed.shared.domain.utils;

import java.util.concurrent.locks.LockSupport;
import org.springframework.stereotype.Component;

@Component
public class PreciseTimingUtils {

  public void preciseSleep(long nanos) throws InterruptedException {
    if (nanos <= 0)
      return;

    long startTime = System.nanoTime();
    long endTime = startTime + nanos;

    if (nanos > 2_000_000) {
      long sleepMs = (nanos - 1_000_000) / 1_000_000;
      Thread.sleep(sleepMs);
    }

    long remaining = endTime - System.nanoTime();
    if (remaining > 100_000) {
      LockSupport.parkNanos(remaining - 50_000);
    }

    while (System.nanoTime() < endTime) {
      if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException();
      }
      if ((System.nanoTime() - startTime) % 1000 == 0) {
        Thread.yield();
      }
    }
  }

  public void preciseSleepMicros(long micros) throws InterruptedException {
    preciseSleep(micros * 1_000);
  }

  public void preciseSleepMilis(long millis) throws InterruptedException {
    preciseSleep(millis * 1_000_000);
  }

  public void parkSleep(long nanos) throws InterruptedException {
    if (nanos <= 0) return;

    long deadline = System.nanoTime() + nanos;

    while (System.nanoTime() < deadline) {
      long remaining = deadline - System.nanoTime();
      if (remaining <= 0) break;

      if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException();
      }

      LockSupport.parkNanos(remaining);
    }
  }
}