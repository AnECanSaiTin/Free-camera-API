package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.CameraExtension;
import cn.anecansaitin.freecameraapi.core.ManagerTicker;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExtension {
    @Shadow
    private float fov;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;prepareCullFrustum(Lorg/joml/Matrix4fc;Lorg/joml/Matrix4f;Lnet/minecraft/world/phys/Vec3;)V"))
    public void free_camera_api$update(DeltaTracker deltaTracker, CallbackInfo ci, @Local(name = "partialTicks") float partialTicks) {
        ManagerTicker.update(partialTicks);
    }

    @Override
    public void setFov(float fov) {
        this.fov = fov;
    }
}
