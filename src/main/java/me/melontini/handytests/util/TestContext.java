package me.melontini.handytests.util;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;
import me.melontini.handytests.util.runner.TestRunner;

public interface TestContext<C> {

  default void waitFor(String what, Predicate<C> predicate) {
    this.waitFor(what, predicate, Duration.ofSeconds(10));
  }

  void waitFor(String what, Predicate<C> predicate, Duration timeout);

  <T> T submitAndWait(Function<C, T> function);

  default void addLateCheck(String description, Runnable check) {
    Utils.addLateCheck(description, check);
  }

  default void runAllForEntrypoint(Object entryponit) {
    TestRunner.runTests(entryponit, this);
  }

  C context();
}
