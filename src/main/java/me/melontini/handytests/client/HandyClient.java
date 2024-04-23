package me.melontini.handytests.client;

import me.melontini.handytests.server.HandyServer;
import me.melontini.handytests.util.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class HandyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (!Utils.ENABLED) return;

        var thread = new Thread(() -> {
            try {
                HandyServer.LOGGER.info("Started client test.");
                ClientTestContext context = new ClientTestContext(MinecraftClient.getInstance());
                context.waitForWorldTicks(200);

                FabricLoader.getInstance().invokeEntrypoints("handy:client_test", ClientTestEntrypoint.class, e -> e.onClientTest(context));
                MixinEnvironment.getCurrentEnvironment().audit();

                MinecraftClient.getInstance().scheduleStop();
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        });
        thread.start();
    }
}
