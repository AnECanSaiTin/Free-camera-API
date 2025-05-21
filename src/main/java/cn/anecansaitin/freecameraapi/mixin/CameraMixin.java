package cn.anecansaitin.freecameraapi.mixin;

import cn.anecansaitin.freecameraapi.ICameraExtend;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Camera.class)
public abstract class CameraMixin implements ICameraExtend {
    @Unique
    private float freeCameraAPI$fov;

    @Unique
    @Override
    public void setFov(float fov) {
        freeCameraAPI$fov = fov;
    }

    @Unique
    @Override
    public float getFov() {
        return freeCameraAPI$fov;
    }
}
