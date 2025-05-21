package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.common.ICameraModifier;
import cn.anecansaitin.freecameraapi.starup.CameraPlugin;
import cn.anecansaitin.freecameraapi.starup.IPlugin;

@CameraPlugin(id = "test")
public class Test implements IPlugin {
    @Override
    public void update(ICameraModifier modifier) {

    }
}
