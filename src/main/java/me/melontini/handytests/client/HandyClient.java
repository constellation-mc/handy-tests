package me.melontini.handytests.client;

import com.mojang.logging.LogUtils;
import me.melontini.handytests.util.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class HandyClient implements ClientModInitializer {

  private static final Logger log = LogUtils.getLogger();

  @Override
  public void onInitializeClient() {
    if (!Utils.ENABLED) return;

    var thread = new Thread(() -> {
      try {
        log.info("Started client test.");
        ClientTestContext context = new ClientTestContext(MinecraftClient.getInstance());
        context.waitForWorldTicks(200);
        GLFW.glfwShowWindow(context.client().getWindow().getHandle());

        FabricLoader.getInstance()
            .invokeEntrypoints(
                "handy:client_test", ClientTestEntrypoint.class, e -> e.onClientTest(context));
        MixinEnvironment.getCurrentEnvironment().audit();

        Utils.runChecks();
        MinecraftClient.getInstance().scheduleStop();
      } catch (Throwable t) {
        log.error("Failed client test!", t);
        System.exit(1);
      }
    });
    thread.start();
  }
}
