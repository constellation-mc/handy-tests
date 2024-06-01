package me.melontini.handytests.mixin.client;

import me.melontini.handytests.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
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
                    var imm = DynamicRegistryManager.createAndLoad().toImmutable();
                    var opt = imm.get(Registry.WORLD_PRESET_KEY).entryOf(WorldPresets.FLAT).value().createGeneratorOptions(0L, true, false);
                    client.createIntegratedServerLoader().createAndStart("handy_tests_world",
                            new LevelInfo("handy_tests_world", GameMode.CREATIVE, false, Difficulty.PEACEFUL, true,
                                    new GameRules(), DataPackSettings.SAFE_MODE), imm, opt);
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
