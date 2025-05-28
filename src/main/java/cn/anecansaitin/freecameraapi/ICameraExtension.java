package cn.anecansaitin.freecameraapi;

public interface ICameraExtension {
    default void setFov(float fov) {
    }

    default float getFov() {
        return 0;
    }
}
