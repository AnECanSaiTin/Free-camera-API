package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.core.ModifierPriority;
import net.minecraft.client.Minecraft;

@CameraPlugin(value = "example", priority = ModifierPriority.LOWEST)
public class ExamplePlugin implements ICameraPlugin {
    private ICameraModifier modifier;

    @Override
    public void initialize(ICameraModifier modifier) {
        this.modifier = modifier;
        modifier.enable()
                .enablePos()
                .enableRotation()
                .enableGlobalMode()
                .enableChunkLoader();
    }

    @Override
    public void update() {
        modifier.setPos(-30, 64, -314).setRotationYXZ(90, 180, 0);
    }
}
