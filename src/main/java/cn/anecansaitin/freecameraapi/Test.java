package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.common.ICameraModifier;
import cn.anecansaitin.freecameraapi.common.ModifierPriority;
import cn.anecansaitin.freecameraapi.starup.CameraPlugin;
import cn.anecansaitin.freecameraapi.starup.IPlugin;

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
