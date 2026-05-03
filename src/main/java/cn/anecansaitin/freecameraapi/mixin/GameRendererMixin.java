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
    @Inject(method = "updateCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    public void free_camera_api$updateCamera(DeltaTracker deltaTracker, CallbackInfo ci) {
        ManagerTicker.tick();
    }
}