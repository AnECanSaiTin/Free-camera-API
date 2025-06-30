package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.api.ModifierPriority;

@CameraPlugin(value = "dev", priority = ModifierPriority.LOWEST)
public class ExamplePlugin implements ICameraPlugin {
    private ICameraModifier modifier;

    @Override
    public void initialize(ICameraModifier modifier) {
        this.modifier = modifier;
        modifier.disable()
                .enablePos()
                .enableRotation();
    }

    @Override
    public void update() {
        modifier
                .enable()
                .enableRotation()
                .enablePos()
                .setToVanilla()
                .addPos(0,0,-1);
    }
}
