package cn.anecansaitin.freecameraapi.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    private int lastCameraSectionX;
    @Shadow
    private int lastCameraSectionY;
    @Shadow
    private int lastCameraSectionZ;
    @Shadow
    private ViewArea viewArea;

    @Inject(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher;setCamera(Lnet/minecraft/world/phys/Vec3;)V"))
    public void freeCameraAPI$setupRender(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo ci, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j, @Local(ordinal = 2) int k) {
        SectionPos pos = viewArea.getCameraSectionPos();

        if (pos.getX() == i && pos.getY() == j && pos.getZ() == k) {
            return;
        }

        lastCameraSectionX = i;
        lastCameraSectionY = j;
        lastCameraSectionZ = k;
        viewArea.repositionCamera(SectionPos.of(camera.getPosition()));
    }
}