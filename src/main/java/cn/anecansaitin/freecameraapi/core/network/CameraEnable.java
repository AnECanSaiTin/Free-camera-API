package cn.anecansaitin.freecameraapi.core.network;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.core.ModifierManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CameraEnable() implements CustomPacketPayload {
    public static final Type<CameraEnable> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "camera_enable"));
    public static final StreamCodec<ByteBuf, CameraEnable> CODEC = StreamCodec.unit(new CameraEnable());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CameraEnable pack, IPayloadContext context) {
        ModifierManager.INSTANCE.updateStorage();
    }
}
