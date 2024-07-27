package me.melontini.handytests.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Utils {

  public static final boolean ENABLED = System.getProperty("handy-tests.auto-test") != null;

  private static final AtomicBoolean CRASHED = new AtomicBoolean();
  private static Set<CheckEntry> CHECKS = new LinkedHashSet<>();

  public static void runChecks() {
    if (!ENABLED) return;

    Set<CheckEntry> checks;

    synchronized (Utils.class) {
      checks = CHECKS;
      CHECKS = null;
    }

    for (CheckEntry check : checks) {
      try {
        check.check().run();
      } catch (Throwable throwable) {
        throw new IllegalStateException(
            "Failed late check: %s".formatted(check.description()), throwable);
      }
    }
  }

  public static synchronized void addLateCheck(String description, Runnable check) {
    if (CHECKS == null) throw new IllegalStateException("Game is shutting down!");
    CHECKS.add(new CheckEntry(description, check));
  }

  public static void markCrashed() {
    CRASHED.set(true);
  }

  record CheckEntry(String description, Runnable check) {}
}
