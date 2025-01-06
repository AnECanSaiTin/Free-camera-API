package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.CameraModifierManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setAnglesInternal(FF)V", shift = At.Shift.AFTER))
    private void freecameraapi$cameraModifierSetup(float pPartialTicks, long pFinishTimeNano, PoseStack pPoseStack, CallbackInfo ci) {
        CameraModifierManager.modify();
    }
}
