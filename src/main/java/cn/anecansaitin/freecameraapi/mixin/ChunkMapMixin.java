package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.core.attachment.CameraData;
import cn.anecansaitin.freecameraapi.core.attachment.ModAttachment;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    @Shadow
    protected abstract void markChunkPendingToSend(ServerPlayer player, ChunkPos pos);

    @Inject(method = "updateChunkTracking", at = @At(value = "HEAD"))
    private void freeCameraAPI$onUpdateChunkTracking(ServerPlayer player, CallbackInfo ci) {
        // 发送相机附近的区块信息到客户端
        CameraData data = player.getData(ModAttachment.CAMERA_DATA);

        if (!data.enable) {
            if (data.update) {
                // 玩家退出相机视角并恢复到正常视角后，再次发送玩家周围方块信息，避免区块渲染缺失
                player.getChunkTrackingView().forEach(chunkPos -> markChunkPendingToSend(player, chunkPos));
                data.update = false;
            }

            return;
        }

        if (!data.update) {
            return;
        }

        ChunkTrackingView.difference(data.oldView, data.currentView, chunkPos -> markChunkPendingToSend(player, chunkPos), chunkPos -> {});
        data.update = false;
    }

    @ModifyExpressionValue(method = "onChunkReadyToSend", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkTrackingView;contains(Lnet/minecraft/world/level/ChunkPos;)Z"))
    public boolean freeCameraAPI$onChunkReadyToSend(boolean original, @Local ChunkPos pos, @Local ServerPlayer player) {
        // 让相机区块能通过可视范围检测
        if (original) {
            return true;
        }

        CameraData data = player.getData(ModAttachment.CAMERA_DATA);

        if (!data.enable) {
            return false;
        }

        return data.currentView.contains(pos);
    }

    @Inject(method = "isChunkTracked", at = @At("HEAD"), cancellable = true)
    private void freeCameraAPI$onIsChunkTracked(ServerPlayer player, int x, int z, CallbackInfoReturnable<Boolean> cir) {
        // 让相机范围内区块保持更新
        CameraData data = player.getData(ModAttachment.CAMERA_DATA);

        if (!data.enable || !data.currentView.contains(x, z)) {
            return;
        }

        if (!player.connection.chunkSender.isPending(ChunkPos.asLong(x, z))) {
            cir.setReturnValue(true);
        }
    }
}
