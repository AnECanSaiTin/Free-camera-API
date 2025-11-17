package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.core.ManagerTicker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getFov(Lnet/minecraft/client/Camera;FZ)F", ordinal = 0))
    public void renderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        ManagerTicker.tick();
    }
}