package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.ChunkTest;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(Lnet/minecraft/core/SectionPos;)V"))
    public void freeCameraAPI$setupRender(ViewArea instance, SectionPos pos) {
        if (ChunkTest.INSTANCE.inCamera) {
            return;
        }

        instance.repositionCamera(pos);
    }
}