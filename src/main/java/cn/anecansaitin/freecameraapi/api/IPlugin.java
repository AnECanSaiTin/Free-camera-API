package cn.anecansaitin.freecameraapi.api;

public interface IPlugin {
    void initialize(ICameraModifier modifier);
    void update();
}
