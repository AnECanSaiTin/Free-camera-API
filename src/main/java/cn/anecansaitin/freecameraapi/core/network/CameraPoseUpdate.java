package cn.anecansaitin.freecameraapi.core.network;

import cn.anecansaitin.freecameraapi.FreeCamera;
import cn.anecansaitin.freecameraapi.core.ModTicketController;
import cn.anecansaitin.freecameraapi.core.attachment.CameraData;
import cn.anecansaitin.freecameraapi.core.attachment.ModAttachment;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CameraPoseUpdate(boolean enable, boolean update, float x, float y, float z,
                               int radius) implements CustomPacketPayload {
    public static final Type<CameraPoseUpdate> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FreeCamera.MODID, "camera_pose_update"));
    public static final StreamCodec<ByteBuf, CameraPoseUpdate> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, (pack) -> pack.enable,
            ByteBufCodecs.BOOL, (pack) -> pack.update,
            ByteBufCodecs.FLOAT, (pack) -> pack.x,
            ByteBufCodecs.FLOAT, (pack) -> pack.y,
            ByteBufCodecs.FLOAT, (pack) -> pack.z,
            ByteBufCodecs.VAR_INT, (pack) -> pack.radius,
            CameraPoseUpdate::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CameraPoseUpdate pack, IPayloadContext context) {
        Player player = context.player();
        CameraData data = player.getData(ModAttachment.CAMERA_DATA);
        data.update(pack.enable, pack.update, pack.x, pack.y, pack.z, pack.radius);

        if (pack.enable) {
            if (data.currentView.x() != data.oldView.x() || data.currentView.z() != data.oldView.z()) {
                int currentX = data.currentView.x();
                int currentZ = data.currentView.z();
                int currentRadius = pack.radius;

                int currentMinX = currentX - currentRadius;
                int currentMaxX = currentX + currentRadius;
                int currentMinZ = currentZ - currentRadius;
                int currentMaxZ = currentZ + currentRadius;

                for (int x = currentMinX; x <= currentMaxX; x++) {
                    for (int z = currentMinZ; z <= currentMaxZ; z++) {
                        ModTicketController.addChunk((ServerLevel) player.level(), player, x, z);
                    }
                }
            }
        } else {
            ModTicketController.removeAllChunk(player.getUUID());
        }
    }
}
