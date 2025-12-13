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
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    public void free_camera_api$render(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        ManagerTicker.tick();
    }
}