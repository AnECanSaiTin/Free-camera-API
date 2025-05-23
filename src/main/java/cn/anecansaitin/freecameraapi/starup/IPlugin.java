package cn.anecansaitin.freecameraapi.starup;

import cn.anecansaitin.freecameraapi.common.ICameraModifier;

public interface IPlugin {
    void initialize(ICameraModifier modifier);
    void update();
}
