package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.ICameraModifier;
import cn.anecansaitin.freecameraapi.api.ICameraPlugin;

@CameraPlugin(value = "dev")
public class DevPlugin implements ICameraPlugin {
    private ICameraModifier modifier;

    @Override
    public void initialize(ICameraModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void update() {
        modifier.enable().enablePos().enableRotation()
                .setToVanilla()
                .addPos(0,1,0);
    }
}
