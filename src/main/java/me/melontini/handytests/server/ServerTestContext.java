package me.melontini.handytests.server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import me.melontini.handytests.util.TestContext;
import net.minecraft.server.MinecraftServer;

public record ServerTestContext(MinecraftServer server) implements TestContext<MinecraftServer> {

  public void sendCommand(String command) {
    submitAndWait(
        server -> {
            server.getCommandManager().executeWithPrefix(server.getCommandSource(), command);
            return null;
        });
    }

  public void waitForOverworldTicks(long ticks) {
    waitFor("Overworld load", server -> server.getOverworld() != null, Duration.ofMinutes(30));
    final long startTicks = submitAndWait(server -> server.getOverworld().getTime());
    waitFor(
        "Overworld load",
        server -> Objects.requireNonNull(server.getOverworld()).getTime() > startTicks + ticks,
        Duration.ofMinutes(10));
  }

  public void waitFor(String what, Predicate<MinecraftServer> predicate, Duration timeout) {
    final LocalDateTime end = LocalDateTime.now().plus(timeout);

    while (true) {
      boolean result = submitAndWait(predicate::test);

      if (result) {
        break;
      }

      if (LocalDateTime.now().isAfter(end)) {
        throw new RuntimeException("Timed out waiting for " + what);
      }

      waitFor(Duration.ofSeconds(1));
    }
  }

  public <T> T submitAndWait(Function<MinecraftServer, T> function) {
    return submit(function).join();
  }

  @Override
  public MinecraftServer context() {
    return server();
  }

  static void waitFor(Duration duration) {
    try {
      Thread.sleep(duration.toMillis());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  <T> CompletableFuture<T> submit(Function<MinecraftServer, T> function) {
    return server.submit(() -> function.apply(server));
  }
}
