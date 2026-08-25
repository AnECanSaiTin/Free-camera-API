package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.api.CameraModifier;
import cn.anecansaitin.freecameraapi.api.CameraPlugin;
import cn.anecansaitin.freecameraapi.api.Plugin;

//@Plugin(value = "dev", modid = "free_camera_api", modifier = "cn.anecansaitin.freecameraapi.core.Modifier")
public class DevPlugin implements CameraPlugin {
    private final CameraModifier modifier;

    public DevPlugin(CameraModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void update(float partialTicks) {
        modifier.enable().enablePos().enableRotation()
                .setToVanilla()
                .addPos(0,1,0);
    }
}
