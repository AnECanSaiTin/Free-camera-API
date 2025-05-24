package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.IPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.core.ModifierPriority;

@CameraPlugin(value = "test1", priority = ModifierPriority.HIGH)
public class Test implements IPlugin {
    private ICameraModifier modifier;
    private int c = 0;

    @Override
    public void initialize(ICameraModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void update() {
        modifier
                .enable()
                .enableRotation()
                .setToVanilla()
                .rotateYXZ(0, 0, c++);
    }
}
