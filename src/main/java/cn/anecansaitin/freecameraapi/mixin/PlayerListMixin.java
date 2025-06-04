package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.core.attachment.CameraData;
import cn.anecansaitin.freecameraapi.core.attachment.ModAttachment;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "broadcast", at = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 0))
    public void freeCameraAPI$broadcast(Player except, double x, double y, double z, double radius, ResourceKey<Level> dimension, Packet<?> packet, CallbackInfo ci, @Local ServerPlayer player) {
        // 根据相机的范围来决定是否发包
        if (player == except || player.level().dimension() != dimension) {
            return;
        }

        CameraData data = player.getData(ModAttachment.CAMERA_DATA);

        if (!data.enable) {
            return;
        }

        float cx = data.x,
                cy = data.y,
                cz = data.z;

        if (cx * cx + cy * cy + cz * cz > radius * radius) {
            return;
        }

        player.connection.send(packet);
    }
}
