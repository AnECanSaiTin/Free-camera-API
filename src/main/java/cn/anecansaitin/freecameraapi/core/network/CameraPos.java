package cn.anecansaitin.freecameraapi.core.network;

import cn.anecansaitin.freecameraapi.FreeCamera;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CameraPos(float x, float y, float z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CameraPos> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "camera_pos"));
    public static final StreamCodec<ByteBuf, CameraPos> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, (pack) -> pack.x,
            ByteBufCodecs.FLOAT, (pack) -> pack.y,
            ByteBufCodecs.FLOAT, (pack) -> pack.z,
            CameraPos::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CameraPos pack, IPayloadContext context) {

    }
}
