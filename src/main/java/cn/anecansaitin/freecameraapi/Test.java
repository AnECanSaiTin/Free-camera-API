package cn.anecansaitin.freecameraapi;

import cn.anecansaitin.freecameraapi.common.ICameraModifier;
import cn.anecansaitin.freecameraapi.common.ModifierPriority;
import cn.anecansaitin.freecameraapi.starup.CameraPlugin;
import cn.anecansaitin.freecameraapi.starup.IPlugin;

@CameraPlugin(id = "test1", priority = ModifierPriority.HIGH)
public class Test implements IPlugin {
    @Override
    public void update(ICameraModifier modifier) {

    }
}
