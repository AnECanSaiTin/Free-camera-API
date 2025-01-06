package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.ICameraMixinExtend;
import net.minecraft.client.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class CameraMixin implements ICameraMixinExtend {
    @Shadow private float xRot;

    @Shadow private float yRot;

    @Shadow @Final private Quaternionf rotation;

    @Shadow @Final private Vector3f forwards;

    @Shadow @Final private Vector3f up;

    @Shadow @Final private Vector3f left;

    @Override
    public void setRotation(float x, float y, float z) {
        this.xRot = x;
        this.yRot = y;
        this.rotation.rotationYXZ(-y * ((float)Math.PI / 180F), x * ((float)Math.PI / 180F), z * ((float)Math.PI / 180F));
        this.forwards.set(0.0F, 0.0F, 1.0F).rotate(this.rotation);
        this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotation);
        this.left.set(1.0F, 0.0F, 0.0F).rotate(this.rotation);
    }
}
