package cn.anecansaitin.freecameraapi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(value = ClientChunkCache.class)
public abstract class ClientChunkCacheMixin {
    @Unique
    private ClientChunkCache.Storage freeCameraAPI$cameraStorage;

    @Unique
    private ClientChunkCache self() {
        return (ClientChunkCache) (Object) this;
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void an$onInit(ClientLevel level, int viewDistance, CallbackInfo ci) {
        freeCameraAPI$cameraStorage = self().new Storage(Math.max(2, viewDistance) + 3);
    }

    @Inject(method = "updateViewRadius", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;<init>(Lnet/minecraft/client/multiplayer/ClientChunkCache;I)V"))
    public void freeCameraAPI$onUpdateViewRadius(int viewDistance, CallbackInfo ci) {
        // 可视范围更新时，同步更新相机的storage
        ClientChunkCache.Storage storage = self().new Storage(Math.max(2, viewDistance) + 3);

        storage.viewCenterX = freeCameraAPI$cameraStorage.viewCenterX;
        storage.viewCenterZ = freeCameraAPI$cameraStorage.viewCenterZ;

        for (int i = 0; i < freeCameraAPI$cameraStorage.chunks.length(); ++i) {
            LevelChunk chunk = freeCameraAPI$cameraStorage.chunks.get(i);

            if (chunk == null) {
                continue;
            }

            ChunkPos pos = chunk.getPos();

            if (!storage.inRange(pos.x, pos.z)) {
                continue;
            }

            storage.replace(storage.getIndex(pos.x, pos.z), chunk);
        }

        freeCameraAPI$cameraStorage = storage;
    }

    @Inject(method = "drop", at = @At(value = "HEAD"))
    public void freeCameraAPI$onDrop(ChunkPos pos, CallbackInfo ci) {
        // 丢弃相机范围内的区块
        if (!freeCameraAPI$cameraStorage.inRange(pos.x, pos.z)) {
            return;
        }

        int i = freeCameraAPI$cameraStorage.getIndex(pos.x, pos.z);
        LevelChunk chunk = freeCameraAPI$cameraStorage.getChunk(i);

        if (chunk == null || chunk.getPos().x != pos.x || chunk.getPos().z != pos.z) {
            return;
        }

        NeoForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
        freeCameraAPI$cameraStorage.replace(i, null);
    }

    @Inject(method = "replaceWithPacketData", at = @At(value = "HEAD"), cancellable = true)
    private void freeCameraAPI$onReplace(int x, int z, FriendlyByteBuf buffer, CompoundTag tag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> cir) {

    }

    @Inject(method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/LevelChunk;", at = @At("TAIL"), cancellable = true)
    private void freeCameraAPI$onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<LevelChunk> callback) {

    }
}