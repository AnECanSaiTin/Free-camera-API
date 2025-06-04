package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.ChunkTest;
import cn.anecansaitin.freecameraapi.core.attachment.CameraData;
import cn.anecansaitin.freecameraapi.core.attachment.ModAttachment;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class TrackedEntityMixin {
    @Shadow
    @Final
    Entity entity;

    @ModifyVariable(method = "updatePlayer", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 2))
    public boolean freeCameraAPI$modifyFlag(boolean original, ServerPlayer player, @Local(ordinal = 0) double viewDistance) {
        // 让相机范围内的实体能更新
        if (ChunkTest.INSTANCE.isInCamera()) {
            CameraData data = player.getData(ModAttachment.CAMERA_DATA);

            if (data.enable) {
                Vec3 relativePosToCamera = entity.position().subtract(data.x, data.y, data.z);

                if (relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && data.z <= viewDistance)
                    return true;
            }
        }

        return original;
    }
}