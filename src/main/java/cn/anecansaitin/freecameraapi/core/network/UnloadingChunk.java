package cn.anecansaitin.freecameraapi.core.network;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.core.ModTicketController;
import cn.anecansaitin.freecameraapi.core.attachment.CameraChunk;
import cn.anecansaitin.freecameraapi.core.attachment.ModAttachment;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Comparator;

public record UnloadingChunk() implements CustomPacketPayload {
    public static final Type<UnloadingChunk> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "unloading_chunk"));
    public static final StreamCodec<ByteBuf, UnloadingChunk> CODEC = StreamCodec.unit(new UnloadingChunk());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UnloadingChunk pack, IPayloadContext context) {
        Player player = context.player();
        player.setData(ModAttachment.CAMERA_CHUNK, new CameraChunk());
        ModTicketController.ticketHelper.removeAllTickets(player.getUUID());
    }
}
