package cn.anecansaitin.freecameraapi.mixin;

public interface ICameraExtend {
    default void setFov(float fov) {
    };
    default float getFov() {
        return 0;
    };
}
