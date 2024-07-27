package me.melontini.handytests.mixin.server;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.melontini.handytests.util.Utils;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Main.class)
public class ServerMainMixin {
  @ModifyExpressionValue(
      method = "main",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/server/dedicated/EulaReader;isEulaAgreedTo()Z"))
  private static boolean isEulaAgreedTo(boolean original) {
    return original || Utils.ENABLED;
  }
}
