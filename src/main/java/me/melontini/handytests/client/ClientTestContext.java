package me.melontini.handytests.client;

import me.melontini.handytests.util.TestContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.Perspective;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static me.melontini.handytests.client.FabricClientTestHelper.submit;

public record ClientTestContext(MinecraftClient client) implements TestContext<MinecraftClient> {

    public void sendCommand(String command) {
        FabricClientTestHelper.submitAndWait(client -> {
            client.player.sendCommand(command);
            return null;
        });
    }

    public void waitForLoadingComplete() {
        FabricClientTestHelper.waitForLoadingComplete();
    }

    public void waitForScreen(Class<? extends Screen> screenClass) {
        FabricClientTestHelper.waitForScreen(screenClass);
    }

    public <T, S extends Screen> T executeForScreen(Class<S> screenClass, BiFunction<MinecraftClient, S, T> function) {
        return FabricClientTestHelper.submitAndWait(client -> {
            if (screenClass.isInstance(client.currentScreen)) {
                return function.apply(client, screenClass.cast(client.currentScreen));
            }
            throw new IllegalStateException("Expected: %s, got: %s".formatted(screenClass.getName(), client.currentScreen != null ? client.currentScreen.getClass().getName() : "null"));
        });
    }

    public void openGameMenu() {
        FabricClientTestHelper.openGameMenu();
    }

    public void openInventory() {
        FabricClientTestHelper.openInventory();
    }

    public void closeScreen() {
        setScreen((client) -> null);
    }

    public void setScreen(Function<MinecraftClient, Screen> screenSupplier) {
        FabricClientTestHelper.setScreen(screenSupplier);
    }

    public void takeScreenshot(String name) {
        FabricClientTestHelper.takeScreenshot(name);
    }

    public void waitForWorldTicks(long ticks) {
        FabricClientTestHelper.waitForWorldTicks(ticks);
    }

    public void enableDebugHud() {
        FabricClientTestHelper.enableDebugHud();
    }

    public void setPerspective(Perspective perspective) {
        FabricClientTestHelper.setPerspective(perspective);
    }

    @Override
    public void waitFor(String what, Predicate<MinecraftClient> predicate, Duration timeout) {
        FabricClientTestHelper.waitFor(what, predicate, timeout);
    }

    public <T> T submitAndWait(Function<MinecraftClient, T> function) {
        return submit(function).join();
    }

    @Override
    public MinecraftClient context() {
        return client();
    }
}
