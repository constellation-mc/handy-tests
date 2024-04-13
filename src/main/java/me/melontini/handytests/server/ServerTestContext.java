package me.melontini.handytests.server;


import net.minecraft.server.MinecraftServer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public record ServerTestContext(IntSupplier ticks, MinecraftServer server) {

    public void waitForOverworldTicks(int ticks) {
        waitFor("Overworld load", client -> server.getOverworld() != null, Duration.ofMinutes(30));
        final long startTicks = submitAndWait(client -> client.getOverworld().getTime());
        waitFor("Overworld load", client -> Objects.requireNonNull(client.getOverworld()).getTime() > startTicks + ticks, Duration.ofMinutes(10));
    }

    void waitFor(String what, Predicate<MinecraftServer> predicate) {
        waitFor(what, predicate, Duration.ofSeconds(10));
    }

    void waitFor(String what, Predicate<MinecraftServer> predicate, Duration timeout) {
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

    public <T> T submitAndWait(Function<MinecraftServer, T> function) {
        return submit(function).join();
    }
}
