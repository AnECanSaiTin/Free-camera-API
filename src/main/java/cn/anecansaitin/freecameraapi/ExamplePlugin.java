package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.core.ModifierPriority;

@CameraPlugin(value = "example", priority = ModifierPriority.LOWEST)
public class ExamplePlugin implements ICameraPlugin {
    private ICameraModifier modifier;

    @Override
    public void initialize(ICameraModifier modifier) {
        this.modifier = modifier;
        modifier.enable();
    }

    @Override
    public void update() {
        modifier
                .enableRotation()
                .setToVanilla()
                .rotateYXZ(0, 0, 180);
    }
}
