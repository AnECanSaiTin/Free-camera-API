package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.ChunkRangeTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public abstract class ClientChunkCacheMixin {
    @Inject(method = "inRange", at = @At("HEAD"), cancellable = true)
    public void freeCameraAPI$inRange(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        if (ChunkRangeTest.test(x, z)) {
            cir.setReturnValue(true);
        }
    }
}
