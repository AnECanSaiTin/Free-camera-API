package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.zoom.ZoomConfig;
import cn.anecansaitin.freecameraapi.zoom.ZoomPlugin;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "hurtTo", at = @At("HEAD"))
    public void hurtTo(float health, CallbackInfo ci) {
        if (!ZoomPlugin.enabled() || !ZoomConfig.Client.hurtExit()) {
            return;
        }

        ZoomPlugin.instance.disable();
    }
}
