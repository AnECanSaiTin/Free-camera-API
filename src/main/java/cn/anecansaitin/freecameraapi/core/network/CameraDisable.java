package cn.anecansaitin.freecameraapi.core.network;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.core.ModifierManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CameraDisable() implements CustomPacketPayload {
    public static final Type<CameraDisable> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "camera_disable"));
    public static final StreamCodec<ByteBuf, CameraDisable> CODEC = StreamCodec.unit(new CameraDisable());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CameraDisable pack, IPayloadContext context) {
        ModifierManager.INSTANCE.end();
    }
}
