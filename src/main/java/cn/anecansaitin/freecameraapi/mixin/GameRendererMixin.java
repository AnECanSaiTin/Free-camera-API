package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.common.CameraModifierManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "renderLevel",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
    private void freecameraapi$cameraModifierSetup(DeltaTracker deltaTracker, CallbackInfo ci) {
        CameraModifierManager.modify();
    }
}
