package cn.anecansaitin.freecameraapi.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {
    @Inject(method = "lambda$collectTickingChunks$3", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),  cancellable = true)
    private void freeCameraAPI$collectTickingChunks(List<LevelChunk> chunks, ChunkHolder holder, CallbackInfo ci, @Local LevelChunk levelchunk) {
        if (levelchunk == null) {
            ci.cancel();
        }
    }
}
