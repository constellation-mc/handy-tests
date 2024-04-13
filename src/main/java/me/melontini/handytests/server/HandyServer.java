package me.melontini.handytests.server;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class HandyServer implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeServer() {
        if (System.getProperty("fabric-api.gametest") != null) {
            LOGGER.info("Skipping handy server test, as gametest is enabled!");
            return;
        }
        MutableInt ticks = new MutableInt(0);
        ServerTickEvents.END_SERVER_TICK.register(server -> ticks.add(1));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            var thread = new Thread(() -> {
                try {
                    HandyServer.LOGGER.info("Started server test.");
                    ServerTestContext context = new ServerTestContext(ticks::intValue, server);
                    context.waitForOverworldTicks(200);

                    FabricLoader.getInstance().invokeEntrypoints("handy:server_test", ServerTestEntrypoint.class, e -> e.onServerTest(context));
                    MixinEnvironment.getCurrentEnvironment().audit();

                    server.stop(false);
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.exit(1);
                }
            });
            thread.start();
        });
    }
}
