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

public record LoadingChunk(ChunkPos pos) implements CustomPacketPayload {
    public static final Type<LoadingChunk> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "loading_chunk"));
    public static final StreamCodec<ByteBuf, LoadingChunk> CODEC = StreamCodec.composite(ChunkPos.STREAM_CODEC, (pack) -> pack.pos, LoadingChunk::new);
//    private static final TicketType<ChunkPos> CAMERA_TICKET = TicketType.create(FreeCamera.MODID + ":camera", Comparator.comparingLong(ChunkPos::toLong), 20 * 60);

    public LoadingChunk(BlockPos pos) {
        this(new ChunkPos(pos));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LoadingChunk pack, IPayloadContext context) {
        Player player = context.player();
        ServerLevel level = (ServerLevel) player.level();
//        player.setData(ModAttachment.CAMERA_CHUNK, new CameraChunk(true, pack.pos.x, pack.pos.z));
        ModTicketController.TICKET_CONTROLLER.forceChunk(level, player, pack.pos.x, pack.pos.z, true, true);
    }
}
