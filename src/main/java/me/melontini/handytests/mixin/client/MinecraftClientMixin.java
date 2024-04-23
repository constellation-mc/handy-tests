package me.melontini.handytests.mixin.client;

import me.melontini.handytests.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 1200)
public class MinecraftClientMixin {
    @Inject(method = "method_29338", at = @At("TAIL"), require = 0)
    private void handy$init(CallbackInfo ci) {
        if (!Utils.ENABLED) return;

        MinecraftClient.getInstance().send(() -> {
            try {
                MinecraftClient client = MinecraftClient.getInstance();

                if (!client.getLevelStorage().levelExists("handy_tests_world")) {
                    client.createIntegratedServerLoader().createAndStart("handy_tests_world",
                            new LevelInfo("handy_tests_world", GameMode.CREATIVE, false, Difficulty.PEACEFUL, true,
                                    new GameRules(), DataConfiguration.SAFE_MODE),
                            new GeneratorOptions(0, true, false),
                            registryManager -> registryManager.get(RegistryKeys.WORLD_PRESET).entryOf(WorldPresets.FLAT).value().createDimensionsRegistryHolder());
                } else {
                    client.createIntegratedServerLoader().start(new TitleScreen(), "handy_tests_world");
                }
            } catch (Throwable t) {
                CrashReport report = CrashReport.create(t, "Setting tests world");
                MinecraftClient.printCrashReport(report);
            }
        });
    }
}
